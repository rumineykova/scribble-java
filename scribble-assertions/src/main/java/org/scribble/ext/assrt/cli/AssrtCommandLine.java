package org.scribble.ext.assrt.cli;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ast.Module;
import org.scribble.ast.global.GProtoDecl;
import org.scribble.cli.CLFlags;
import org.scribble.cli.CommandLine;
import org.scribble.cli.CommandLineException;
import org.scribble.core.job.CoreArgs;
import org.scribble.core.model.endpoint.EGraph;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGProtocolDeclTranslator;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEGraphBuilder;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModel;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelBuilder;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSafetyErrors;
import org.scribble.ext.assrt.core.model.stp.AssrtStpEState;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.core.type.session.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGType;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLType;
import org.scribble.ext.assrt.main.AssrtException;
import org.scribble.ext.assrt.main.AssrtJob;
import org.scribble.ext.assrt.main.AssrtJob.Solver;
import org.scribble.ext.assrt.main.AssrtMain;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;
import org.scribble.job.Job;
import org.scribble.main.Main;
import org.scribble.main.resource.locator.DirectoryResourceLocator;
import org.scribble.main.resource.locator.ResourceLocator;
import org.scribble.util.AntlrSourceException;
import org.scribble.util.ScribException;
import org.scribble.util.ScribParserException;

// Includes assrt-core functionality (all extra args are currently for assrt-core)
public class AssrtCommandLine extends CommandLine
{
	public AssrtCommandLine(String... args)
	{
		super(args);
	}

	public static void main(String[] args)
			throws CommandLineException, AntlrSourceException
	{
		new AssrtCommandLine(args).run();
	}

	@Override
	protected AssrtCoreCLFlags newCLFlags()
	{
		return new AssrtCoreCLFlags();
	}
	
	// Based on CommandLine.newMainContext
	@Override
	protected Main newMain() throws ScribParserException, ScribException
	{
		Map<CoreArgs, Boolean> args = Collections.unmodifiableMap(newCoreArgs());
		List<Path> impaths = parseImportPaths();
		ResourceLocator locator = new DirectoryResourceLocator(impaths);
		Path mainpath = parseMainPath();
		
		// FIXME: if no -assrt, then just do super.newMain
			
		Solver solver = this.assrtCoreArgs.containsKey(AssrtCoreCLFlags.ASSRT_CORE_NATIVE_Z3)
				? AssrtJob.Solver.NATIVE_Z3
				: AssrtJob.Solver.JAVA_SMT_Z3;  // Default for base assrt -- though base assrt doesn't actually check the solver flag
			
			return new AssrtMain(locator, mainpath, args, solver);
		}
	}

	@Override
	protected Map<AssrtCoreArgs, Boolean> newCoreArgs()  // FIXME: Set
	{
		Map<CoreArgs, Boolean> args = super.newCoreArgs();

		args.put(CoreArgs.VERBOSE, hasFlag(CLFlags.VERBOSE_FLAG));

		boolean assrtBatching = this.assrtCoreArgs.containsKey(AssrtCoreCLFlags.ASSRT_CORE_BATCHING);

		return args;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	protected void runValidationTasks(Job job) throws AssrtCoreSyntaxException, AntlrSourceException, ScribParserException, CommandLineException
	{
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLFlags.ASSRT_CORE))  // assrt-*core* mode
		{

			doAssrtCoreValidationTasks((AssrtJob) job);
		}
		else
		{
			super.runValidationTasks(job);
		}
	}

	private void doAssrtCoreValidationTasks(AssrtJob j) throws AssrtCoreSyntaxException, ScribException, ScribParserException, CommandLineException
	{
		/*if (this.args.containsKey(CLArgFlag.PROJECT))  // HACK
			// modules/f17/src/test/scrib/demo/fase17/AppD.scr in [default] mode bug --- projection/EFSM not properly formed if this if is commented ????
		{

		}*/

		assrtCorePreContextBuilding(j);

		GProtoName simpname = new GProtoName(this.assrtCoreArgs.get(AssrtCoreCLFlags.ASSRT_CORE)[0]);
		if (simpname.toString().equals("[AssrtCoreAllTest]"))  // HACK: AssrtCoreAllTest
		{
			assrtCoreParseAndCheckWF(j);  // Includes base passes
		}
		else
		{
			assrtCoreParseAndCheckWF(j, simpname);  // Includes base passes
		}
		
		// FIXME? assrt-core FSM building only used for assrt-core validation -- output tasks, e.g., -api, will still use default Scribble FSMs
		// -- but the FSMs should be the same? -- no: action assertions treated differently in core than base
	}

	
	// Refactor into Assrt(Core)Job?
	// Following methods are for assrt-*core*
	
	private void assrtCorePreContextBuilding(AssrtJob job) throws ScribException
	{
		job.runContextBuildingPasses();

		//job.runVisitorPassOnParsedModules(RecRemover.class);  // FIXME: Integrate into main passes?  Do before unfolding? 
				// FIXME: no -- revise to support annots
	}

	// Pre: assrtPreContextBuilding(job)
	private void assrtCoreParseAndCheckWF(AssrtJob job) throws AssrtCoreSyntaxException, ScribException, ScribParserException, CommandLineException
	{
		Module main = job.getContext().getMainModule();
		for (GProtoDecl gpd : main.getGlobalProtocolDecls())
		{
			if (!gpd.isAuxModifier())
			{
				assrtCoreParseAndCheckWF(job, gpd.getHeader().getDeclName());  // decl name is simple name
			}
		}
	}

	// Pre: assrtPreContextBuilding(job)
	private void assrtCoreParseAndCheckWF(AssrtJob job, GProtoName simpname) throws AssrtCoreSyntaxException, ScribException, ScribParserException, CommandLineException
	{
		Module main = job.getContext().getMainModule();
		if (!main.hasProtocolDecl(simpname))
		{
			throw new AssrtException("[assrt-core] Global protocol not found: " + simpname);
		}
		this.gpd = (GProtoDecl) main.getProtocolDecl(simpname);

		AssrtCoreAstFactory af = new AssrtCoreAstFactory();
		AssrtCoreGType gt = new AssrtCoreGProtocolDeclTranslator(job, af).translate(this.gpd);
		
		/*..HERE FIXME: need to add global assrt rec/continue and fix global inlining -- below steps use only the inlined *global*
		CHECKME: base assrt "works" because projected local proto decl does keep the assertion, and inlining of local, which does handle the assertions (AssrtLProjectionDeclDel), is done from the "base" protocol decl(s) -- i.e., not from the inlined global (CHECKME?)
		(in base, inlining of global is only for global level (syntactic) checks -- model checking is done from the separately inlined locals -- inlined global is also for "extensions" like this one and f17)
		-- does inlining->projection give the same as "base projection"->inlining?*/
		
		job.verbosePrintln("\n[assrt-core] Translated:\n  " + gt);
		
		List<AssrtDataTypeVar> adts = gt.collectAnnotDataTypeVarDecls().stream().map(v -> v.var).collect(Collectors.toList());
		job.verbosePrintln("\n[assrt-core] Collected data type annotation var decls: " + adts);
		Set<AssrtDataTypeVar> dups = adts.stream().filter(i -> Collections.frequency(adts, i) > 1)
				.collect(Collectors.toSet());	
		if (dups.size() > 0)
		{
			throw new AssrtCoreSyntaxException("[assrt-core] Repeat data type annotation variable declarations not allowed: " + dups);
		}

		for (Role r : gpd.header.roledecls.getRoles())
		{
			AssrtCoreLType lt = gt.project(af, r, AssrtTrueFormula.TRUE);
			P0.put(r, lt);

			job.verbosePrintln("\n[assrt-core] Projected onto " + r + ":\n  " + lt);
		}

		AssrtCoreEGraphBuilder builder = new AssrtCoreEGraphBuilder(job);
		this.E0 = new HashMap<>();
		for (Role r : P0.keySet())
		{
			EGraph g = builder.build(P0.get(r));
			this.E0.put(r, (AssrtEState) g.init);

			
			job.verbosePrintln("\n[assrt-core] Built endpoint graph for " + r + ":\n" + g.toDot());
		}
				
		assrtCoreValidate(job, simpname, gpd.isExplicitModifier());//, this.E0);  // TODO

		/*if (!job.fair)
		{
			Map<Role, EState> U0 = new HashMap<>();
			for (Role r : E0.keySet())
			{
				EState u = E0.get(r).unfairTransform();
				U0.put(r, u);

				job.verbosePrintln
				//System.out.println
						("\n[assrt-core] Unfair transform for " + r + ":\n" + u.toDot());
			}
			
			//validate(job, gpd.isExplicitModifier(), U0, true);  //TODO
		}*/
		
		//((AssrtJob) job).runF17ProjectionPasses();  // projections not built on demand; cf. models

		//return gt;
	}
		
	// HACK: store in (Core) Job/JobContext?
	protected GProtoDecl gpd;
	protected Map<Role, AssrtCoreLType> P0 = new HashMap<>();
	protected Map<Role, AssrtEState> E0;  // There is no core version
	protected AssrtCoreSModel model;

	// FIXME: factor out -- cf. super.doAttemptableOutputTasks
	@Override
	protected void tryOutputTasks(Job job) throws CommandLineException, ScribException
	{
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLFlags.ASSRT_CORE_PROJECT))
		{
			String[] args = this.assrtCoreArgs.get(AssrtCoreCLFlags.ASSRT_CORE_PROJECT);
			for (int i = 0; i < args.length; i += 1)
			{
				Role role = CommandLine.checkRoleArg(job.getContext(), gpd.getHeader().getDeclName(), args[i]);
				String out = P0.get(role).toString();
				System.out.println("\n" + out);  // Endpoint graphs are "inlined" (a single graph is built)
			}
		}
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLFlags.ASSRT_CORE_EFSM))
		{
			String[] args = this.assrtCoreArgs.get(AssrtCoreCLFlags.ASSRT_CORE_EFSM);
			for (int i = 0; i < args.length; i += 1)
			{
				Role role = CommandLine.checkRoleArg(job.getContext(), gpd.getHeader().getDeclName(), args[i]);
				String out = E0.get(role).toDot();
				System.out.println("\n" + out);  // Endpoint graphs are "inlined" (a single graph is built)
			}
		}
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLFlags.ASSRT_CORE_EFSM_PNG))
		{
			String[] args = this.assrtCoreArgs.get(AssrtCoreCLFlags.ASSRT_CORE_EFSM_PNG);
			for (int i = 0; i < args.length; i += 2)
			{
				Role role = CommandLine.checkRoleArg(job.getContext(), gpd.getHeader().getDeclName(), args[i]);
				String png = args[i+1];
				String out = E0.get(role).toDot();
				runDot(out, png);
			}
		}
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLFlags.ASSRT_CORE_MODEL))
		{
			System.out.println("\n" + model.toDot());
		}
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLFlags.ASSRT_CORE_MODEL_PNG))
		{
			String[] arg = this.assrtCoreArgs.get(AssrtCoreCLFlags.ASSRT_CORE_MODEL_PNG);
			String png = arg[0];
			runDot(model.toDot(), png);
		}

		if (this.assrtCoreArgs.containsKey(AssrtCoreCLFlags.ASSRT_STP_EFSM))
		{
			String[] args = this.assrtCoreArgs.get(AssrtCoreCLFlags.ASSRT_STP_EFSM);
			for (int i = 0; i < args.length; i += 1)
			{
				Role role = CommandLine.checkRoleArg(job.getContext(), gpd.getHeader().getDeclName(), args[i]);
				AssrtStpEState init = AssrtStpEState.from((AssrtCoreEModelFactory) job.ef, E0.get(role));
				String out = init.toDot();
				System.out.println("\n" + out);  // Endpoint graphs are "inlined" (a single graph is built)
			}
		}
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLFlags.ASSRT_CORE_EFSM_PNG))
		{
			String[] args = this.assrtCoreArgs.get(AssrtCoreCLFlags.ASSRT_CORE_EFSM_PNG);
			for (int i = 0; i < args.length; i += 2)
			{
				Role role = CommandLine.checkRoleArg(job.getContext(), gpd.getHeader().getDeclName(), args[i]);
				String png = args[i+1];
				AssrtStpEState init = AssrtStpEState.from((AssrtCoreEModelFactory) job.ef, E0.get(role));
				String out = init.toDot();
				runDot(out, png);
			}
		}
	}

	private void assrtCoreValidate(Job job, GProtoName simpname, boolean isExplicit, 
			//Map<Role, AssrtEState> E0,
			boolean... unfair) throws ScribException, CommandLineException
	{
		this.model = new AssrtCoreSModelBuilder(job.sf).build(this.E0, isExplicit);

		job.verbosePrintln("\n[assrt-core] Built model:\n" + this.model.toDot());
		
		if (unfair.length == 0 || !unfair[0])
		{
			AssrtCoreSafetyErrors serrs = this.model.getSafetyErrors(job, simpname);  // job just for debug printing
			if (serrs.isSafe())
			{
				job.verbosePrintln("\n[assrt-core] Protocol safe.");
			}
			else
			{
				throw new AssrtException("[assrt-core] Protocol not safe:\n" + serrs);
			}
		}
		
		/*F17ProgressErrors perrs = m.getProgressErrors();
		if (perrs.satisfiesProgress())
		{
			job.verbosePrintln
			//System.out.println
					("\n[f17] " + ((unfair.length == 0) ? "Fair protocol" : "Protocol") + " satisfies progress.");
		}
		else
		{

			// FIXME: refactor eventual reception as 1-bounded stable check
			Set<F17SState> staberrs = m.getStableErrors();
			if (perrs.eventualReception.isEmpty())
			{
				if (!staberrs.isEmpty())
				{
					throw new RuntimeException("[f17] 1-stable check failure: " + staberrs);
				}
			}
			else
			{
				if (staberrs.isEmpty())
				{
					throw new RuntimeException("[f17] 1-stable check failure: " + perrs);
				}
			}
			
			throw new F17Exception("\n[f17] " + ((unfair.length == 0) ? "Fair protocol" : "Protocol") + " violates progress.\n" + perrs);
		}*/
	}
}

package org.scribble.ext.assrt.cli;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ast.Module;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.cli.CLArgFlag;
import org.scribble.cli.CommandLine;
import org.scribble.cli.CommandLineException;
import org.scribble.ext.assrt.core.ast.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.ast.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGProtocolDeclTranslator;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGType;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEGraphBuilder;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModel;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelBuilder;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSafetyErrors;
import org.scribble.ext.assrt.core.model.stp.AssrtStpEState;
import org.scribble.ext.assrt.main.AssrtException;
import org.scribble.ext.assrt.main.AssrtJob;
import org.scribble.ext.assrt.main.AssrtJob.Solver;
import org.scribble.ext.assrt.main.AssrtMainContext;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.main.AntlrSourceException;
import org.scribble.main.Job;
import org.scribble.main.ScribbleException;
import org.scribble.main.resource.DirectoryResourceLocator;
import org.scribble.main.resource.ResourceLocator;
import org.scribble.model.endpoint.EGraph;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;
import org.scribble.util.ScribParserException;

// Includes assrt-core functionality (all extra args are currently for assrt-core)
public class AssrtCommandLine extends CommandLine
{

	private final Map<AssrtCoreCLArgFlag, String[]> assrtCoreArgs;  // Maps each flag to list of associated argument values
	
	public AssrtCommandLine(String... args) throws CommandLineException
	{
		this(new AssrtCoreCLArgParser(args));
	}
	
	private AssrtCommandLine(AssrtCoreCLArgParser p) throws CommandLineException
	{
		//super(p);  // calls p.parse()

		super(time(p, 0));  // calls p.parse()

		if (this.args.containsKey(CLArgFlag.INLINE_MAIN_MOD))
		{
			// FIXME: should be fine
			throw new RuntimeException("[assrt] Inline modules not supported:\n" + this.args.get(CLArgFlag.INLINE_MAIN_MOD));
		}
		// FIXME? Duplicated from core
		if (!this.args.containsKey(CLArgFlag.MAIN_MOD))
		{
			throw new CommandLineException("No main module has been specified\r\n");
		}
		
		time(null, 1);

		this.assrtCoreArgs = p.getAssrtArgs();
	}
	
	// Based on CommandLine.newMainContext
	@Override
	protected AssrtMainContext newMainContext() throws ScribParserException, ScribbleException
	{
		boolean debug = this.args.containsKey(CLArgFlag.VERBOSE);  // TODO: factor out with CommandLine (cf. MainContext fields)
		boolean useOldWF = this.args.containsKey(CLArgFlag.OLD_WF);
		boolean noLiveness = this.args.containsKey(CLArgFlag.NO_LIVENESS);
		boolean minEfsm = this.args.containsKey(CLArgFlag.LTSCONVERT_MIN);
		boolean fair = this.args.containsKey(CLArgFlag.FAIR);
		boolean noLocalChoiceSubjectCheck = this.args.containsKey(CLArgFlag.NO_LOCAL_CHOICE_SUBJECT_CHECK);
		boolean noAcceptCorrelationCheck = this.args.containsKey(CLArgFlag.NO_ACCEPT_CORRELATION_CHECK);
		boolean noValidation = this.args.containsKey(CLArgFlag.NO_VALIDATION);

		boolean assrtBatching = this.assrtCoreArgs.containsKey(AssrtCoreCLArgFlag.ASSRT_CORE_BATCHING);
		Solver solver = this.assrtCoreArgs.containsKey(AssrtCoreCLArgFlag.ASSRT_CORE_NATIVE_Z3)
				? AssrtJob.Solver.NATIVE_Z3
				: AssrtJob.Solver.JAVA_SMT_Z3;  // Default for base assrt -- though base assrt doesn't actually check the solver flag

		List<Path> impaths = this.args.containsKey(CLArgFlag.IMPORT_PATH)
				? CommandLine.parseImportPaths(this.args.get(CLArgFlag.IMPORT_PATH)[0])
				: Collections.emptyList();
		ResourceLocator locator = new DirectoryResourceLocator(impaths);
		if (this.args.containsKey(CLArgFlag.INLINE_MAIN_MOD))
		{
			/*return new AssrtMainContext(debug, locator, this.args.get(CLArgFlag.INLINE_MAIN_MOD)[0], useOldWF, noLiveness, minEfsm, fair,
					noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation, solver);*/
			throw new RuntimeException("[assrt] Shouldn't get in here:\n" + this.args.get(CLArgFlag.INLINE_MAIN_MOD)[0]);  // Checked in constructor
		}
		else
		{
			Path mainpath = CommandLine.parseMainPath(this.args.get(CLArgFlag.MAIN_MOD)[0]);
			return new AssrtMainContext(debug, locator, mainpath, useOldWF, noLiveness, minEfsm, fair,
					noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation, solver, assrtBatching);
		}
	}

	public static void main(String[] args) throws CommandLineException, AntlrSourceException
	{
		new AssrtCommandLine(args).run();
	}

	@Override
	protected void doValidationTasks(Job job) throws AssrtCoreSyntaxException, AntlrSourceException, ScribParserException, CommandLineException
	{
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLArgFlag.ASSRT_CORE))  // assrt-*core* mode
		{
			time(null, 2);

			doAssrtCoreValidationTasks((AssrtJob) job);
		}
		else
		{
			super.doValidationTasks(job);
		}
	}

	private void doAssrtCoreValidationTasks(AssrtJob j) throws AssrtCoreSyntaxException, ScribbleException, ScribParserException, CommandLineException
	{
		/*if (this.args.containsKey(CLArgFlag.PROJECT))  // HACK
			// modules/f17/src/test/scrib/demo/fase17/AppD.scr in [default] mode bug --- projection/EFSM not properly formed if this if is commented ????
		{

		}*/

		time(null, 3);
		
		assrtCorePreContextBuilding(j);

		time(null, 4);

		GProtocolName simpname = new GProtocolName(this.assrtCoreArgs.get(AssrtCoreCLArgFlag.ASSRT_CORE)[0]);
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
	
	private void assrtCorePreContextBuilding(AssrtJob job) throws ScribbleException
	{
		job.runContextBuildingPasses();

		//job.runVisitorPassOnParsedModules(RecRemover.class);  // FIXME: Integrate into main passes?  Do before unfolding? 
				// FIXME: no -- revise to support annots
	}

	// Pre: assrtPreContextBuilding(job)
	private void assrtCoreParseAndCheckWF(AssrtJob job) throws AssrtCoreSyntaxException, ScribbleException, ScribParserException, CommandLineException
	{
		Module main = job.getContext().getMainModule();
		for (GProtocolDecl gpd : main.getGlobalProtocolDecls())
		{
			if (!gpd.isAuxModifier())
			{
				assrtCoreParseAndCheckWF(job, gpd.getHeader().getDeclName());  // decl name is simple name
			}
		}
	}

	// Pre: assrtPreContextBuilding(job)
	private void assrtCoreParseAndCheckWF(AssrtJob job, GProtocolName simpname) throws AssrtCoreSyntaxException, ScribbleException, ScribParserException, CommandLineException
	{
		time(null, 5);

		Module main = job.getContext().getMainModule();
		if (!main.hasProtocolDecl(simpname))
		{
			throw new AssrtException("[assrt-core] Global protocol not found: " + simpname);
		}
		this.gpd = (GProtocolDecl) main.getProtocolDecl(simpname);

		AssrtCoreAstFactory af = new AssrtCoreAstFactory();
		AssrtCoreGType gt = new AssrtCoreGProtocolDeclTranslator(job, af).translate(this.gpd);
		
		/*..HERE FIXME: need to add global assrt rec/continue and fix global inlining -- below steps use only the inlined *global*
		CHECKME: base assrt "works" because projected local proto decl does keep the assertion, and inlining of local, which does handle the assertions (AssrtLProjectionDeclDel), is done from the "base" protocol decl(s) -- i.e., not from the inlined global (CHECKME?)
		(in base, inlining of global is only for global level (syntactic) checks -- model checking is done from the separately inlined locals -- inlined global is also for "extensions" like this one and f17)
		-- does inlining->projection give the same as "base projection"->inlining?*/
		
		time(null, 6);
		job.debugPrintln("\n[assrt-core] Translated:\n  " + gt);
		
		List<AssrtDataTypeVar> adts = gt.collectAnnotDataTypeVarDecls().stream().map(v -> v.var).collect(Collectors.toList());
		job.debugPrintln("\n[assrt-core] Collected data type annotation var decls: " + adts);
		Set<AssrtDataTypeVar> dups = adts.stream().filter(i -> Collections.frequency(adts, i) > 1)
				.collect(Collectors.toSet());	
		if (dups.size() > 0)
		{
			throw new AssrtCoreSyntaxException("[assrt-core] Repeat data type annotation variable declarations not allowed: " + dups);
		}

		Map<Role, AssrtCoreLType> P0 = new HashMap<>();
		for (Role r : gpd.header.roledecls.getRoles())
		{
			AssrtCoreLType lt = gt.project(af, r, AssrtTrueFormula.TRUE);
			P0.put(r, lt);

			job.debugPrintln("\n[assrt-core] Projected onto " + r + ":\n  " + lt);
		}
		time(null, 7);

		AssrtCoreEGraphBuilder builder = new AssrtCoreEGraphBuilder(job);
		this.E0 = new HashMap<>();
		for (Role r : P0.keySet())
		{
			EGraph g = builder.build(P0.get(r));
			this.E0.put(r, (AssrtEState) g.init);

			
			job.debugPrintln("\n[assrt-core] Built endpoint graph for " + r + ":\n" + g.toDot());
		}
		time(null, 8);
				
		assrtCoreValidate(job, simpname, gpd.isExplicitModifier());//, this.E0);  // TODO

		time(null, 100);

		/*if (!job.fair)
		{
			Map<Role, EState> U0 = new HashMap<>();
			for (Role r : E0.keySet())
			{
				EState u = E0.get(r).unfairTransform();
				U0.put(r, u);

				job.debugPrintln
				//System.out.println
						("\n[assrt-core] Unfair transform for " + r + ":\n" + u.toDot());
			}
			
			//validate(job, gpd.isExplicitModifier(), U0, true);  //TODO
		}*/
		
		//((AssrtJob) job).runF17ProjectionPasses();  // projections not built on demand; cf. models

		//return gt;
	}
		
	// HACK: store in (Core) Job/JobContext?
	protected GProtocolDecl gpd;
	protected Map<Role, AssrtEState> E0;  // There is no core version
	protected AssrtCoreSModel model;

	// FIXME: factor out -- cf. super.doAttemptableOutputTasks
	@Override
	protected void tryOutputTasks(Job job) throws CommandLineException, ScribbleException
	{
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLArgFlag.ASSRT_CORE_EFSM))
		{
			String[] args = this.assrtCoreArgs.get(AssrtCoreCLArgFlag.ASSRT_CORE_EFSM);
			for (int i = 0; i < args.length; i += 1)
			{
				Role role = CommandLine.checkRoleArg(job.getContext(), gpd.getHeader().getDeclName(), args[i]);
				String out = E0.get(role).toDot();
				System.out.println("\n" + out);  // Endpoint graphs are "inlined" (a single graph is built)
			}
		}
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLArgFlag.ASSRT_CORE_EFSM_PNG))
		{
			String[] args = this.assrtCoreArgs.get(AssrtCoreCLArgFlag.ASSRT_CORE_EFSM_PNG);
			for (int i = 0; i < args.length; i += 2)
			{
				Role role = CommandLine.checkRoleArg(job.getContext(), gpd.getHeader().getDeclName(), args[i]);
				String png = args[i+1];
				String out = E0.get(role).toDot();
				runDot(out, png);
			}
		}
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLArgFlag.ASSRT_CORE_MODEL))
		{
			System.out.println("\n" + model.toDot());
		}
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLArgFlag.ASSRT_CORE_MODEL_PNG))
		{
			String[] arg = this.assrtCoreArgs.get(AssrtCoreCLArgFlag.ASSRT_CORE_MODEL_PNG);
			String png = arg[0];
			runDot(model.toDot(), png);
		}

		if (this.assrtCoreArgs.containsKey(AssrtCoreCLArgFlag.ASSRT_STP_EFSM))
		{
			String[] args = this.assrtCoreArgs.get(AssrtCoreCLArgFlag.ASSRT_STP_EFSM);
			for (int i = 0; i < args.length; i += 1)
			{
				Role role = CommandLine.checkRoleArg(job.getContext(), gpd.getHeader().getDeclName(), args[i]);
				AssrtStpEState init = AssrtStpEState.from((AssrtCoreEModelFactory) job.ef, E0.get(role));
				String out = init.toDot();
				System.out.println("\n" + out);  // Endpoint graphs are "inlined" (a single graph is built)
			}
		}
		if (this.assrtCoreArgs.containsKey(AssrtCoreCLArgFlag.ASSRT_CORE_EFSM_PNG))
		{
			String[] args = this.assrtCoreArgs.get(AssrtCoreCLArgFlag.ASSRT_CORE_EFSM_PNG);
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

	private void assrtCoreValidate(Job job, GProtocolName simpname, boolean isExplicit, 
			//Map<Role, AssrtEState> E0,
			boolean... unfair) throws ScribbleException, CommandLineException
	{
		time(null, 91);
		
		this.model = new AssrtCoreSModelBuilder(job.sf).build(this.E0, isExplicit);

		time(null, 92);

		job.debugPrintln("\n[assrt-core] Built model:\n" + this.model.toDot());
		
		if (unfair.length == 0 || !unfair[0])
		{
			AssrtCoreSafetyErrors serrs = this.model.getSafetyErrors(job, simpname);  // job just for debug printing
			if (serrs.isSafe())
			{
				job.debugPrintln("\n[assrt-core] Protocol safe.");
			}
			else
			{
				throw new AssrtException("[assrt-core] Protocol not safe:\n" + serrs);
			}
		}
		
		/*F17ProgressErrors perrs = m.getProgressErrors();
		if (perrs.satisfiesProgress())
		{
			job.debugPrintln
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

	public static <T> T time(T t, int i)
	{
		System.err.println(i + ": " + System.currentTimeMillis());
		return t;
	}
}

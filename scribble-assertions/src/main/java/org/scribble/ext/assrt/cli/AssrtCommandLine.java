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
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGProtocolDeclTranslator;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGType;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEGraphBuilder;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModel;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelBuilder;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSafetyErrors;
import org.scribble.ext.assrt.main.AssrtException;
import org.scribble.ext.assrt.main.AssrtMainContext;
import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.main.Job;
import org.scribble.main.ScribbleException;
import org.scribble.main.resource.DirectoryResourceLocator;
import org.scribble.main.resource.ResourceLocator;
import org.scribble.model.endpoint.EGraph;
import org.scribble.model.endpoint.EState;
import org.scribble.sesstype.name.GProtocolName;
import org.scribble.sesstype.name.Role;
import org.scribble.util.ScribParserException;
import org.scribble.visit.context.RecRemover;

public class AssrtCommandLine extends CommandLine
{

	private final Map<AssrtCLArgFlag, String[]> assrtArgs;  // Maps each flag to list of associated argument values
	
	public AssrtCommandLine(String... args) throws CommandLineException
	{
		this(new AssrtCLArgParser(args));
	}

	private AssrtCommandLine(AssrtCLArgParser p) throws CommandLineException
	{
		super(p);  // calls p.parse()
		if (this.args.containsKey(CLArgFlag.INLINE_MAIN_MOD))
		{
			// FIXME: should be fine
			throw new RuntimeException("[scrib-assert] Inline modules not supported:\n" + this.args.get(CLArgFlag.INLINE_MAIN_MOD));
		}
		// FIXME? Duplicated from core
		if (!this.args.containsKey(CLArgFlag.MAIN_MOD))
		{
			throw new CommandLineException("No main module has been specified\r\n");
		}

		this.assrtArgs = p.getAssrtArgs();
	}
	
	// Based on CommandLine.newMainContext
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

		List<Path> impaths = this.args.containsKey(CLArgFlag.IMPORT_PATH)
				? CommandLine.parseImportPaths(this.args.get(CLArgFlag.IMPORT_PATH)[0])
				: Collections.emptyList();
		ResourceLocator locator = new DirectoryResourceLocator(impaths);
		if (this.args.containsKey(CLArgFlag.INLINE_MAIN_MOD))
		{
			return new AssrtMainContext(debug, locator, this.args.get(CLArgFlag.INLINE_MAIN_MOD)[0], useOldWF, noLiveness, minEfsm, fair,
					noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation);
		}
		else
		{
			Path mainpath = CommandLine.parseMainPath(this.args.get(CLArgFlag.MAIN_MOD)[0]);
			return new AssrtMainContext(debug, locator, mainpath, useOldWF, noLiveness, minEfsm, fair,
					noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation);
		}
	}

	public static void main(String[] args) throws CommandLineException, ScribbleException
	{
		new AssrtCommandLine(args).run();
	}

	@Override
	protected void doValidationTasks(Job job) throws ScribbleException, ScribParserException
	{
		if (this.assrtArgs.containsKey(AssrtCLArgFlag.ASSRT))
		{
			/*if (this.args.containsKey(CLArgFlag.PROJECT))  // HACK
				// modules/f17/src/test/scrib/demo/fase17/AppD.scr in [default] mode bug --- projection/EFSM not properly formed if this if is commented ????
			{

			}*/

			GProtocolName simpname = new GProtocolName(this.assrtArgs.get(AssrtCLArgFlag.ASSRT)[0]);
			/*if (simpname.toString().equals("[F17AllTest]"))  // HACK: F17AllTest
			{
				parseAndCheckWF(job);  // Includes base passes
			}
			else*/
			{
				assrtParseAndCheckWF(job, simpname);  // Includes base passes
			}
			
			// FIXME? f17 FSM building only used for f17 validation -- output tasks, e.g., -api, will still use default Scribble FSMs
		}
		else
		{
			super.doValidationTasks(job);
		}
	}

	
	// Refactor into AssrtJob?

	/*private static void parseAndCheckWF(Job job, GProtocolName simpname) throws ScribbleException, ScribParserException
	{
		assrtPreContextBuilding(job);
		
		Module main = job.getContext().getMainModule();
		
		/*if (simpname.toString().equals("[F17AllTest]")) // HACK: F17AllTest
		{
			simpname = main.getGlobalProtocolDecls().iterator().next().getHeader().getNameNode().toName();
		}* /

		/*if (!main.hasProtocolDecl(simpname))
		{
			throw new ScribbleException("Global protocol not found: " + simpname);
		}
		GProtocolDecl gpd = (GProtocolDecl) main.getProtocolDecl(simpname);* /
		
		parseAndCheckWF(job, main, simpname);
	}*/
	
	private static void assrtPreContextBuilding(Job job) throws ScribbleException
	{
		job.runContextBuildingPasses();
		job.runVisitorPassOnParsedModules(RecRemover.class);  // FIXME: Integrate into main passes?  Do before unfolding?
		//job.runVisitorPassOnParsedModules(AnnotSetter.class);  // Hacky -- run after inlining, because original dels discarded
	}

	// Pre: f17PreContextBuilding
	private static void assrtParseAndCheckWF(Job job, GProtocolName simpname) throws ScribbleException, ScribParserException
	{
		assrtPreContextBuilding(job);

		Module main = job.getContext().getMainModule();
		if (!main.hasProtocolDecl(simpname))
		{
			throw new AssrtException("[assrt-core] Global protocol not found: " + simpname);
		}
		GProtocolDecl gpd = (GProtocolDecl) main.getProtocolDecl(simpname);

		AssrtCoreAstFactory af = new AssrtCoreAstFactory();
		AssrtCoreGType gt = new AssrtCoreGProtocolDeclTranslator(job, af).translate(gpd);
		
		job.debugPrintln("\n[assrt-core] Translated:\n  " + gt);
		
		List<AssrtDataTypeVar> adts = gt.collectAnnotDataTypes().stream().map(v -> v.var).collect(Collectors.toList());
		job.debugPrintln("\n[assrt-core] Collected data type annotation var decls: " + adts);
		Set<AssrtDataTypeVar> dups = adts.stream().filter(i -> Collections.frequency(adts, i) > 1)
				.collect(Collectors.toSet());	
		if (dups.size() > 0)
		{
			throw new AssrtException("[assrt-core] Repeat data type annotation variable declarations not allowed: " + dups);
		}

		Map<Role, AssrtCoreLType> P0 = new HashMap<>();
		for (Role r : gpd.header.roledecls.getRoles())
		{
			AssrtCoreLType lt = gt.project(af, r);
			P0.put(r, lt);

			job.debugPrintln("\n[assrt-core] Projected onto " + r + ":\n  " + lt);
		}

		AssrtCoreEGraphBuilder builder = new AssrtCoreEGraphBuilder(job.ef);
		Map<Role, EState> E0 = new HashMap<>();
		for (Role r : P0.keySet())
		{
			EGraph g = builder.build(P0.get(r));
			E0.put(r, g.init);

			job.debugPrintln("\n[assrt-core] Built endpoint graph for " + r + ":\n" + g.toDot());
		}

		validate(job, gpd.isExplicitModifier(), E0);  // TODO

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

	private static void validate(Job job, boolean isExplicit, Map<Role, EState> E0, boolean... unfair) throws AssrtException
	{
		AssrtCoreSModel m = new AssrtCoreSModelBuilder(job.sf).build(E0, isExplicit);

		job.debugPrintln("\n[assrt-core] Built model:\n" + m.toDot());
		
		/*if (unfair.length == 0 || !unfair[0])
		{
			AssrtCoreSafetyErrors serrs = m.getSafetyErrors();
			if (serrs.isSafe())
			{
				job.debugPrintln("\n[assrt-core] Protocol safe.");
			}
			else
			{
				throw new AssrtException("[assrt-core] Protocol not safe:\n" + serrs);
			}
		}*/
		
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
}

package org.scribble.ext.assrt.main;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ast.AstFactory;
import org.scribble.ast.Module;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ext.assrt.model.endpoint.AssrtEGraphBuilderUtil;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.scribble.ext.assrt.util.Z3Wrapper;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.AssrtNameDisambiguator;
import org.scribble.main.Job;
import org.scribble.main.JobContext;
import org.scribble.main.ScribbleException;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.ModuleName;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.context.ModuleContextBuilder;
import org.scribble.visit.context.ProtocolDeclContextBuilder;
import org.scribble.visit.util.RoleCollector;
import org.scribble.visit.wf.DelegationProtocolRefChecker;

public class AssrtJob extends Job
{
	// N.B. currently only used by assrt-core
	public enum Solver { NATIVE_Z3, JAVA_SMT_Z3, NONE }

	public final Solver solver; //= Solver.NATIVE_Z3;

	public AssrtJob(boolean debug, Map<ModuleName, Module> parsed, ModuleName main,
			boolean useOldWF, boolean noLiveness, boolean minEfsm, boolean fair, boolean noLocalChoiceSubjectCheck,
			boolean noAcceptCorrelationCheck, boolean noValidation, 
			Solver solver, AstFactory af, EModelFactory ef, SModelFactory sf)
	{
		super(debug, parsed, main, useOldWF, noLiveness, minEfsm, fair, noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation, af, ef, sf);
		this.solver = solver;
	}

	// N.B. currently only used by assrt-core
	public boolean checkSat(GProtocolName simpname, Set<AssrtBoolFormula> fs)  // Maybe record simpname as field (for core)
	{
		switch (this.solver)
		{
			case JAVA_SMT_Z3:
			{
				if (fs.size() > 1)
				{
					throw new RuntimeException("[assrt] TODO: " + fs);
				}
				return JavaSmtWrapper.getInstance().isSat(fs.iterator().next().getJavaSmtFormula());
			}
			case NATIVE_Z3:
			{
				JobContext jc = getContext();
				return Z3Wrapper.checkSat(this, (GProtocolDecl) jc.getMainModule().getProtocolDecl(simpname), fs);
			}
			case NONE:
			{
				debugPrintln("\n[assrt-core] WARNING: skipping sat check: "
						+ fs.stream().map(f -> f.toSmt2Formula() + "\n").collect(Collectors.joining("")));

				return true;
			}
			default: throw new RuntimeException( "[assrt-core] Shouldn't get in here: " + this.solver);
		}
	}
	
	// FIXME: move to MainContext::newJob?
	@Override
	public AssrtEGraphBuilderUtil newEGraphBuilderUtil()
	{
		return new AssrtEGraphBuilderUtil((AssrtEModelFactory) this.ef);
	}
	
	@Override
	public void runContextBuildingPasses() throws ScribbleException
	{
		runVisitorPassOnAllModules(ModuleContextBuilder.class);

		runVisitorPassOnAllModules(AssrtNameDisambiguator.class);  // FIXME: factor out overriding pattern?

		runVisitorPassOnAllModules(ProtocolDeclContextBuilder.class);
		runVisitorPassOnAllModules(DelegationProtocolRefChecker.class);
		runVisitorPassOnAllModules(RoleCollector.class);
		runVisitorPassOnAllModules(ProtocolDefInliner.class);
	}
		
	@Override
	public void runUnfoldingPass() throws ScribbleException
	{
		//runVisitorPassOnAllModules(AssrtInlinedProtocolUnfolder.class);  // FIXME: skipping for now
	}

	@Override
	protected void runProjectionUnfoldingPass() throws ScribbleException
	{
		//runVisitorPassOnProjectedModules(AssrtInlinedProtocolUnfolder.class);  // FIXME
	}

	@Override
	public void runWellFormednessPasses() throws ScribbleException
	{
		super.runWellFormednessPasses();

		// Additional
		if (!this.noValidation)
		{
			runVisitorPassOnAllModules(AssrtAnnotationChecker.class);
		}
	}
	
	/*// FIXME: refactor
	@Override
	protected SConfig createInitialSConfig(Job job, Map<Role, EGraph> egraphs, boolean explicit)
	{
		Map<Role, EFSM> efsms = egraphs.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().toFsm()));
		SBuffers b0 = new SBuffers(job.ef, efsms.keySet(), !explicit);
		return ((AssrtSModelFactory) job.sf).newAssrtSConfig(efsms, b0, null, new HashMap<Role, Set<String>>());
	}*/
}

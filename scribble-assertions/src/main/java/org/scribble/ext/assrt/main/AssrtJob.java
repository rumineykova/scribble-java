package org.scribble.ext.assrt.main;

import java.util.Map;

import org.scribble.ast.AstFactory;
import org.scribble.ast.Module;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.AssrtNameDisambiguator;
import org.scribble.main.Job;
import org.scribble.main.ScribbleException;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.global.SModelFactory;
import org.scribble.sesstype.name.ModuleName;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.context.ModuleContextBuilder;
import org.scribble.visit.context.ProtocolDeclContextBuilder;
import org.scribble.visit.util.RoleCollector;
import org.scribble.visit.wf.DelegationProtocolRefChecker;

public class AssrtJob extends Job
{
	public AssrtJob(boolean debug, Map<ModuleName, Module> parsed, ModuleName main,
			boolean useOldWF, boolean noLiveness, boolean minEfsm, boolean fair, boolean noLocalChoiceSubjectCheck,
			boolean noAcceptCorrelationCheck, boolean noValidation, 
			AstFactory af, EModelFactory ef, SModelFactory sf)
	{
		super(debug, parsed, main, useOldWF, noLiveness, minEfsm, fair, noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation, af, ef, sf);
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

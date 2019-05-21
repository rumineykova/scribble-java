package org.scribble.ext.assrt.job;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ast.AstFactory;
import org.scribble.ast.Module;
import org.scribble.ast.global.GProtoDecl;
import org.scribble.core.job.Core;
import org.scribble.core.job.CoreArgs;
import org.scribble.core.lang.global.GProtocol;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.ModuleName;
import org.scribble.core.type.session.STypeFactory;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.core.job.AssrtCore;
import org.scribble.ext.assrt.core.job.AssrtCoreArgs;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSTypeFactory;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGTypeFactory;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLTypeFactory;
import org.scribble.ext.assrt.util.Z3Wrapper;
import org.scribble.ext.assrt.visit.AssrtVisitorFactoryImpl;
import org.scribble.job.Job;
import org.scribble.job.JobContext;
import org.scribble.util.ScribException;
import org.scribble.visit.VisitorFactory;

public class AssrtJob extends Job
{
	// N.B. currently only used by assrt-core
	public enum Solver { NATIVE_Z3, NONE }

	public AssrtJob(ModuleName mainFullname, AssrtCoreArgs args,
			Map<ModuleName, Module> parsed, AstFactory af, DelFactory df)
			throws ScribException  // Currently only for dup vis name check (ModuleContextBuilder)
	{
		super(mainFullname, args, parsed, af, df);
	}
	
	@Override
	protected VisitorFactory newVisitorFactory()
	{
		return new AssrtVisitorFactoryImpl();
	}
	
	// A Scribble extension should override newVisitorFactory/STypeFactory as appropriate
	// Used by GTypeTranslator (cf. getCore)
	@Override
	protected STypeFactory newSTypeFactory()
	{
		return new AssrtCoreSTypeFactory(new AssrtCoreGTypeFactory(),
				new AssrtCoreLTypeFactory());
	}

	/*// A Scribble extension should override newJobConfig/Context/Core as appropriate
	)@Override
	protected JobConfig newJobConfig(ModuleName mainFullname, CoreArgs args,
			AstFactory af, DelFactory df, VisitorFactory vf, STypeFactory tf)
	{
		return new JobConfig(mainFullname, args, af, df, vf, tf);
	}*/

	/*// A Scribble extension should override newJobConfig/Context/Core as appropriate
	@Override
	protected JobContext newJobContext(Job job,
			Map<ModuleName, Module> parsed) throws ScribException
	{
		return new JobContext(this, parsed);
	}*/
	
	// A Scribble extension should override newJobConfig/Context/Core as appropriate
	@Override
	protected Core newCore(ModuleName mainFullname, CoreArgs args,
			Set<GProtocol> imeds, STypeFactory tf)
	{
		return new AssrtCore(mainFullname, args, imeds, tf);
	}

	@Override
	public void runPasses() throws ScribException
	{
		super.runPasses();  // Includes AssrtNameDisambiguator

		/*if (!this.config.args.NO_VALIDATION)
		{
			runVisitorPassOnAllModules(AssrtAnnotationChecker.class);
		}*/
	}


	
	
	
	
	
	
	// N.B. currently only used by assrt-core
	public boolean checkSat(GProtoName simpname, Set<AssrtBFormula> fs)  // Maybe record simpname as field (for core)
	{
		Solver solver = ((AssrtCoreArgs) this.config.args).solver;
		switch (solver)
		{
			case NATIVE_Z3:
			{
				JobContext jc = getContext();
				return Z3Wrapper.checkSat(this,
						(GProtoDecl) jc.getMainModule().getGProtoDeclChild(simpname),
						fs);
			}
			case NONE:
			{
				verbosePrintln("\n[assrt-core] [WARNING] Skipping sat check:\n\t"
						+ fs.stream().map(f -> f.toSmt2Formula() + "\n\t")
								.collect(Collectors.joining("")));
				return true;
			}
			default:
				throw new RuntimeException(
						"[assrt-core] Shouldn't get in here: " + solver);
		}
	}
}

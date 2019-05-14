package org.scribble.ext.assrt.main;

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
import org.scribble.core.type.session.global.GTypeFactoryImpl;
import org.scribble.core.type.session.local.LTypeFactoryImpl;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.core.job.AssrtCore;
import org.scribble.ext.assrt.core.job.AssrtCoreArgs;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.util.Z3Wrapper;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.job.JobContext;
import org.scribble.util.ScribException;
import org.scribble.visit.VisitorFactory;
import org.scribble.visit.VisitorFactoryImpl;

public class AssrtJob extends org.scribble.job.Job
{
	// N.B. currently only used by assrt-core
	public enum Solver { NATIVE_Z3, NONE }

	public AssrtJob(ModuleName mainFullname, AssrtCoreArgs args,
			Map<ModuleName, Module> parsed, AstFactory af, DelFactory df)
	{
		super(mainFullname, args, parsed, af, df);
	}
	
	@Override
	protected VisitorFactory newVisitorFactory()
	{
		runVisitorPassOnAllModules(AssrtNameDisambiguator.class);  // FIXME: factor out overriding pattern?

		return new VisitorFactoryImpl();
	}
	
	// A Scribble extension should override newVisitorFactory/STypeFactory as appropriate
	// Used by GTypeTranslator (cf. getCore)
	@Override
	protected STypeFactory newSTypeFactory()
	{
		return new STypeFactory(new GTypeFactoryImpl(),
				new LTypeFactoryImpl());
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
		super.runPasses();

		if (!this.config.args.NO_VALIDATION)
		{
			runVisitorPassOnAllModules(AssrtAnnotationChecker.class);
		}
	}


	
	
	
	
	
	
	// N.B. currently only used by assrt-core
	public boolean checkSat(GProtoName simpname, Set<AssrtBoolFormula> fs)  // Maybe record simpname as field (for core)
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

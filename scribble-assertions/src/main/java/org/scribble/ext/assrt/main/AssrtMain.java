package org.scribble.ext.assrt.main;

import java.nio.file.Path;
import java.util.Map;

import org.scribble.ast.AstFactory;
import org.scribble.core.job.CoreArgs;
import org.scribble.ext.assrt.ast.AssrtAstFactoryImpl;
import org.scribble.ext.assrt.parser.scribble.AssrtScribbleAntlrWrapper;
import org.scribble.main.Main;
import org.scribble.main.resource.locator.ResourceLocator;
import org.scribble.parser.ScribAntlrWrapper;
import org.scribble.util.ScribException;
import org.scribble.util.ScribParserException;

public class AssrtMain extends Main
{
	//public final Solver solver; //= Solver.NATIVE_Z3;
	//public final boolean batching;

	// Load main module from file system
	public AssrtMain(ResourceLocator locator, Path mainpath,
			Map<CoreArgs, Boolean> args) throws ScribException, ScribParserException
	{
		super(locator, mainpath, args);
	}

	@Override
	protected ScribAntlrWrapper newAntlr()
	{
		return new AssrtScribbleAntlrWrapper();
	}
	
	@Override
	protected AstFactory newAstFactory()
	{
		return new AssrtAstFactoryImpl();
	}

	@Override
	public AssrtJob newJob()
	{
		return new AssrtJob(this.debug, this.getParsedModules(), this.main, this.useOldWF, this.noLiveness, this.minEfsm, this.fair,
				this.noLocalChoiceSubjectCheck, this.noAcceptCorrelationCheck, this.noValidation,
				this.solver, this.batching, this.af, this.ef, this.sf);
	}
}

package org.scribble.ext.assrt.main;

import java.nio.file.Path;

import org.scribble.ast.AstFactory;
import org.scribble.ext.assrt.ast.AssrtAstFactoryImpl;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactoryImpl;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelFactoryImpl;
import org.scribble.ext.assrt.parser.scribble.AssrtAntlrToScribParser;
import org.scribble.ext.assrt.parser.scribble.AssrtScribbleAntlrWrapper;
import org.scribble.main.MainContext;
import org.scribble.main.ScribbleException;
import org.scribble.main.resource.ResourceLocator;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.global.SModelFactory;
import org.scribble.parser.scribble.AntlrToScribParser;
import org.scribble.parser.scribble.ScribbleAntlrWrapper;
import org.scribble.util.ScribParserException;

public class AssrtMainContext extends MainContext
{
	// Load main module from file system
	public AssrtMainContext(boolean debug, ResourceLocator locator, Path mainpath, boolean useOldWF, boolean noLiveness, boolean minEfsm,
			boolean fair, boolean noLocalChoiceSubjectCheck, boolean noAcceptCorrelationCheck, boolean noValidation)
					throws ScribParserException, ScribbleException
	{
		super(debug, locator, mainpath, useOldWF, noLiveness, minEfsm, fair, noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation);
	}

	// For inline module arg
	public AssrtMainContext(boolean debug, ResourceLocator locator, String inline, boolean useOldWF, boolean noLiveness, boolean minEfsm,
			boolean fair, boolean noLocalChoiceSubjectCheck, boolean noAcceptCorrelationCheck, boolean noValidation)
					throws ScribParserException, ScribbleException
	{
		super(debug, locator, inline, useOldWF, noLiveness, minEfsm, fair, noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation);
		throw new RuntimeException("[assrt] Shouldn't get in here:\n" + inline);
	}

	@Override
	public AssrtJob newJob()
	{
		return new AssrtJob(this.debug, this.getParsedModules(), this.main, this.useOldWF, this.noLiveness, this.minEfsm, this.fair,
				this.noLocalChoiceSubjectCheck, this.noAcceptCorrelationCheck, this.noValidation,
				this.af, this.ef, this.sf);
	}

	@Override
	protected ScribbleAntlrWrapper newAntlrParser()
	{
		return new AssrtScribbleAntlrWrapper();
	}
	
	@Override
	protected AntlrToScribParser newScribParser()
	{
		return new AssrtAntlrToScribParser();
	}
	
	protected AstFactory newAstFactory()
	{
		return new AssrtAstFactoryImpl();
	}
	
	@Override
	protected EModelFactory newEModelFactory()
	{
		//return new AssrtEModelFactoryImpl();
		return new AssrtCoreEModelFactoryImpl();  // HACK FIXME
	}
	
	@Override
	protected SModelFactory newSModelFactory()
	{
		//return new AssrtSModelFactoryImpl();
		return new AssrtCoreSModelFactoryImpl();  // HACK FIXME
	}
}

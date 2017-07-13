package org.scribble.ext.assrt.main;

import java.nio.file.Path;

import org.scribble.ast.AstFactory;
import org.scribble.ext.assrt.ast.AssrtAstFactoryImpl;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactoryImpl;
import org.scribble.ext.assrt.model.global.AssrtSModelFactoryImpl;
import org.scribble.ext.assrt.parser.scribble.AssrtAntlrParser;
import org.scribble.ext.assrt.parser.scribble.AssrtScribParser;
import org.scribble.main.MainContext;
import org.scribble.main.ScribbleException;
import org.scribble.main.resource.ResourceLocator;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.global.SModelFactory;
import org.scribble.parser.scribble.AntlrParser;
import org.scribble.parser.scribble.ScribParser;
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
		throw new RuntimeException("[scrib-assert] Shouldn't get in here:\n" + inline);
	}

	@Override
	public AssrtJob newJob()
	{
		return new AssrtJob(this.debug, this.getParsedModules(), this.main, this.useOldWF, this.noLiveness, this.minEfsm, this.fair,
				this.noLocalChoiceSubjectCheck, this.noAcceptCorrelationCheck, this.noValidation,
				this.af, this.ef, this.sf);
	}

	@Override
	protected AntlrParser newAntlrParser()
	{
		return new AssrtAntlrParser();
	}
	
	@Override
	protected ScribParser newScribParser()
	{
		return new AssrtScribParser();
	}
	
	protected AstFactory newAstFactory()
	{
		return new AssrtAstFactoryImpl();
	}
	
	@Override
	protected EModelFactory newEModelFactory()
	{
		return new AssrtEModelFactoryImpl();
	}
	
	@Override
	protected SModelFactory newSModelFactory()
	{
		return new AssrtSModelFactoryImpl();
	}
}

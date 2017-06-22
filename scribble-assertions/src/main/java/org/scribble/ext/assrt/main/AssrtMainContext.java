/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.ext.assrt.main;

import java.nio.file.Path;

import org.scribble.ast.AstFactory;
import org.scribble.ext.assrt.ast.AssrtAstFactoryImpl;
import org.scribble.ext.assrt.parser.AssrtAntlrParser;
import org.scribble.ext.assrt.parser.AssrtScribParser;
import org.scribble.main.MainContext;
import org.scribble.main.ScribbleException;
import org.scribble.main.resource.ResourceLocator;
import org.scribble.parser.AntlrParser;
import org.scribble.parser.ScribParser;
import org.scribble.util.ScribParserException;

public class AssrtMainContext extends MainContext
{
	@Override
	public AssrtJob newJob()
	{
		return new AssrtJob(this.debug, this.getParsedModules(), this.main, this.useOldWF, this.noLiveness, this.minEfsm, this.fair,
				this.noLocalChoiceSubjectCheck, this.noAcceptCorrelationCheck, this.noValidation,
				this.af);
	}

	// Load main module from file system
	public AssrtMainContext(boolean debug, ResourceLocator locator, Path mainpath, boolean useOldWF, boolean noLiveness, boolean minEfsm,
			boolean fair, boolean noLocalChoiceSubjectCheck, boolean noAcceptCorrelationCheck, boolean noValidation, boolean f17)
					throws ScribParserException, ScribbleException
	{
		super(debug, locator, mainpath, useOldWF, noLiveness, minEfsm, fair, noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation, f17);
	}

	// For inline module arg
	public AssrtMainContext(boolean debug, ResourceLocator locator, String inline, boolean useOldWF, boolean noLiveness, boolean minEfsm,
			boolean fair, boolean noLocalChoiceSubjectCheck, boolean noAcceptCorrelationCheck, boolean noValidation, boolean f17)
					throws ScribParserException, ScribbleException
	{
		super(debug, locator, inline, useOldWF, noLiveness, minEfsm, fair, noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation, f17);
		throw new RuntimeException("[scrib-assert] Shouldn't get in here:\n" + inline);
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
}
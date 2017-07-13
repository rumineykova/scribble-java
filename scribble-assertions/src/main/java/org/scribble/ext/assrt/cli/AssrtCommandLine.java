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
package org.scribble.ext.assrt.cli;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.scribble.cli.CLArgFlag;
import org.scribble.cli.CommandLine;
import org.scribble.cli.CommandLineException;
import org.scribble.ext.assrt.main.AssrtMainContext;
import org.scribble.main.ScribbleException;
import org.scribble.main.resource.DirectoryResourceLocator;
import org.scribble.main.resource.ResourceLocator;
import org.scribble.util.ScribParserException;

public class AssrtCommandLine extends CommandLine
{

	public AssrtCommandLine(String... args) throws CommandLineException
	{
		super(args);
		if (this.args.containsKey(CLArgFlag.INLINE_MAIN_MOD))
		{
			throw new RuntimeException("[scrib-assert] Inline modules not supported:\n" + this.args.get(CLArgFlag.INLINE_MAIN_MOD));
		}
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
		boolean f17 = this.args.containsKey(CLArgFlag.F17);

		List<Path> impaths = this.args.containsKey(CLArgFlag.IMPORT_PATH)
				? CommandLine.parseImportPaths(this.args.get(CLArgFlag.IMPORT_PATH)[0])
				: Collections.emptyList();
		ResourceLocator locator = new DirectoryResourceLocator(impaths);
		if (this.args.containsKey(CLArgFlag.INLINE_MAIN_MOD))
		{
			return new AssrtMainContext(debug, locator, this.args.get(CLArgFlag.INLINE_MAIN_MOD)[0], useOldWF, noLiveness, minEfsm, fair,
					noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation, f17);
		}
		else
		{
			Path mainpath = CommandLine.parseMainPath(this.args.get(CLArgFlag.MAIN_MOD)[0]);
			return new AssrtMainContext(debug, locator, mainpath, useOldWF, noLiveness, minEfsm, fair,
					noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation, f17);
		}
	}

	public static void main(String[] args) throws CommandLineException, ScribbleException
	{
		new AssrtCommandLine(args).run();
	}
}

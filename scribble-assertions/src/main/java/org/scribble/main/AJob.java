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
package org.scribble.main;

import java.util.Map;

import org.scribble.ast.Module;
import org.scribble.sesstype.name.ModuleName;
import org.scribble.visit.AnnotationChecker;
import org.scribble.visit.validation.GProtocolValidator;
import org.scribble.visit.wf.ReachabilityChecker;
import org.scribble.visit.wf.WFChoiceChecker;

public class AJob extends Job
{
	public AJob(boolean debug, Map<ModuleName, Module> parsed, ModuleName main,
			boolean useOldWF, boolean noLiveness, boolean minEfsm, boolean fair, boolean noLocalChoiceSubjectCheck,
			boolean noAcceptCorrelationCheck, boolean noValidation)
	{
		super(debug, parsed, main, useOldWF, noLiveness, minEfsm, fair, noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation);
	}

	@Override
	public void runWellFormednessPasses() throws ScribbleException
	{
		if (!this.noValidation)
		{
			runVisitorPassOnAllModules(WFChoiceChecker.class);  // For enabled roles and disjoint enabling messages -- includes connectedness checks
			runProjectionPasses();
			runVisitorPassOnAllModules(ReachabilityChecker.class);  // Moved before GlobalModelChecker.class, OK?
			if (!this.useOldWf)
			{
				runVisitorPassOnAllModules(GProtocolValidator.class);
			}
			
			runVisitorPassOnAllModules(AnnotationChecker.class);
		}
	}
}

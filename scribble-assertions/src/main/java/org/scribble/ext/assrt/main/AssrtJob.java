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

import java.util.Map;

import org.scribble.ast.AstFactory;
import org.scribble.ast.Module;
import org.scribble.ext.assrt.model.global.AssrtSGraph;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.main.Job;
import org.scribble.main.ScribbleException;
import org.scribble.model.endpoint.EGraph;
import org.scribble.sesstype.name.GProtocolName;
import org.scribble.sesstype.name.ModuleName;
import org.scribble.sesstype.name.Role;

public class AssrtJob extends Job
{
	public AssrtJob(boolean debug, Map<ModuleName, Module> parsed, ModuleName main,
			boolean useOldWF, boolean noLiveness, boolean minEfsm, boolean fair, boolean noLocalChoiceSubjectCheck,
			boolean noAcceptCorrelationCheck, boolean noValidation, 
			AstFactory af)
	{
		super(debug, parsed, main, useOldWF, noLiveness, minEfsm, fair, noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation, af);
	}

	// FIXME: factor out as a builder util, cf. EGraphBuilderUtil
	@Override
	protected AssrtSGraph buildSGraph(Map<Role, EGraph> egraphs, boolean explicit, Job job, GProtocolName fullname) throws ScribbleException
	{
		return AssrtSGraph.buildSGraph(egraphs, explicit, this, fullname);
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
}

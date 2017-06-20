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
package org.scribble.model.global;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scribble.main.Job;
import org.scribble.main.ScribbleException;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.model.global.actions.SAction;
import org.scribble.sesstype.name.Role;

public class ASModel extends SModel
{
	protected ASModel(SGraph graph)
	{
		super(graph);
	}

	@Override
	public void validate(Job job) throws ScribbleException
	{
		SState init = this.graph.init;
		Map<Integer, SState> states = this.graph.states;

		String errorMsg = "";

		int count = 0;
		for (SState s : states.values())
		{
			if (job.debug)
			{
				count++;
				if (count % 50 == 0)
				{
					//job.debugPrintln("(" + this.graph.proto + ") Checking safety: " + count + " states");
					job.debugPrintln("(" + this.graph.proto + ") Checking states: " + count);
				}
			}
			ASStateErrors errors = ((ASState) s).getErrors();
			//SMTWrapper.getInstance().close();
			
			if (!errors.isEmpty())
			{
				// FIXME: getTrace can get stuck when local choice subjects are disabled
				List<SAction> trace = this.graph.getTrace(init, s);  // FIXME: getTrace broken on non-det self loops?
				//errorMsg += "\nSafety violation(s) at " + s.toString() + ":\n    Trace=" + trace;
				errorMsg += "\nSafety violation(s) at session state " + s.id + ":\n    Trace=" + trace;
			}
			if (!errors.stuck.isEmpty())
			{
				errorMsg += "\n    Stuck messages: " + errors.stuck;  // Deadlock from reception error
			}
			if (!errors.waitFor.isEmpty())
			{
				errorMsg += "\n    Wait-for errors: " + errors.waitFor;  // Deadlock from input-blocked cycles, terminated dependencies, etc
			}
			if (!errors.orphans.isEmpty())
			{
				errorMsg += "\n    Orphan messages: " + errors.orphans;  // FIXME: add sender of orphan to error message 
			}
			if (!errors.unfinished.isEmpty())
			{
				errorMsg += "\n    Unfinished roles: " + errors.unfinished;
			}
			if (!errors.varsNotInScope.isEmpty())
			{
				errorMsg += "\n    Assertion variables are not in scope " + errors.varsNotInScope;
			}
			if (!errors.unsatAssertions.isEmpty())
			{
				errorMsg += "\n    Unsatisfiable constraints " + errors.unsatAssertions;
			}
		}
		job.debugPrintln("(" + this.graph.proto + ") Checked all states: " + count);  // May include unsafe states
		//*/
		
		if (!job.noProgress)
		{
			//job.debugPrintln("(" + this.graph.proto + ") Checking progress: ");  // Incompatible with current errorMsg approach*/

			Set<Set<Integer>> termsets = this.graph.getTerminalSets();
			for (Set<Integer> termset : termsets)
			{
				/*job.debugPrintln("(" + this.graph.proto + ") Checking terminal set: "
							+ termset.stream().map((i) -> new Integer(all.get(i).id).toString()).collect(Collectors.joining(",")));  // Incompatible with current errorMsg approach*/

				Set<Role> starved = SModel.checkRoleProgress(states, init, termset);
				if (!starved.isEmpty())
				{
					errorMsg += "\nRole progress violation for " + starved + " in session state terminal set:\n    " + termSetToString(job, termset, states);
				}
				Map<Role, Set<ESend>> ignored = SModel.checkEventualReception(states, init, termset);
				if (!ignored.isEmpty())
				{
					errorMsg += "\nEventual reception violation for " + ignored + " in session state terminal set:\n    " + termSetToString(job, termset, states);
				}
			}
		}
		
		if (!errorMsg.equals(""))
		{
			//throw new ScribbleException("\n" + init.toDot() + errorMsg);
			throw new ScribbleException(errorMsg);
		}
		//job.debugPrintln("(" + this.graph.proto + ") Progress satisfied.");  // Also safety... current errorMsg approach
	}
}

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
package org.scribble.ext.assrt.del.global;

import java.util.List;
import java.util.stream.Collectors;

import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GChoice;
import org.scribble.del.global.GChoiceDel;
import org.scribble.ext.assrt.del.AssrtICompoundInteractionNodeDel;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;

public class AssrtGChoiceDel extends GChoiceDel implements AssrtICompoundInteractionNodeDel
{

	@Override
	public GChoice leaveAnnotCheck(ScribNode parent, ScribNode child,  AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		// Cf. GChoiceDel.leaveInlinedWFChoiceCheck
		GChoice cho = (GChoice) visited;
		List<AssrtAnnotationEnv> benvs = cho.getBlocks().stream()
				.map(b -> (AssrtAnnotationEnv) b.del().env()).collect(Collectors.toList());
		AssrtAnnotationEnv merged = checker.popEnv().mergeContexts(benvs); 
		checker.pushEnv(merged);
		return (GChoice) AssrtICompoundInteractionNodeDel.super.leaveAnnotCheck(parent, child, checker, visited);  // Replaces base popAndSet to do pop, merge and set
	}

	/*@Override
	public void enterAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker) throws ScribbleException
	{
		AssrtAnnotationEnv env = checker.peekEnv().enterContext();
		checker.pushEnv(env);
	}*/
	
	/*// Cf. GChoiceDel.leaveInlinedWFChoiceCheck
	@Override
	public GChoice leaveAnnotCheck(ScribNode parent, ScribNode child,  AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		GChoice cho = (GChoice) visited;
		
		List<AssrtAnnotationEnv> benvs =
				cho.getBlocks().stream().map((b) -> (AssrtAnnotationEnv) b.del().env()).collect(Collectors.toList());
		AssrtAnnotationEnv merged = checker.popEnv().mergeContexts(benvs); 
		checker.pushEnv(merged);
				// Merged children into one here, now merge it into parent	

		// From CompoundInteractionNodeDel.leaveInlinedWFChoiceCheck
		// FIXME: don't need to push above, just to pop again here
		AssrtAnnotationEnv visited_env = checker.popEnv();  // popAndSet current
		setEnv(visited_env);
		AssrtAnnotationEnv parent_env = checker.popEnv();  // pop-merge-push parent
		parent_env = parent_env.mergeContext(visited_env);
		checker.pushEnv(parent_env);
		
		return cho;
	}*/
}

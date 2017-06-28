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
package org.scribble.ext.assrt.del;

import org.scribble.ast.ScribNode;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;

public interface AssrtICompoundInteractionNodeDel extends AssrtICompoundInteractionDel
{

	@Override
	default ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		// Duplicated from CompoundInteractionNodeDel.leaveInlinedWFChoiceCheck
		AssrtAnnotationEnv visited_env = checker.popEnv();  // popAndSet current
		setEnv(visited_env);
		AssrtAnnotationEnv parent_env = checker.popEnv();  // pop-merge-push parent
		parent_env = parent_env.mergeContext(visited_env);
		checker.pushEnv(parent_env);
		return visited;
	}
}

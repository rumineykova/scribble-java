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

import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GRecursion;
import org.scribble.del.global.GRecursionDel;
import org.scribble.ext.assrt.del.AssrtICompoundInteractionNodeDel;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;

public class AssrtGRecursionDel extends GRecursionDel implements AssrtICompoundInteractionNodeDel
{
	
	@Override
	public ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child,  AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		// Duplicated from GRecursionDel.leaveInlinedWFChoiceCheck
		GRecursion rec = (GRecursion) visited;
		AssrtAnnotationEnv merged = checker.popEnv().mergeContext((AssrtAnnotationEnv) rec.block.del().env());  // Merge block child env into current rec env
		checker.pushEnv(merged);
		return (GRecursion) AssrtICompoundInteractionNodeDel.super.leaveAnnotCheck(parent, child, checker, rec);  // Will merge current rec env into parent (and set env on del)
	}
}

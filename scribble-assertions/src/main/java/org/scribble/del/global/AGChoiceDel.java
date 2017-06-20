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
package org.scribble.del.global;

import java.util.List;
import java.util.stream.Collectors;

import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GChoice;
import org.scribble.del.AScribDel;
import org.scribble.main.ScribbleException;
import org.scribble.visit.AAnnotationChecker;
import org.scribble.visit.wf.env.AAnnotationEnv;

public class AGChoiceDel extends GChoiceDel implements AScribDel
{

	@Override
	public void enterAnnotCheck(ScribNode parent, ScribNode child, AAnnotationChecker checker) throws ScribbleException
	{
		AAnnotationEnv env = checker.peekEnv().enterContext();
		checker.pushEnv(env);
	}
	
	@Override
	public GChoice leaveAnnotCheck(ScribNode parent, ScribNode child,  AAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		GChoice cho = (GChoice) visited;
		
		List<AAnnotationEnv> benvs =
				cho.getBlocks().stream().map((b) -> (AAnnotationEnv) b.del().env()).collect(Collectors.toList());
		AAnnotationEnv merged = checker.popEnv().mergeContexts(benvs); 
		checker.pushEnv(merged);
		return (GChoice) super.leaveAnnotCheck(parent, child, checker, visited);  // Done merge of children here, super does merge into parent	
				// FIXME: don't use AChoiceDel super
	}
	
}

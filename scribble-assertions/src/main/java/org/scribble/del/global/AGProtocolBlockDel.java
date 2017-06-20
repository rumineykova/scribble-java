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

import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GChoice;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.del.AScribDel;
import org.scribble.del.ScribDelBase;
import org.scribble.main.ScribbleException;
import org.scribble.visit.wf.AAnnotationChecker;

public class AGProtocolBlockDel extends GProtocolBlockDel implements AScribDel
{
	
	// Cf. GProtocolBlockDel.project?
	@Override
	public void enterAnnotCheck(ScribNode parent, ScribNode child, AAnnotationChecker checker) throws ScribbleException
	{
		// CHECKME: why only for GChoice?
		if (parent instanceof GChoice)
		{
			ScribDelBase.pushVisitorEnv(this, checker);
		}
	}
	
	@Override
	public GProtocolBlock leaveAnnotCheck(ScribNode parent, ScribNode child, AAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		// CHECKME: why only for GChoice?
		return (parent instanceof GChoice)
				? (GProtocolBlock) ScribDelBase.popAndSetVisitorEnv(this, checker, visited)
				: (GProtocolBlock) visited; 
	}
}

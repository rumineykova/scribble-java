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

import org.scribble.del.global.GProtocolBlockDel;
import org.scribble.ext.assrt.del.AssrtICompoundInteractionDel;

public class AssrtGProtocolBlockDel extends GProtocolBlockDel implements AssrtICompoundInteractionDel
{
	
	/*// Cf. GProtocolBlockDel.project?
	@Override
	public void enterAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker) throws ScribbleException
	{
		// CHECKME: why only for GChoice? -- Should be left to AssrtGChoiceDel? cf. GChoiceDel.enterInlinedWFChoiceCheck
		if (parent instanceof GChoice)
		{
			ScribDelBase.pushVisitorEnv(this, checker);
		}
	}
	
	@Override
	public GProtocolBlock leaveAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		// CHECKME: why only for GChoice?
		return (parent instanceof GChoice)
				? (GProtocolBlock) ScribDelBase.popAndSetVisitorEnv(this, checker, visited)
				: (GProtocolBlock) visited; 
	}*/
}

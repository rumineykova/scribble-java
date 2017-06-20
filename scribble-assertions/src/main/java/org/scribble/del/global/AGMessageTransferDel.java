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

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.ScribNode;
import org.scribble.del.AScribDel;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.name.APayloadType;
import org.scribble.sesstype.name.PayloadType;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.wf.AAnnotationChecker;
import org.scribble.visit.wf.env.AAnnotationEnv;

public class AGMessageTransferDel extends GMessageTransferDel implements AScribDel
{
	public AGMessageTransferDel()
	{
		
	}

	@Override
	public MessageTransfer<?> leaveAnnotCheck(ScribNode parent, ScribNode child, AAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		AAnnotationEnv env = checker.popEnv();
		MessageTransfer<?> mt = (MessageTransfer<?>) visited;
		if (mt.msg.isMessageSigNode())
		{	
			Role src = mt.src.toName();
			List<Role> dest = mt.getDestinationRoles();   
			
			for (PayloadElem<?> pe : ((MessageSigNode) mt.msg).payloads.getElements())
			{
				PayloadType<?> peType = pe.toPayloadType(); 
				if (peType instanceof APayloadType<?>)  // FIXME?
				{
					APayloadType<?> apt = (APayloadType<?>) peType;
					if (apt.isAnnotPayloadDecl() || apt.isAnnotPayloadInScope())
					{
						env.checkIfPayloadValid(apt, src, dest); 
					}
				}
			}
		}
		
		checker.pushEnv(env);
		
		return mt; 
	}
}

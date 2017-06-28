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

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.ScribNode;
import org.scribble.del.global.GMessageTransferDel;
import org.scribble.ext.assrt.del.AssrtScribDel;
import org.scribble.ext.assrt.sesstype.name.AssrtPayloadType;
import org.scribble.ext.assrt.sesstype.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVarName;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.name.PayloadType;
import org.scribble.sesstype.name.Role;

public class AssrtGMessageTransferDel extends GMessageTransferDel implements AssrtScribDel
{
	public AssrtGMessageTransferDel()
	{
		
	}

	@Override
	public MessageTransfer<?> leaveAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		MessageTransfer<?> mt = (MessageTransfer<?>) visited;
		AssrtAnnotationEnv env = checker.popEnv();

		if (mt.msg.isMessageSigNode())
		{	
			Role src = mt.src.toName();
			List<Role> dests = mt.getDestinationRoles();   
			
			for (PayloadElem<?> pe : ((MessageSigNode) mt.msg).payloads.getElements())
			{
				PayloadType<?> peType = pe.toPayloadType(); 
				if (peType instanceof AssrtPayloadType<?>)
				{
					AssrtPayloadType<?> apt = (AssrtPayloadType<?>) peType;
					/*if (apt.isAnnotVarDecl() || apt.isAnnotVarName())
					{
						env.checkIfPayloadValid(apt, src, dests); 
					}*/
					if (apt.isAnnotVarDecl())
					{
						AssrtAnnotDataType adt = (AssrtAnnotDataType) apt;
						if (env.isDataTypeVarBound(adt.var))
						{
							throw new ScribbleException("Payload var " + pe + " is already declared."); 
						}
						env = env.addAnnotDataType(src, adt); 
						for (Role d : dests)
						{
							//env = env.addAnnotDataType(d, adt);
							env = env.addDataTypeVarName(d, adt.var);
						}
					}
					else //if (apt.isAnnotVarName())
					{
						AssrtDataTypeVarName v = (AssrtDataTypeVarName) apt;
						if (!env.isDataTypeVarKnown(src, v))
						{
							throw new ScribbleException("Payload var " + pe + " is not in scope for role: " + src);
						}
						for (Role d : dests)
						{
							env = env.addDataTypeVarName(d, v);
						}
					}
				}
			}
		}
		else
		{
			throw new RuntimeException("[scrib-assert] TODO: " + mt.msg);
		}
		
		checker.pushEnv(env);
		return mt; 
	}
}

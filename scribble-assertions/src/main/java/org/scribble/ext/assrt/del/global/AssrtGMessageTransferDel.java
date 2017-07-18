package org.scribble.ext.assrt.del.global;

import java.util.List;

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.ScribNode;
import org.scribble.del.global.GMessageTransferDel;
import org.scribble.ext.assrt.del.AssrtScribDel;
import org.scribble.ext.assrt.sesstype.name.AssrtPayloadElemType;
import org.scribble.ext.assrt.sesstype.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.name.PayloadElemType;
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
				PayloadElemType<?> peType = pe.toPayloadType(); 
				if (peType instanceof AssrtPayloadElemType<?>)
				{
					AssrtPayloadElemType<?> apt = (AssrtPayloadElemType<?>) peType;
					if (apt.isAnnotVarDecl())
					{
						AssrtAnnotDataType adt = (AssrtAnnotDataType) apt;
						if (env.isDataTypeVarBound(adt.var))
						{
							throw new ScribbleException("Payload var " + pe + " is already declared."); 
						}
						env = env.addAnnotDataType(src, adt); 
						for (Role dest : dests)
						{
							env = env.addDataTypeVarName(dest, adt.var);
						}
					}
					else //if (apt.isAnnotVarName())
					{
						AssrtDataTypeVar v = (AssrtDataTypeVar) apt;
						if (!env.isDataTypeVarKnown(src, v))
						{
							throw new ScribbleException("Payload var " + pe + " is not in scope for role: " + src);
						}
						for (Role dest : dests)
						{
							env = env.addDataTypeVarName(dest, v);
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

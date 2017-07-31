package org.scribble.ext.assrt.del.global;

import java.util.List;

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.ScribNode;
import org.scribble.del.global.GMessageTransferDel;
import org.scribble.ext.assrt.del.AssrtScribDel;
import org.scribble.ext.assrt.main.AssrtException;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.type.name.AssrtPayloadElemType;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;
import org.scribble.type.name.PayloadElemType;
import org.scribble.type.name.Role;

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
			
			env = leaveAnnotCheckForAssrtAssertion(env, src, (MessageSigNode) mt.msg, dests);

		}
		else
		{
			throw new RuntimeException("[scrib-assert] TODO: " + mt.msg);
		}
		
		checker.pushEnv(env);
		return mt; 
	}

	// Factor into AssrtAssrtionDel directly?  Need access to all the params
	// List<Role> dests, though multicast not supported overall
	protected static AssrtAnnotationEnv leaveAnnotCheckForAssrtAssertion(AssrtAnnotationEnv env, Role src, MessageSigNode msg, List<Role> dests) throws ScribbleException
	{
		for (PayloadElem<?> pe : msg.payloads.getElements())
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
						throw new AssrtException("[assrt] Payload var " + pe + " is already declared."); 
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
						throw new AssrtException("[assrt] Payload var " + pe + " is not in scope for role: " + src);
					}
					for (Role dest : dests)
					{
						env = env.addDataTypeVarName(dest, v);
					}
				}
			}
		}
		return env;
	}
}

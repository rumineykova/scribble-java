package org.scribble.ext.assrt.del.global;

import org.scribble.del.global.GMessageTransferDel;
import org.scribble.ext.assrt.del.AssrtScribDel;

public class AssrtGMsgTransferDel extends GMessageTransferDel
		implements AssrtScribDel
{
	public AssrtGMsgTransferDel()
	{
		
	}

	/*
	@Override
	public MessageTransfer<?> leaveAnnotCheck(ScribNode child,
			AssrtAnnotationChecker checker, ScribNode visited) throws ScribException
	{
		MsgTransfer<?> mt = (MsgTransfer<?>) visited;
		AssrtAnnotationEnv env = checker.popEnv();

		if (mt.msg.isMessageSigNode())
		{	
			Role src = mt.src.toName();
			List<Role> dests = mt.getDestinationRoles();   
			
			env = leaveAnnotCheckForAssrtAssertion(env, src, (MsgSigNode) mt.msg, dests);

		}
		else
		{
			throw new RuntimeException("[assrt] TODO: " + mt.msg);
		}
		
		checker.pushEnv(env);
		return mt; 
	}

	// Factor into AssrtAssrtionDel directly?  Need access to all the params
	// List<Role> dests, though multicast not supported overall
	protected static AssrtAnnotationEnv leaveAnnotCheckForAssrtAssertion(
			AssrtAnnotationEnv env, Role src, MsgSigNode msg, List<Role> dests)
			throws ScribException
	{
		for (PayElem<?> pe : msg.payloads.getElements())
		{
			PayElemType<?> peType = pe.toPayloadType(); 
			if (peType instanceof AssrtPayElemType<?>)
			{
				AssrtPayElemType<?> apt = (AssrtPayElemType<?>) peType;
				if (apt.isAnnotVarDecl())
				{
					AssrtAnnotDataType adt = (AssrtAnnotDataType) apt;
					if (env.isDataTypeVarBound(adt.var))
					{
						throw new AssrtException(
								"[assrt] Payload var " + pe + " is already declared.");
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
						throw new AssrtException("[assrt] Payload var " + pe
								+ " is not in scope for role: " + src);
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
	//*/
}

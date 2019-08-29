package org.scribble.ext.assrt.del.global;

import org.scribble.del.global.GConnectDel;
import org.scribble.ext.assrt.del.AssrtScribDel;

public class AssrtGConnectDel extends GConnectDel implements AssrtScribDel
{
	public AssrtGConnectDel()
	{
		
	}

	/*
	// Duplicated from AssrtGMessageTransferDel
	@Override
	public ConnectionAction<?> leaveAnnotCheck(ScribNode child,
			AssrtAnnotationChecker checker, ScribNode visited) throws ScribException
	{
		ConnectionAction<?> ca = (ConnectionAction<?>) visited;
		AssrtAnnotationEnv env = checker.popEnv();

		if (ca.msg.isMessageSigNode())
		{	
			Role src = ca.src.toName();
			Role dest = ca.dest.toName();   
			
			env = AssrtGMessageTransferDel.leaveAnnotCheckForAssrtAssertion(env, src, (MessageSigNode) ca.msg, Arrays.asList(dest));
			
			/*for (PayloadElem<?> pe : ((MessageSigNode) ca.msg).payloads.getElements())
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
							throw new ScribException("Payload var " + pe + " is already declared."); 
						}
						env = env.addAnnotDataType(src, adt); 
						env = env.addDataTypeVarName(dest, adt.var);
					}
					else //if (apt.isAnnotVarName())
					{
						AssrtDataTypeVar v = (AssrtDataTypeVar) apt;
						if (!env.isDataTypeVarKnown(src, v))
						{
							throw new ScribException("Payload var " + pe + " is not in scope for role: " + src);
						}
						env = env.addDataTypeVarName(dest, v);
					}
				}
			}* /
		}
		else
		{
			throw new RuntimeException("[assrt] TODO: " + ca.msg);
		}
		
		checker.pushEnv(env);
		return ca; 
	}
	*/
}

package org.scribble.ext.assrt.core.ast;

import java.util.Map;

import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGActionKind;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGChoice;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGEnd;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGRec;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGRecVar;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGType;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLActionKind;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLChoice;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLRec;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLRecVar;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.ext.assrt.sesstype.name.AssrtAnnotDataType;
import org.scribble.sesstype.name.Op;
import org.scribble.sesstype.name.RecVar;
import org.scribble.sesstype.name.Role;


public class AssrtCoreAstFactory
{
	//public static final F17AstFactory FACTORY = new F17AstFactory();
	
	public AssrtCoreAstFactory()
	{
		
	}
	
	// Pre: not null
	public AssrtCoreAction action(Op op, AssrtAnnotDataType pay, AssrtAssertion ass)
	//public AssrtCoreAction action(OpNode op, AssrtAnnotDataTypeElem<DataTypeKind> pay, AssrtAssertion ass)
	{
		return new AssrtCoreAction(op, pay, ass);
	}
	
	public AssrtCoreGChoice AssrtCoreGChoice(Role src, AssrtCoreGActionKind kind, Role dest, Map<AssrtCoreAction, AssrtCoreGType> cases)
	{
		return new AssrtCoreGChoice(src, kind, dest, cases);
	}
	
	/*public F17GDisconnect GDisconnect(Role src, Role dest)
	{
		return new F17GDisconnect(src, dest);
	}*/
	
	public AssrtCoreGRec AssrtCoreGRec(RecVar recvar, AssrtCoreGType body)
	{
		return new AssrtCoreGRec(recvar, body);
	}
	
	public AssrtCoreGRecVar AssrtCoreGRecVar(RecVar var)
	{
		return new AssrtCoreGRecVar(var);
	}

	public AssrtCoreGEnd AssrtCoreGEnd()
	{
		return AssrtCoreGEnd.END;
	}

	public AssrtCoreLChoice AssrtCoreLChoice(Role role, AssrtCoreLActionKind kind, Map<AssrtCoreAction, AssrtCoreLType> cases)
	{
		return new AssrtCoreLChoice(role, kind, cases);
	}
	
	/*public AssrtCoreLSend LSend(Role self, Role peer, Op op, Payload pay)
	{
		return new AssrtCoreLSend(self, peer, op, pay);
	}
	
	public AssrtCoreLReceive LReceive(Role self, Role peer, Op op, Payload pay)
	{
		return new AssrtCoreLReceive(self, peer, op, pay);
	}
	
	public AssrtCoreLConnect LConnect(Role self, Role peer, Op op, Payload pay)
	{
		return new AssrtCoreLConnect(self, peer, op, pay);
	}
	
	public AssrtCoreLAccept LAccept(Role self, Role peer, Op op, Payload pay)
	{
		return new AssrtCoreLAccept(self, peer, op, pay);
	}*/
	
	/*public AssrtCoreLDisconnect LDisconnect(Role self, Role peer)
	{
		return new AssrtCoreLDisconnect(self, peer);
	}*/
	
	public AssrtCoreLRec AssrtCoreLRec(RecVar recvar, AssrtCoreLType body)
	{
		return new AssrtCoreLRec(recvar, body);
	}
	
	public AssrtCoreLRecVar AssrtCoreLRecVar(RecVar var)
	{
		return new AssrtCoreLRecVar(var);
	}

	public AssrtCoreLEnd AssrtCoreLEnd()
	{
		//return new AssrtCoreLEnd();
		return AssrtCoreLEnd.END;
	}
}

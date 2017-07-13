package org.scribble.ext.assrt.core.ast;

import java.util.Map;

import org.scribble.ast.name.simple.OpNode;
import org.scribble.ext.assrt.ast.AssrtAnnotDataTypeElem;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGChoice;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGEnd;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGRec;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGRecVar;
import org.scribble.ext.assrt.core.ast.global.AssrtCoreGType;
import org.scribble.ext.assrt.core.ast.global.action.AssrtCoreGActionKind;
import org.scribble.sesstype.kind.DataTypeKind;
import org.scribble.sesstype.name.RecVar;
import org.scribble.sesstype.name.Role;


public class AssrtCoreAstFactory
{
	//public static final F17AstFactory FACTORY = new F17AstFactory();
	
	public AssrtCoreAstFactory()
	{
		
	}
	
	//public AssrtCoreAction action(Op op, AssrtAnnotDataType pay, AssrtAssertion ass)
	public AssrtCoreAction action(OpNode op, AssrtAnnotDataTypeElem<DataTypeKind> pay, AssrtAssertion ass)
	{
		return new AssrtCoreAction(op, pay, ass);
	}
	
	public AssrtCoreGChoice GChoice(Role src, AssrtCoreGActionKind kind, Role dest, Map<AssrtCoreAction, AssrtCoreGType> cases)
	{
		return new AssrtCoreGChoice(src, kind, dest, cases);
	}
	
	/*public F17GDisconnect GDisconnect(Role src, Role dest)
	{
		return new F17GDisconnect(src, dest);
	}*/
	
	public AssrtCoreGRec GRec(RecVar recvar, AssrtCoreGType body)
	{
		return new AssrtCoreGRec(recvar, body);
	}
	
	public AssrtCoreGRecVar GRecVar(RecVar var)
	{
		return new AssrtCoreGRecVar(var);
	}

	public AssrtCoreGEnd GEnd()
	{
		return AssrtCoreGEnd.END;
	}

	/*
	public F17LChoice LChoice(Map<F17LAction, F17LType> cases)
	{
		return new F17LChoice(cases);
	}
	
	public F17LSend LSend(Role self, Role peer, Op op, Payload pay)
	{
		return new F17LSend(self, peer, op, pay);
	}
	
	public F17LReceive LReceive(Role self, Role peer, Op op, Payload pay)
	{
		return new F17LReceive(self, peer, op, pay);
	}
	
	public F17LConnect LConnect(Role self, Role peer, Op op, Payload pay)
	{
		return new F17LConnect(self, peer, op, pay);
	}
	
	public F17LAccept LAccept(Role self, Role peer, Op op, Payload pay)
	{
		return new F17LAccept(self, peer, op, pay);
	}
	
	public F17LDisconnect LDisconnect(Role self, Role peer)
	{
		return new F17LDisconnect(self, peer);
	}
	
	public F17LRec LRec(RecVar recvar, F17LType body)
	{
		return new F17LRec(recvar, body);
	}
	
	public F17LRecVar LRecVar(RecVar var)
	{
		return new F17LRecVar(var);
	}

	public F17LEnd LEnd()
	{
		//return new F17LEnd();
		return F17LEnd.END;
	}*/
}

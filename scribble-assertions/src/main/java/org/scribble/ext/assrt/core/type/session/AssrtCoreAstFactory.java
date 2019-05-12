package org.scribble.ext.assrt.core.type.session;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.core.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGActionKind;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGChoice;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGEnd;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGRec;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGRecVar;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGType;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLActionKind;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLChoice;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLRec;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLRecVar;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLType;
import org.scribble.type.name.Op;
import org.scribble.type.name.RecVar;
import org.scribble.type.name.Role;


public class AssrtCoreAstFactory
{
	//public static final F17AstFactory FACTORY = new F17AstFactory();
	
	public AssrtCoreAstFactory()
	{
		
	}
	
	// Pre: not null
	//public AssrtCoreMessage AssrtCoreAction(Op op, AssrtAnnotDataType pay, AssrtBoolFormula ass)
	public AssrtCoreMsg AssrtCoreAction(Op op, List<AssrtAnnotDataType> pays, AssrtBoolFormula ass)
	{
		return new AssrtCoreMsg(op, pays, ass);
	}
	
	public AssrtCoreGChoice AssrtCoreGChoice(Role src, AssrtCoreGActionKind kind, Role dest, Map<AssrtCoreMsg, AssrtCoreGType> cases)
	{
		return new AssrtCoreGChoice(src, kind, dest, cases);
	}
	
	/*public F17GDisconnect GDisconnect(Role src, Role dest)
	{
		return new F17GDisconnect(src, dest);
	}*/
	
	public AssrtCoreGRec AssrtCoreGRec(RecVar recvar,
			//AssrtDataTypeVar annot, AssrtArithFormula init,
			LinkedHashMap<AssrtDataTypeVar, AssrtArithFormula> annotvars,
			AssrtCoreGType body,
			AssrtBoolFormula ass)
	{
		//return new AssrtCoreGRec(recvar, annot, init, body);
		return new AssrtCoreGRec(recvar, annotvars, body,
				ass);
	}
	
	//public AssrtCoreGRecVar AssrtCoreGRecVar(RecVar var, AssrtArithFormula expr)
	public AssrtCoreGRecVar AssrtCoreGRecVar(RecVar recvar, List<AssrtArithFormula> annotexprs)
	{
		return new AssrtCoreGRecVar(recvar, annotexprs);
	}

	public AssrtCoreGEnd AssrtCoreGEnd()
	{
		return AssrtCoreGEnd.END;
	}

	public AssrtCoreLChoice AssrtCoreLChoice(Role role, AssrtCoreLActionKind kind, Map<AssrtCoreMsg, AssrtCoreLType> cases)
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
	
	public AssrtCoreLRec AssrtCoreLRec(RecVar recvar, 
			//AssrtDataTypeVar annot, AssrtArithFormula init,
			LinkedHashMap<AssrtDataTypeVar, AssrtArithFormula> annotvars,
			AssrtCoreLType body,
			AssrtBoolFormula ass)
	{
		//return new AssrtCoreLRec(recvar, annot, init, body);
		return new AssrtCoreLRec(recvar, annotvars, body,
				ass);
	}
	
	//public AssrtCoreLRecVar AssrtCoreLRecVar(RecVar var, AssrtArithFormula expr)
	public AssrtCoreLRecVar AssrtCoreLRecVar(RecVar recvar, List<AssrtArithFormula> annotexprs)
	{
		return new AssrtCoreLRecVar(recvar, annotexprs);
	}

	public AssrtCoreLEnd AssrtCoreLEnd()
	{
		//return new AssrtCoreLEnd();
		return AssrtCoreLEnd.END;
	}
}

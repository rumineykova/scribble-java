package org.scribble.ext.assrt.core.ast;

import java.util.List;
import java.util.Map;

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
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
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
	public AssrtCoreAction AssrtCoreAction(Op op, AssrtAnnotDataType pay, AssrtBoolFormula ass)
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
	
	public AssrtCoreGRec AssrtCoreGRec(RecVar recvar,
			//AssrtDataTypeVar annot, AssrtArithFormula init,
			Map<AssrtDataTypeVar, AssrtArithFormula> annotvars,
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
	
	public AssrtCoreLRec AssrtCoreLRec(RecVar recvar, 
			//AssrtDataTypeVar annot, AssrtArithFormula init,
			Map<AssrtDataTypeVar, AssrtArithFormula> annotvars,
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

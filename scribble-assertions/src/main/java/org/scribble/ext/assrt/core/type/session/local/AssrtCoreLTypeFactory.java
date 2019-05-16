package org.scribble.ext.assrt.core.type.session.local;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.core.type.session.AssrtCoreMsg;


public class AssrtCoreLTypeFactory
{
	public AssrtCoreLTypeFactory()
	{
		
	}
	
	public AssrtCoreLChoice AssrtCoreLChoice(CommonTree source, Role role,
			AssrtCoreLActionKind kind, Map<AssrtCoreMsg, AssrtCoreLType> cases)
	{
		return new AssrtCoreLChoice(source, role, kind, cases);
	}
	
	public AssrtCoreLRec AssrtCoreLRec(CommonTree source, RecVar recvar,
			LinkedHashMap<AssrtDataTypeVar, AssrtArithFormula> annotvars,
			AssrtCoreLType body, AssrtBoolFormula ass)
	{
		return new AssrtCoreLRec(source, recvar, annotvars, body,
				ass);
	}
	
	public AssrtCoreLRecVar AssrtCoreLRecVar(CommonTree source, RecVar recvar,
			List<AssrtArithFormula> annotexprs)
	{
		return new AssrtCoreLRecVar(source, recvar, annotexprs);
	}

	public AssrtCoreLEnd AssrtCoreLEnd()
	{
		return AssrtCoreLEnd.END;
	}
}

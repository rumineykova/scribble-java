package org.scribble.ext.assrt.core.type.session.global;

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


public class AssrtCoreGTypeFactory
{
	public AssrtCoreGTypeFactory()
	{
		
	}
	
	public AssrtCoreGChoice AssrtCoreGChoice(CommonTree source, Role src,
			AssrtCoreGActionKind kind, Role dest,
			Map<AssrtCoreMsg, AssrtCoreGType> cases)
	{
		return new AssrtCoreGChoice(source, src, kind, dest, cases);
	}
	
	public AssrtCoreGRec AssrtCoreGRec(CommonTree source, RecVar recvar,
			LinkedHashMap<AssrtDataTypeVar, AssrtArithFormula> annotvars,
			AssrtCoreGType body,
			AssrtBoolFormula ass)
	{
		return new AssrtCoreGRec(source, recvar, annotvars, body,
				ass);
	}
	
	public AssrtCoreGRecVar AssrtCoreGRecVar(CommonTree source, RecVar recvar,
			List<AssrtArithFormula> annotexprs)
	{
		return new AssrtCoreGRecVar(source, recvar, annotexprs);
	}

	public AssrtCoreGEnd AssrtCoreGEnd()
	{
		return AssrtCoreGEnd.END;
	}
}

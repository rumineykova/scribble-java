package org.scribble.ext.assrt.core.ast.local;

import java.util.LinkedHashMap;

import org.scribble.ext.assrt.core.ast.AssrtCoreRec;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.type.name.RecVar;

public class AssrtCoreLRec extends AssrtCoreRec<AssrtCoreLType> implements AssrtCoreLType
{
	public AssrtCoreLRec(RecVar recvar, //AssrtDataTypeVar annot, AssrtArithFormula init,
			LinkedHashMap<AssrtDataTypeVar, AssrtArithFormula> annotvars,
			AssrtCoreLType body,
			AssrtBoolFormula ass)
	{
		//super(recvar, annot, init, body);
		super(recvar, annotvars, body,
				ass);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreLRec))
		{
			return false;
		}
		return super.equals(obj);  // Does canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreLRec;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 2389;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
}

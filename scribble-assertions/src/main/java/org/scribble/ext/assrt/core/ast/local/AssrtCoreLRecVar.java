package org.scribble.ext.assrt.core.ast.local;

import org.scribble.ext.assrt.core.ast.AssrtCoreRecVar;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.type.name.RecVar;

	
// FIXME: hashCode/equals -- already done?
public class AssrtCoreLRecVar extends AssrtCoreRecVar implements AssrtCoreLType
{
	public AssrtCoreLRecVar(RecVar var, AssrtArithFormula expr)
	{
		super(var, expr);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreLRecVar))
		{
			return false;
		}
		return super.equals(obj);  // Does canEquals
	}

	@Override
	public int hashCode()
	{
		int hash = 2417;
		hash = 31*hash + super.hashCode();
		return hash;
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreLRecVar;
	}
}

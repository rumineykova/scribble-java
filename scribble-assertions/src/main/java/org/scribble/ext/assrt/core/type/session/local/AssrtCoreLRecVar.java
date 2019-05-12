package org.scribble.ext.assrt.core.type.session.local;

import java.util.List;

import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.session.AssrtCoreRecVar;
import org.scribble.type.name.RecVar;

	
// FIXME: hashCode/equals -- already done?
public class AssrtCoreLRecVar extends AssrtCoreRecVar implements AssrtCoreLType
{
	//public AssrtCoreLRecVar(RecVar var, AssrtArithFormula expr)
	public AssrtCoreLRecVar(RecVar var, List<AssrtArithFormula> annotexprs)
	{
		super(var, annotexprs);
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

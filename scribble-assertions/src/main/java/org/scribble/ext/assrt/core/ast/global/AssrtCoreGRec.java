package org.scribble.ext.assrt.core.ast.global;

import org.scribble.ext.assrt.core.ast.AssrtCoreRec;
import org.scribble.sesstype.name.RecVar;

public class AssrtCoreGRec extends AssrtCoreRec<AssrtCoreGType> implements AssrtCoreGType
{
	public AssrtCoreGRec(RecVar recvar, AssrtCoreGType body)
	{
		super(recvar, body);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreGRec))
		{
			return false;
		}
		return super.equals(obj);  // Does canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreGRec;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 2333;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
}

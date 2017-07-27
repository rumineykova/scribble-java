package org.scribble.ext.assrt.core.ast.local;

import org.scribble.ext.assrt.core.ast.AssrtCoreRec;
import org.scribble.type.name.RecVar;

public class AssrtCoreLRec extends AssrtCoreRec<AssrtCoreLType> implements AssrtCoreLType
{
	public AssrtCoreLRec(RecVar recvar, AssrtCoreLType body)
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

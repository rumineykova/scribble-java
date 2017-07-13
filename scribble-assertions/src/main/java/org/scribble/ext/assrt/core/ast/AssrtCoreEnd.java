package org.scribble.ext.assrt.core.ast;


public abstract class AssrtCoreEnd implements AssrtCoreType
{
	@Override 
	public String toString()
	{
		return "end";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreEnd))
		{
			return false;
		}
		return ((AssrtCoreEnd) obj).canEquals(this);
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreEnd;
	}

	@Override
	public int hashCode()
	{
		return 997;
	}
}

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

	public abstract boolean canEquals(Object o);

	@Override
	public int hashCode()
	{
		return 31*2447;
	}
}

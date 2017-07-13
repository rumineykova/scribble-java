package org.scribble.ext.assrt.core.ast;

import org.scribble.sesstype.name.RecVar;


public abstract class AssrtCoreRecVar implements AssrtCoreType
{
	public final RecVar var;
	
	public AssrtCoreRecVar(RecVar var)
	{
		this.var = var;
	}

	@Override 
	public String toString()
	{
		return this.var.toString();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreRecVar))
		{
			return false;
		}
		AssrtCoreRecVar them = (AssrtCoreRecVar) obj;
		return them.canEquals(this) && this.var.equals(them.var);
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreRecVar;
	}

	@Override
	public int hashCode()
	{
		return this.var.hashCode();
	}
}

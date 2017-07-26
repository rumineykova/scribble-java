package org.scribble.ext.assrt.core.ast;

import org.scribble.type.name.RecVar;

public abstract class AssrtCoreRec<B extends AssrtCoreType> implements AssrtCoreType
{
	public final RecVar recvar;  // FIXME: RecVarNode?  (Cf. AssrtCoreAction.op/pay)
	public final B body;
	
	public AssrtCoreRec(RecVar recvar, B body)
	{
		this.recvar = recvar;
		this.body = body;
	}
	
	@Override
	public String toString()
	{
		return "mu " + this.recvar + "." + this.body;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreRec))
		{
			return false;
		}
		AssrtCoreRec<?> them = (AssrtCoreRec<?>) obj;
		return them.canEquals(this) && this.recvar.equals(them.recvar) && this.body.equals(them.body);
				// FIXME: check B is equal
	}
	
	@Override
	public abstract boolean canEquals(Object o);
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((recvar == null) ? 0 : recvar.hashCode());
		return result;
	}
}

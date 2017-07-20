package org.scribble.ext.assrt.core.ast.local;

import org.scribble.ext.assrt.core.ast.AssrtCoreRecVar;
import org.scribble.sesstype.name.RecVar;

	
// FIXME: hashCode/equals
public class AssrtCoreLRecVar extends AssrtCoreRecVar implements AssrtCoreLType
{
	public AssrtCoreLRecVar(RecVar var)
	{
		super(var);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreLRecVar))
		{
			return false;
		}
		return ((AssrtCoreLRecVar) obj).canEquals(this);
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

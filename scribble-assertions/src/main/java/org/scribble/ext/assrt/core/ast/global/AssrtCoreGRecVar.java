package org.scribble.ext.assrt.core.ast.global;

import org.scribble.ext.assrt.core.ast.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.ast.AssrtCoreRecVar;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLRecVar;
import org.scribble.sesstype.name.RecVar;
import org.scribble.sesstype.name.Role;

	
// FIXME: hashCode/equals
public class AssrtCoreGRecVar extends AssrtCoreRecVar implements AssrtCoreGType
{
	public AssrtCoreGRecVar(RecVar var)
	{
		super(var);
	}

	@Override
	public AssrtCoreLRecVar project(AssrtCoreAstFactory af, Role subj)
	{
		return af.AssrtCoreLRecVar(this.var);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreGRecVar))
		{
			return false;
		}
		return ((AssrtCoreGRecVar) obj).canEquals(this);
	}

	@Override
	public int hashCode()
	{
		int hash = 2411;
		hash = 31*hash + super.hashCode();
		return hash;
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreGRecVar;
	}
}

package org.scribble.ext.assrt.core.ast.local;

import java.util.Map;

import org.scribble.ext.assrt.core.ast.AssrtCoreAction;
import org.scribble.ext.assrt.core.ast.AssrtCoreChoice;
import org.scribble.sesstype.kind.Local;
import org.scribble.sesstype.name.Role;

public class AssrtCoreLChoice extends AssrtCoreChoice<AssrtCoreAction, AssrtCoreLType, Local> implements AssrtCoreLType
{
	public AssrtCoreLChoice(Role role, AssrtCoreLActionKind kind, Map<AssrtCoreAction, AssrtCoreLType> cases)
	{
		super(role, kind, cases);
	}
	
	@Override
	public AssrtCoreLActionKind getKind()
	{
		return (AssrtCoreLActionKind) super.kind;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 2399;
		hash = 31 * hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreLChoice))
		{
			return false;
		}
		return super.equals(obj);  // Does canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreLChoice;
	}

	@Override
	public String toString()
	{
		return this.role.toString() + this.kind + casesToString();
	}
}

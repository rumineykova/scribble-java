package org.scribble.ext.assrt.core.ast.global;

import java.util.Map;

import org.scribble.ext.assrt.core.ast.AssrtCoreAction;
import org.scribble.ext.assrt.core.ast.AssrtCoreChoice;
import org.scribble.ext.assrt.core.ast.global.action.AssrtCoreGActionKind;
import org.scribble.sesstype.name.Role;

public class AssrtCoreGChoice extends AssrtCoreChoice<AssrtCoreAction, AssrtCoreGType> implements AssrtCoreGType
{
	public AssrtCoreGChoice(Role subj, AssrtCoreGActionKind kind, Role dest, Map<AssrtCoreAction, AssrtCoreGType> cases)
	{
		super(subj, kind, dest, cases);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 2339;
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
		if (!(obj instanceof AssrtCoreGChoice))
		{
			return false;
		}
		return super.equals(obj);  // Does canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreGChoice;
	}
}

package org.scribble.ext.assrt.core.type.name;

import org.scribble.core.type.name.AbstractName;
import org.scribble.ext.assrt.core.type.kind.AssrtIntVarKind;

public class AssrtDataVar extends AbstractName<AssrtIntVarKind> implements AssrtPayElemType<AssrtIntVarKind>
{
	private static final long serialVersionUID = 1L;

	public AssrtDataVar(String simplename)
	{
		super(AssrtIntVarKind.KIND, simplename);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtDataVar))
		{
			return false;
		}
		return super.equals(o);  // Checks canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtDataVar;
	}

	@Override
	public int hashCode()
	{
		int hash = 5519;
		hash = 31 * super.hashCode();
		return hash;
	}
	
	@Override
	public boolean isAnnotVarName()
	{
		return true;
	}
}

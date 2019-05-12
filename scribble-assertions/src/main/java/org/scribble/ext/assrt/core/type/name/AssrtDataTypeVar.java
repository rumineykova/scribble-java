package org.scribble.ext.assrt.core.type.name;

import org.scribble.core.type.name.AbstractName;
import org.scribble.ext.assrt.core.type.kind.AssrtVarNameKind;

public class AssrtDataTypeVar extends AbstractName<AssrtVarNameKind> implements AssrtPayloadElemType<AssrtVarNameKind>
{
	private static final long serialVersionUID = 1L;

	public AssrtDataTypeVar(String simplename)
	{
		super(AssrtVarNameKind.KIND, simplename);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtDataTypeVar))
		{
			return false;
		}
		return super.equals(o);  // Checks canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtDataTypeVar;
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

package org.scribble.ext.assrt.sesstype.name;

import org.scribble.ext.assrt.sesstype.kind.AssrtVarNameKind;
import org.scribble.sesstype.name.AbstractName;

public class AssrtDataTypeVar extends AbstractName<AssrtVarNameKind> implements AssrtPayloadType<AssrtVarNameKind>
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
		AssrtDataTypeVar n = (AssrtDataTypeVar) o;
		return n.canEqual(this) && super.equals(o);
	}
	
	public boolean canEqual(Object o)
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

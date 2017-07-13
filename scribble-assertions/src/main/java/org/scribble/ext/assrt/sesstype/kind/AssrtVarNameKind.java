package org.scribble.ext.assrt.sesstype.kind;

import org.scribble.sesstype.kind.AbstractKind;
import org.scribble.sesstype.kind.DataTypeKind;
import org.scribble.sesstype.kind.PayloadTypeKind;

public class AssrtVarNameKind extends AbstractKind implements PayloadTypeKind
{
	public static final AssrtVarNameKind KIND = new AssrtVarNameKind();
	
	protected AssrtVarNameKind()
	{

	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof DataTypeKind))
		{
			return false;
		}
		return ((DataTypeKind) o).canEqual(this);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtVarNameKind;
	}
}

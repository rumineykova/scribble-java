package org.scribble.ext.assrt.type.kind;

import org.scribble.type.kind.AbstractKind;
import org.scribble.type.kind.DataTypeKind;

public class AssrtSortKind extends AbstractKind
{
	public static final AssrtSortKind KIND = new AssrtSortKind();
	
	protected AssrtSortKind()
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
		return o instanceof AssrtSortKind;
	}
}

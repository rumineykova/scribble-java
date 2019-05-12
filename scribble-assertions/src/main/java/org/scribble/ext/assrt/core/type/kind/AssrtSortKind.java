package org.scribble.ext.assrt.core.type.kind;

import org.scribble.core.type.kind.AbstractKind;
import org.scribble.core.type.kind.DataKind;

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
		if (!(o instanceof DataKind))
		{
			return false;
		}
		return ((DataKind) o).canEqual(this);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtSortKind;
	}
}

package org.scribble.ext.assrt.core.type.kind;

import org.scribble.core.type.kind.AbstractKind;
import org.scribble.core.type.kind.DataKind;
import org.scribble.core.type.kind.PayElemKind;

public class AssrtIntVarNameKind extends AbstractKind implements PayElemKind
{
	public static final AssrtIntVarNameKind KIND = new AssrtIntVarNameKind();
	
	protected AssrtIntVarNameKind()
	{
		super("AssrtIntVar");
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
		return o instanceof AssrtIntVarNameKind;
	}
}

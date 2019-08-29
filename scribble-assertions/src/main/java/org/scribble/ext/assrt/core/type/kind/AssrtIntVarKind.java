package org.scribble.ext.assrt.core.type.kind;

import org.scribble.core.type.kind.AbstractKind;
import org.scribble.core.type.kind.DataKind;
import org.scribble.core.type.kind.PayElemKind;

// Cf. RecVarKind
public class AssrtIntVarKind extends AbstractKind implements PayElemKind
{
	public static final AssrtIntVarKind KIND = new AssrtIntVarKind();
	
	protected AssrtIntVarKind()
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
		return ((DataKind) o).canEquals(this);
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtIntVarKind;
	}
}

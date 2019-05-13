package org.scribble.ext.assrt.core.type.session;

import org.scribble.core.type.kind.ProtoKind;

public abstract class AssrtCoreEnd<K extends ProtoKind>
		extends AssrtCoreSTypeBase<K>
{
	public AssrtCoreEnd()
	{
		super(null);
	}

	@Override 
	public String toString()
	{
		return "end";
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof AssrtCoreEnd))
		{
			return false;
		}
		return super.equals(o);  // Checks canEquals
	}

	@Override
	public int hashCode()
	{
		return 31*2447;
	}
}

package org.scribble.ext.assrt.core.type.kind;

import org.scribble.core.type.kind.AbstractKind;
import org.scribble.core.type.kind.PayElemKind;

//public class AssrtAnnotPayloadElemKind extends AbstractKind implements PayloadTypeKind
public class AssrtAnnotDataKind extends AbstractKind //extends DataTypeKind  // No: extending DataTypeKind doesn't mean anything for these Kind objects themselves
			implements PayElemKind
{
	public static final AssrtAnnotDataKind KIND 
			= new AssrtAnnotDataKind();
	
	protected AssrtAnnotDataKind()
	{
		super("AssrtAnnotData");
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
		if (!(o instanceof AssrtAnnotDataKind))
		{
			return false;
		}
		return ((AssrtAnnotDataKind) o).canEquals(this);
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtAnnotDataKind;
	}
}

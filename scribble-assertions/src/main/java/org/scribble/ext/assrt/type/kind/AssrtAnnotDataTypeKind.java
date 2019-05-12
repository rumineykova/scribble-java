package org.scribble.ext.assrt.type.kind;

import org.scribble.core.type.kind.AbstractKind;
import org.scribble.core.type.kind.PayElemKind;

//public class AssrtAnnotPayloadElemKind extends AbstractKind implements PayloadTypeKind
public class AssrtAnnotDataTypeKind extends AbstractKind //extends DataTypeKind  // No: extending DataTypeKind doesn't mean anything for these Kind objects themselves
			implements PayElemKind
{
	public static final AssrtAnnotDataTypeKind KIND 
			= new AssrtAnnotDataTypeKind();
	
	protected AssrtAnnotDataTypeKind()
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
		if (!(o instanceof AssrtAnnotDataTypeKind))
		{
			return false;
		}
		return ((AssrtAnnotDataTypeKind) o).canEqual(this);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtAnnotDataTypeKind;
	}
}

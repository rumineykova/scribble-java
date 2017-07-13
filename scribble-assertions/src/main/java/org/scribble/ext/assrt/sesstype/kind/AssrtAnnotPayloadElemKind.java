package org.scribble.ext.assrt.sesstype.kind;

import org.scribble.sesstype.kind.AbstractKind;
import org.scribble.sesstype.kind.PayloadTypeKind;

public class AssrtAnnotPayloadElemKind extends AbstractKind implements PayloadTypeKind
{
	public static final AssrtAnnotPayloadElemKind KIND = new AssrtAnnotPayloadElemKind();
	
	protected AssrtAnnotPayloadElemKind()
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
		if (!(o instanceof AssrtAnnotPayloadElemKind))
		{
			return false;
		}
		return ((AssrtAnnotPayloadElemKind) o).canEqual(this);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtAnnotPayloadElemKind;
	}
}

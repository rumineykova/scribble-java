package org.scribble.sesstype.kind;

public class AAnnotPayloadElemKind extends AbstractKind implements PayloadTypeKind
{
	public static final AAnnotPayloadElemKind KIND = new AAnnotPayloadElemKind();
	
	protected AAnnotPayloadElemKind()
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
		if (!(o instanceof AAnnotPayloadElemKind))
		{
			return false;
		}
		return ((AAnnotPayloadElemKind) o).canEqual(this);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AAnnotPayloadElemKind;
	}
}

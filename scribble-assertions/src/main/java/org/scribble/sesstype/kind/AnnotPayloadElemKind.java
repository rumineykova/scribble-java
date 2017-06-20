package org.scribble.sesstype.kind;

public class AnnotPayloadElemKind extends AbstractKind implements PayloadTypeKind
{
	public static final AnnotPayloadElemKind KIND = new AnnotPayloadElemKind();
	
	protected AnnotPayloadElemKind()
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
		if (!(o instanceof AnnotPayloadElemKind))
		{
			return false;
		}
		return ((AnnotPayloadElemKind) o).canEqual(this);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AnnotPayloadElemKind;
	}
}

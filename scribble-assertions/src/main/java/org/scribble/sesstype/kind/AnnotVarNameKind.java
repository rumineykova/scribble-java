package org.scribble.sesstype.kind;

public class AnnotVarNameKind extends AbstractKind implements PayloadTypeKind
{
	public static final AnnotVarNameKind KIND = new AnnotVarNameKind();
	
	protected AnnotVarNameKind()
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
		return o instanceof DataTypeKind;
	}
}

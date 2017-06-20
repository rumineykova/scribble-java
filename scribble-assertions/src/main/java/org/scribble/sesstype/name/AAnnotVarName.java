package org.scribble.sesstype.name;
import org.scribble.sesstype.kind.AAnnotVarNameKind;


public class AAnnotVarName extends AbstractName<AAnnotVarNameKind> implements APayloadType<AAnnotVarNameKind>
{
	private static final long serialVersionUID = 1L;

	public AAnnotVarName(String simplename)
	{
		super(AAnnotVarNameKind.KIND, simplename);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AAnnotVarName))
		{
			return false;
		}
		AAnnotVarName n = (AAnnotVarName) o;
		return n.canEqual(this) && super.equals(o);
	}
	
	public boolean canEqual(Object o)
	{
		return o instanceof AAnnotVarName;
	}

	@Override
	public int hashCode()
	{
		int hash = 5519;
		hash = 31 * super.hashCode();
		return hash;
	}
	
	@Override
	public boolean isAnnotPayloadInScope()
	{
		return true;
	}
}

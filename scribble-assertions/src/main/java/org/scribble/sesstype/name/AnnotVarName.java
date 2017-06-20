package org.scribble.sesstype.name;
import org.scribble.sesstype.kind.AnnotVarNameKind;


// Potentially qualified/canonical payload type name; not the AST primitive identifier
public class AnnotVarName extends AbstractName<AnnotVarNameKind> implements PayloadType<AnnotVarNameKind>
{
	private static final long serialVersionUID = 1L;

	public AnnotVarName(String simplename)
	{
		super(AnnotVarNameKind.KIND, simplename);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AnnotVarName))
		{
			return false;
		}
		AnnotVarName n = (AnnotVarName) o;
		return n.canEqual(this) && super.equals(o);
	}
	
	public boolean canEqual(Object o)
	{
		return o instanceof AnnotVarName;
	}

	@Override
	public int hashCode()
	{
		int hash = 2769;
		hash = 31 * super.hashCode();
		return hash;
	}
	
	@Override
	public boolean isAnnotPayloadInScope()
	{
		return true;
	}
}

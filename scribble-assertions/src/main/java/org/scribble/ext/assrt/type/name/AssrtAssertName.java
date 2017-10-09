package org.scribble.ext.assrt.type.name;

import org.scribble.ext.assrt.type.kind.AssrtAssertKind;
import org.scribble.type.name.MemberName;
import org.scribble.type.name.ModuleName;


public class AssrtAssertName extends MemberName<AssrtAssertKind>
{
	private static final long serialVersionUID = 1L;

	public AssrtAssertName(ModuleName modname, AssrtAssertName membname)
	{
		super(AssrtAssertKind.KIND, modname, membname);
	}
	
	public AssrtAssertName(String simplename)
	{
		super(AssrtAssertKind.KIND, simplename);
	}

	public boolean isDataType()
	{
		return true;
	}

	@Override
	public AssrtAssertKind getKind()
	{
		return AssrtAssertKind.KIND;
	}

	@Override
	public AssrtAssertName getSimpleName()
	{
		return new AssrtAssertName(getLastElement());
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtAssertName))
		{
			return false;
		}
		AssrtAssertName n = (AssrtAssertName) o;
		return n.canEqual(this) && super.equals(o);
	}
	
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtAssertName;
	}

	@Override
	public int hashCode()
	{
		int hash = 7577;
		hash = 31 * super.hashCode();
		return hash;
	}
}

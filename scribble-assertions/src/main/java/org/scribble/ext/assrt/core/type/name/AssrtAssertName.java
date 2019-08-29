package org.scribble.ext.assrt.core.type.name;

import org.scribble.core.type.name.MemberName;
import org.scribble.core.type.name.ModuleName;
import org.scribble.ext.assrt.core.type.kind.AssrtAssertKind;


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
		return super.equals(o);  // Checks canEqualss
	}
	
	@Override
	public boolean canEquals(Object o)
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

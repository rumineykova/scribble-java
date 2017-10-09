package org.scribble.ext.assrt.type.kind;

import org.scribble.type.kind.AbstractKind;
import org.scribble.type.kind.ModuleMemberKind;
import org.scribble.type.kind.NonProtocolKind;

public class AssrtAssertKind extends AbstractKind implements
		NonProtocolKind,  // For NonProtocolDecl -- maybe better to incorporate that aspect into ModuleMemberKind though (and use NonProtocolDecl for "actual typing")
		ModuleMemberKind
{
	public static final AssrtAssertKind KIND = new AssrtAssertKind();
	
	protected AssrtAssertKind()
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
		if (!(o instanceof AssrtAssertKind))
		{
			return false;
		}
		return ((AssrtAssertKind) o).canEqual(this);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtAssertKind;
	}
}

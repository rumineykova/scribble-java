package org.scribble.ext.assrt.core.type.session.local;

import org.scribble.ext.assrt.core.type.session.AssrtCoreEnd;


public class AssrtCoreLEnd extends AssrtCoreEnd implements AssrtCoreLType
{
	public static final AssrtCoreLEnd END = new AssrtCoreLEnd();
	
	private AssrtCoreLEnd()
	{
		
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreLEnd))
		{
			return false;
		}
		return super.equals(obj);  // Does canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreLEnd;
	}

	@Override
	public int hashCode()
	{
		return 31*2383;
	}
}

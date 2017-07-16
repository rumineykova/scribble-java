package org.scribble.ext.assrt.core.ast.global;

import org.scribble.ext.assrt.core.ast.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.ast.AssrtCoreEnd;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLEnd;
import org.scribble.sesstype.name.Role;


public class AssrtCoreGEnd extends AssrtCoreEnd implements AssrtCoreGType
{
	public static final AssrtCoreGEnd END = new AssrtCoreGEnd();
	
	private AssrtCoreGEnd()
	{
		
	}

	@Override
	public AssrtCoreLEnd project(AssrtCoreAstFactory af, Role subj)
	{
		return af.AssrtCoreLEnd();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreGEnd))
		{
			return false;
		}
		return super.equals(obj);  // Does canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreGEnd;
	}

	@Override
	public int hashCode()
	{
		return 31*2381;
	}
}

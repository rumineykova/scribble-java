package org.scribble.ext.assrt.core.type.session.global;

import java.util.Collections;
import java.util.List;

import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.core.type.session.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.type.session.AssrtCoreEnd;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLEnd;
import org.scribble.type.name.Role;


public class AssrtCoreGEnd extends AssrtCoreEnd implements AssrtCoreGType
{
	public static final AssrtCoreGEnd END = new AssrtCoreGEnd();
	
	private AssrtCoreGEnd()
	{
		
	}

	@Override
	public List<AssrtAnnotDataType> collectAnnotDataTypeVarDecls()
	{
		return Collections.emptyList();
	}

	@Override
	public AssrtCoreLEnd project(AssrtCoreAstFactory af, Role r, AssrtBoolFormula f)
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

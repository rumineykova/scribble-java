package org.scribble.ext.assrt.core.type.session.global;

import java.util.Collections;
import java.util.List;

import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataName;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSTypeFactory;
import org.scribble.ext.assrt.core.type.session.AssrtCoreEnd;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLEnd;


public class AssrtCoreGEnd extends AssrtCoreEnd<Global>
		implements AssrtCoreGType
{
	public static final AssrtCoreGEnd END = new AssrtCoreGEnd();
	
	protected AssrtCoreGEnd()
	{
		
	}

	@Override
	public List<AssrtAnnotDataName> collectAnnotDataTypeVarDecls()
	{
		return Collections.emptyList();
	}

	@Override
	public AssrtCoreLEnd project(AssrtCoreSTypeFactory af, Role r,
			AssrtBFormula f)
	{
		return af.local.AssrtCoreLEnd();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreGEnd))
		{
			return false;
		}
		return super.equals(obj);  // Checks canEquals
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

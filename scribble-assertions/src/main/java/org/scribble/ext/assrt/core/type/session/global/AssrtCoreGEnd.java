package org.scribble.ext.assrt.core.type.session.global;

import java.util.Collections;
import java.util.List;

import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.job.AssrtCore;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataName;
import org.scribble.ext.assrt.core.type.session.AssrtCoreEnd;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLTypeFactory;
import org.scribble.ext.assrt.core.visit.global.AssrtCoreGTypeInliner;


public class AssrtCoreGEnd extends AssrtCoreEnd<Global, AssrtCoreGType>
		implements AssrtCoreGType
{
	public static final AssrtCoreGEnd END = new AssrtCoreGEnd();
	
	protected AssrtCoreGEnd()
	{
		
	}

	@Override
	public AssrtCoreGType pruneRecs(AssrtCore core)
	{
		return this;
	}

	@Override
	public AssrtCoreGType inline(AssrtCoreGTypeInliner v)
	{
		return this;
	}

	@Override
	public AssrtCoreLEnd projectInlined(AssrtCore core, Role self,
			AssrtBFormula f)
	{
		return ((AssrtCoreLTypeFactory) core.config.tf.local).AssrtCoreLEnd();
	}

	@Override
	public List<AssrtAnnotDataName> collectAnnotDataVarDecls()
	{
		return Collections.emptyList();
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

package org.scribble.ext.assrt.core.type.session.local;

import java.util.LinkedHashMap;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.Local;
import org.scribble.core.type.name.RecVar;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataVar;
import org.scribble.ext.assrt.core.type.session.AssrtCoreRec;

public class AssrtCoreLRec extends AssrtCoreRec<Local, AssrtCoreLType>
		implements AssrtCoreLType
{
	protected AssrtCoreLRec(CommonTree source, RecVar rv,
			LinkedHashMap<AssrtDataVar, AssrtAFormula> avars,
			AssrtCoreLType body, AssrtBFormula bform)
	{
		super(source, rv, avars, body, bform);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreLRec))
		{
			return false;
		}
		return super.equals(obj);  // Does canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreLRec;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 2389;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
}

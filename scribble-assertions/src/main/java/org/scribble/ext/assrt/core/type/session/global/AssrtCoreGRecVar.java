package org.scribble.ext.assrt.core.type.session.global;

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataName;
import org.scribble.ext.assrt.core.type.session.AssrtCoreRecVar;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSTypeFactory;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLRecVar;

	
public class AssrtCoreGRecVar extends AssrtCoreRecVar<Global, AssrtCoreGType>
		implements AssrtCoreGType
{
	protected AssrtCoreGRecVar(CommonTree source, RecVar var,
			List<AssrtAFormula> annotexprs)
	{
		super(source, var, annotexprs);
	}

	@Override
	public AssrtCoreLRecVar projectInlined(AssrtCoreSTypeFactory af, Role self,
			AssrtBFormula f)
	{
		return af.local.AssrtCoreLRecVar(null, this.recvar, this.aforms);
	}

	@Override
	public List<AssrtAnnotDataName> collectAnnotDataVarDecls()
	{
		return Collections.emptyList();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreGRecVar))
		{
			return false;
		}
		return super.equals(obj);  // Does canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreGRecVar;
	}

	@Override
	public int hashCode()
	{
		int hash = 2411;
		hash = 31*hash + super.hashCode();
		return hash;
	}
}

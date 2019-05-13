package org.scribble.ext.assrt.core.type.session.global;

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.core.type.session.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.type.session.AssrtCoreRecVar;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLRecVar;

	
public class AssrtCoreGRecVar extends AssrtCoreRecVar<Global>
		implements AssrtCoreGType
{
	protected AssrtCoreGRecVar(CommonTree source, RecVar var,
			List<AssrtArithFormula> annotexprs)
	{
		super(source, var, annotexprs);
	}

	@Override
	public List<AssrtAnnotDataType> collectAnnotDataTypeVarDecls()
	{
		return Collections.emptyList();
	}

	@Override
	public AssrtCoreLRecVar project(AssrtCoreAstFactory af, Role r,
			AssrtBoolFormula f)
	{
		return af.local.AssrtCoreLRecVar(null, this.recvar, this.annotexprs);
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

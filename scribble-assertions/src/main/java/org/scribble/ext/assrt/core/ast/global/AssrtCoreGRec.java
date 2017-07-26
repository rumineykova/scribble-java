package org.scribble.ext.assrt.core.ast.global;

import java.util.List;

import org.scribble.ext.assrt.core.ast.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.ast.AssrtCoreRec;
import org.scribble.ext.assrt.core.ast.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLRecVar;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.ext.assrt.sesstype.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.sesstype.name.AssrtAnnotDataType;
import org.scribble.sesstype.name.RecVar;
import org.scribble.sesstype.name.Role;

public class AssrtCoreGRec extends AssrtCoreRec<AssrtCoreGType> implements AssrtCoreGType
{
	public AssrtCoreGRec(RecVar recvar, AssrtCoreGType body)
	{
		super(recvar, body);
	}

	@Override
	public List<AssrtAnnotDataType> collectAnnotDataTypes()
	{
		return this.body.collectAnnotDataTypes();
	}

	@Override
	public AssrtCoreLType project(AssrtCoreAstFactory af, Role r, AssrtBoolFormula f) throws AssrtCoreSyntaxException
	{
		AssrtCoreLType proj = this.body.project(af, r, f);
		return (proj instanceof AssrtCoreLRecVar) ? AssrtCoreLEnd.END : af.AssrtCoreLRec(this.recvar, proj);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreGRec))
		{
			return false;
		}
		return super.equals(obj);  // Does canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreGRec;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 2333;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
}

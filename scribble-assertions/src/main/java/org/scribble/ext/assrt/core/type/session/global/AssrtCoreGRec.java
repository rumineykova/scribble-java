package org.scribble.ext.assrt.core.type.session.global;

import java.util.LinkedHashMap;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.DataName;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.core.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.core.type.session.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.type.session.AssrtCoreRec;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLRecVar;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLType;

public class AssrtCoreGRec extends AssrtCoreRec<Global, AssrtCoreGType>
		implements AssrtCoreGType
{
	protected AssrtCoreGRec(CommonTree source, RecVar recvar,
			LinkedHashMap<AssrtDataTypeVar, AssrtArithFormula> annotvars,
			AssrtCoreGType body, AssrtBoolFormula ass)
	{
		super(source, recvar, annotvars, body, ass);
	}

	@Override
	public List<AssrtAnnotDataType> collectAnnotDataTypeVarDecls()
	{
		List<AssrtAnnotDataType> res = this.body.collectAnnotDataTypeVarDecls();
		this.annotvars.keySet().stream().forEachOrdered(
				v -> res.add(new AssrtAnnotDataType(v, new DataName("int"))));  // TODO: factor out int constant
		/*this.ass.getIntVars().stream().forEachOrdered(
				v -> res.add(new AssrtAnnotDataType(v, new DataType("int"))));  // No: not decls*/
		return res;
	}

	@Override
	public AssrtCoreLType project(AssrtCoreAstFactory af, Role r,
			AssrtBoolFormula f) throws AssrtCoreSyntaxException
	{
		AssrtCoreLType proj = this.body.project(af, r, f);
		return (proj instanceof AssrtCoreLRecVar) 
				? AssrtCoreLEnd.END
				: af.local.AssrtCoreLRec(null, this.recvar, this.annotvars, proj,
						this.ass);
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

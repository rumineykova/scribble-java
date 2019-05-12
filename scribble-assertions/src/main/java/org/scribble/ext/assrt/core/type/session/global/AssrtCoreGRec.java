package org.scribble.ext.assrt.core.type.session.global;

import java.util.LinkedHashMap;
import java.util.List;

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
import org.scribble.type.name.DataType;
import org.scribble.type.name.RecVar;
import org.scribble.type.name.Role;

public class AssrtCoreGRec extends AssrtCoreRec<AssrtCoreGType> implements AssrtCoreGType
{
	public AssrtCoreGRec(RecVar recvar, //AssrtDataTypeVar annot, AssrtArithFormula init,
			LinkedHashMap<AssrtDataTypeVar, AssrtArithFormula> annotvars,
			AssrtCoreGType body,
			AssrtBoolFormula ass)
	{
		super(recvar, //annot, init,
				annotvars,
				body,
				ass);
	}

	@Override
	public List<AssrtAnnotDataType> collectAnnotDataTypeVarDecls()
	{
		List<AssrtAnnotDataType> res = this.body.collectAnnotDataTypeVarDecls();
		//res.add(new AssrtAnnotDataType(this.annot, new DataType("int")));  // FIXME: factor out int constant
		/*res.addAll(this.annotvars.keySet().stream()
				.map(v -> new AssrtAnnotDataType(v, new DataType("int"))).collect(Collectors.toList()));  // FIXME: factor out int constant*/
		this.annotvars.keySet().stream().forEach(v -> res.add(new AssrtAnnotDataType(v, new DataType("int"))));  // FIXME: factor out int constant
		//this.ass.getIntVars().stream().forEach(v -> res.add(new AssrtAnnotDataType(v, new DataType("int"))));  // No: not decls
		return res;
	}

	@Override
	public AssrtCoreLType project(AssrtCoreAstFactory af, Role r, AssrtBoolFormula f) throws AssrtCoreSyntaxException
	{
		AssrtCoreLType proj = this.body.project(af, r, f);
		return (proj instanceof AssrtCoreLRecVar) ? AssrtCoreLEnd.END : af.AssrtCoreLRec(this.recvar, //this.annot, this.init,
				this.annotvars,
				proj,
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

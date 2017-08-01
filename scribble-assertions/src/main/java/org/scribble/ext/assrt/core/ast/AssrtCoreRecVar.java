package org.scribble.ext.assrt.core.ast;

import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.type.name.RecVar;


public abstract class AssrtCoreRecVar implements AssrtCoreType
{
	public final RecVar var;
	public final AssrtArithFormula expr;
	
	public AssrtCoreRecVar(RecVar var, AssrtArithFormula expr)
	{
		this.var = var;
		this.expr = expr;
	}

	@Override 
	public String toString()
	{
		return this.var.toString() + "<" + expr + ">";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreRecVar))
		{
			return false;
		}
		AssrtCoreRecVar them = (AssrtCoreRecVar) obj;
		return them.canEquals(this) && this.var.equals(them.var) && this.expr.equals(them.expr);
	}
	
	@Override
	public abstract boolean canEquals(Object o);

	@Override
	public int hashCode()
	{
		int hash = 6733;
		hash = 31*hash + this.var.hashCode();
		hash = 31*hash + this.expr.hashCode();
		return hash;
	}
}

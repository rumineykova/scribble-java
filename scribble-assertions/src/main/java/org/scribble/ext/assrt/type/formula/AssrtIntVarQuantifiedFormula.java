package org.scribble.ext.assrt.type.formula;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;

public abstract class AssrtIntVarQuantifiedFormula extends AssrtBoolFormula
{
	public final List<AssrtIntVarFormula> vars;
	public final AssrtBoolFormula expr;

	// Pre: vars non empty
	protected AssrtIntVarQuantifiedFormula(List<AssrtIntVarFormula> vars, AssrtBoolFormula expr)
	{
		this.vars = Collections.unmodifiableList(vars);
		this.expr = expr;
	}

	@Override
	public Set<AssrtDataTypeVar> getIntVars()
	{
		Set<AssrtDataTypeVar> vs = this.expr.getIntVars();
		vs.removeAll(this.vars.stream().map(v -> v.toName()).collect(Collectors.toList()));
		return vs;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtIntVarQuantifiedFormula))
		{
			return false;
		}
		AssrtIntVarQuantifiedFormula f = (AssrtIntVarQuantifiedFormula) o;
		return super.equals(this)  // Does canEqual
				&& this.vars.equals(f.vars) && this.expr.equals(f.expr);  
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtIntVarQuantifiedFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 7013;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.vars.hashCode();
		hash = 31 * hash + this.expr.hashCode();
		return hash;
	}
}

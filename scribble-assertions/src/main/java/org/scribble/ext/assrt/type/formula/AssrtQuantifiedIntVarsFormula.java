package org.scribble.ext.assrt.type.formula;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;

public abstract class AssrtQuantifiedIntVarsFormula extends AssrtBoolFormula
{
	public final List<AssrtIntVarFormula> vars;
	public final AssrtBoolFormula expr;

	// Pre: vars non empty
	protected AssrtQuantifiedIntVarsFormula(List<AssrtIntVarFormula> vars, AssrtBoolFormula expr)
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
	
	protected String bodyToString()
	{
		return "[" + this.vars.stream().map(Object::toString).collect(Collectors.joining(", ")) + "] (" + this.expr + ")";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtQuantifiedIntVarsFormula))
		{
			return false;
		}
		AssrtQuantifiedIntVarsFormula f = (AssrtQuantifiedIntVarsFormula) o;
		return super.equals(this)  // Does canEqual
				&& this.vars.equals(f.vars) && this.expr.equals(f.expr);  
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtQuantifiedIntVarsFormula;
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

package org.scribble.ext.assrt.type.formula;

import java.util.Set;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;

public class AssrtNegFormula extends AssrtBoolFormula
{
	public final AssrtBoolFormula expr;

	protected AssrtNegFormula(AssrtBoolFormula expr)
	{
		this.expr = expr;
	}

	@Override
	public AssrtBoolFormula squash()
	{
		return AssrtFormulaFactory.AssrtNeg(this.expr.squash());
	}

	@Override
	public AssrtBoolFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		return AssrtFormulaFactory.AssrtNeg(this.expr.subs(old, neu));
	}

	@Override
	public String toSmt2Formula()
	{
		return "(not " + this.expr.toSmt2Formula() + ")";
	}

	@Override
	protected BooleanFormula toJavaSmtFormula()
	{
		BooleanFormulaManager fmanager = JavaSmtWrapper.getInstance().bfm;
		return fmanager.not(this.expr.toJavaSmtFormula());
	}

	@Override
	public Set<AssrtDataTypeVar> getIntVars()
	{
		Set<AssrtDataTypeVar> vs = this.expr.getIntVars();
		return vs;
	}
	
	@Override
	public String toString()
	{
		return "!" + this.expr;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtNegFormula))
		{
			return false;
		}
		AssrtNegFormula f = (AssrtNegFormula) o;
		return super.equals(this)  // Does canEqual
				&& this.expr.equals(f.expr);  
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtNegFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 7109;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.expr.hashCode();
		return hash;
	}
}

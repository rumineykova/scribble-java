package org.scribble.ext.assrt.type.formula;

import java.util.List;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager;

public class AssrtForallIntVarsFormula extends AssrtQuantifiedIntVarsFormula
{
	// Pre: vars non empty
	protected AssrtForallIntVarsFormula(List<AssrtIntVarFormula> vars, AssrtBoolFormula expr)
	{
		super(vars, expr);
	}

	@Override
	public AssrtBoolFormula getCnf()
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}

	@Override
	public boolean isNF(AssrtBinBoolFormula.Op op)
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}

	@Override
	public boolean hasOp(AssrtBinBoolFormula.Op op)
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}
	
	@Override
	public AssrtBoolFormula squash()
	{
		List<AssrtIntVarFormula> vars
				= this.vars.stream().filter(v -> !v.toString().startsWith("_dum")).collect(Collectors.toList());  // FIXME
		AssrtBoolFormula expr = this.expr.squash();
		return (vars.isEmpty()) ? expr : AssrtFormulaFactory.AssrtForallFormula(vars, expr);
	}

	@Override
	public AssrtForallIntVarsFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		if (this.vars.contains(old))
		{
			return this;
		}
		return AssrtFormulaFactory.AssrtForallFormula(
				//this.vars.stream().map(v -> v.subs(old, neu)).collect(Collectors.toList()), 
				this.vars,
				this.expr.subs(old, neu));
	}
	
	@Override
	public String toSmt2Formula()
	{
		String vs = this.vars.stream().map(v -> "(" + v.toSmt2Formula() + " Int)").collect(Collectors.joining(" "));
		String expr = this.expr.toSmt2Formula();
		return "(forall (" + vs + ") " + expr + ")";
	}

	@Override
	protected BooleanFormula toJavaSmtFormula()
	{
		QuantifiedFormulaManager qfm = JavaSmtWrapper.getInstance().qfm;
		BooleanFormula expr = (BooleanFormula) this.expr.toJavaSmtFormula();
		List<IntegerFormula> vs = this.vars.stream().map(v -> v.getJavaSmtFormula()).collect(Collectors.toList());
		return qfm.forall(vs, expr);
	}
	
	@Override
	public String toString()
	{
		return "(forall " + bodyToString() + ")";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtForallIntVarsFormula))
		{
			return false;
		}
		return super.equals(this);  // Does canEqual
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtForallIntVarsFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 6803;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
}

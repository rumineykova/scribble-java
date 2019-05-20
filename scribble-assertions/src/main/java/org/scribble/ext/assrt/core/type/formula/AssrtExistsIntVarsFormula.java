package org.scribble.ext.assrt.core.type.formula;

import java.util.List;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

public class AssrtExistsIntVarsFormula extends AssrtQuantifiedIntVarsFormula
{
	// Pre: vars non empty
	protected AssrtExistsIntVarsFormula(List<AssrtIntVarFormula> vars, AssrtBFormula expr)
	{
		super(vars, expr);
	}

	@Override
	public AssrtBFormula getCnf()
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}

	@Override
	public boolean isNF(AssrtBinBFormula.Op op)
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}

	@Override
	public boolean hasOp(AssrtBinBFormula.Op op)
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}
	
	@Override
	public AssrtBFormula squash()
	{
		List<AssrtIntVarFormula> vars
				= this.vars.stream().filter(v -> !v.toString().startsWith("_dum")).collect(Collectors.toList());  // FIXME
		AssrtBFormula expr = this.expr.squash();
		return (vars.isEmpty()) ? expr : AssrtFormulaFactory.AssrtExistsFormula(vars, expr);
	}

	@Override
	public AssrtExistsIntVarsFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		if (this.vars.contains(old))
		{
			return this;
		}
		return AssrtFormulaFactory.AssrtExistsFormula(
				//this.vars.stream().map(v -> v.subs(old, neu)).collect(Collectors.toList()), 
				this.vars,
				this.expr.subs(old, neu));
	}
	
	@Override
	public String toSmt2Formula()
	{
		String vs = this.vars.stream().map(v -> "(" + v.toSmt2Formula() + " Int)").collect(Collectors.joining(" "));
		String expr = this.expr.toSmt2Formula();
		return "(exists (" + vs + ") " + expr + ")";
	}

	@Override
	protected BooleanFormula toJavaSmtFormula()
	{
		BooleanFormula expr = (BooleanFormula) this.expr.toJavaSmtFormula();
		List<IntegerFormula> vs = this.vars.stream().map(v -> v.getJavaSmtFormula()).collect(Collectors.toList());

		/*JavaSmtWrapper j = JavaSmtWrapper.getInstance();  // Cf. AssrtTest.JavaSmtBug
		Object o = j.qfm.exists(vs.get(0), expr);
		if (o.toString().equals("(exists ((_dum1 Int)) false)") && this.toString().equals("(exists ((x)) (False))"))
		{
			System.out.println("ddd: " + j.qfm.exists(vs.get(0), expr) + ", " +j.ifm.makeVariable("x") + ", "
					
		+ j.qfm.exists(j.ifm.makeVariable("x"), j.bfm.makeFalse()) + ", " + j.qfm.exists(Arrays.asList(j.ifm.makeVariable("x")), j.bfm.makeFalse()) + ", "

		+ j.qfm.exists(j.ifm.makeVariable("x"), j.bfm.makeTrue()));

			throw new RuntimeException("ccc: " + vs + ", " + o);
		}*/
		
		return JavaSmtWrapper.getInstance().qfm.exists(vs, expr);
	}
	
	@Override
	public String toString()
	{
		return "(exists " + bodyToString() + ")";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtExistsIntVarsFormula))
		{
			return false;
		}
		return super.equals(this);  // Does canEqual
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtExistsIntVarsFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 6367;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
}

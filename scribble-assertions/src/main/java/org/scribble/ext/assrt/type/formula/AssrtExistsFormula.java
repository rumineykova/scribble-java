package org.scribble.ext.assrt.type.formula;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

// FIXME: not exactly boolean kind?
public class AssrtExistsFormula extends AssrtBoolFormula
{
	public final List<AssrtIntVarFormula> vars;
	public final AssrtBoolFormula expr;

	// Pre: vars non empty
	protected AssrtExistsFormula(List<AssrtIntVarFormula> vars, AssrtBoolFormula expr)
	{
		this.vars = Collections.unmodifiableList(vars);
		this.expr = expr;
	}
	
	@Override
	public AssrtBoolFormula squash()
	{
		List<AssrtIntVarFormula> vars
				= this.vars.stream().filter(v -> !v.toString().startsWith("_dum")).collect(Collectors.toList());  // FIXME
		AssrtBoolFormula expr = this.expr.squash();
		return (vars.isEmpty()) ? expr : AssrtFormulaFactory.AssrtExistsFormula(vars, expr);
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
	public Set<AssrtDataTypeVar> getVars()
	{
		Set<AssrtDataTypeVar> vs = this.expr.getVars();
		vs.removeAll(this.vars.stream().map(v -> v.toName()).collect(Collectors.toList()));
		return vs;
	}
	
	@Override
	public String toString()
	{
		return "(exists [" + this.vars.stream().map(Object::toString).collect(Collectors.joining(", ")) + "] (" + this.expr + "))";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtExistsFormula))
		{
			return false;
		}
		AssrtExistsFormula f = (AssrtExistsFormula) o;
		return super.equals(this)  // Does canEqual
				&& this.vars.equals(f.vars) && this.expr.equals(f.expr);  
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtExistsFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 6367;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.vars.hashCode();
		hash = 31 * hash + this.expr.hashCode();
		return hash;
	}
}

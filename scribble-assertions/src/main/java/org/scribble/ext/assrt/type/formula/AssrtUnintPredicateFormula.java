package org.scribble.ext.assrt.type.formula;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.type.name.AssrtSort;
import org.sosy_lab.java_smt.api.BooleanFormula;


// Make abstract, and use subclasses for specific functions? e.g., for ports
public class AssrtUnintPredicateFormula extends AssrtBoolFormula implements AssrtUnintFunFormula<BooleanFormula>
{
	public final String name;
	public final List<AssrtArithFormula> args;

	protected AssrtUnintPredicateFormula(String name, List<AssrtArithFormula> args)
	{
		this.name = name;
		this.args = Collections.unmodifiableList(args);
	}

	@Override
	public List<AssrtSort> getParamSorts()
	{
		throw new RuntimeException("[assrt] TODO: " + this);
	}

	@Override
	public AssrtSort getReturnSort()
	{
		throw new RuntimeException("[assrt] TODO: " + this);
	}

	@Override
	public AssrtBoolFormula squash()
	{
		return this;
	}

	@Override
	public AssrtBoolFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		return new AssrtUnintPredicateFormula(this.name, 
				this.args.stream().map(a -> a.subs(old, neu)).collect(Collectors.toList()));
	}

	@Override
	public String toSmt2Formula()
	{
		return "(" + this.name + " " + this.args.stream().map(a -> a.toSmt2Formula()).collect(Collectors.joining(" ")) + ")";
	}

	@Override
	protected BooleanFormula toJavaSmtFormula()
	{
		throw new RuntimeException("[assrt] TODO: " + this);
	}

	@Override
	public Set<AssrtDataTypeVar> getIntVars()
	{
		return this.args.stream().flatMap(a -> a.getIntVars().stream()).collect(Collectors.toSet());
	}

	@Override
	public String toString()
	{
		return this.name + "(" + this.args.stream().map(a -> a.toString()).collect(Collectors.joining(", ")) + ")";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtUnintPredicateFormula))
		{
			return false;
		}
		AssrtUnintPredicateFormula them = (AssrtUnintPredicateFormula) o;
		return super.equals(this)  // Does canEqual
				&& this.name.equals(them.name) && this.args.equals(them.args);  
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtUnintPredicateFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 7001;
		hash = 31 * hash + this.name.hashCode();
		hash = 31 * hash + this.args.hashCode();
		return hash;
	}
}

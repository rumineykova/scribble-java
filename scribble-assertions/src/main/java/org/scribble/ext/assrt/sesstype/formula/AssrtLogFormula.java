package org.scribble.ext.assrt.sesstype.formula;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;

public class AssrtLogFormula extends AssrtBoolFormula
{
	public final Set<AssrtDataTypeVar> vars; 
	
	// Takes vars separately, because vars is done by AssrtBoolFormula.getVars (not BooleanFormula)
	public AssrtLogFormula(BooleanFormula f1, Set<AssrtDataTypeVar> vars)
	{
		this.formula = f1;  
		this.vars = Collections.unmodifiableSet(vars); 	
	}
	
	@Override
	protected BooleanFormula toJavaSmtFormula() //throws AssertionParseException
	{
		return (BooleanFormula) this.formula; 
	}
	
	@Override
	public Set<AssrtDataTypeVar> getVars()
	{
		return new HashSet<>(this.vars); 
	}
	
	//public AssrtLogFormula addFormula(AssrtSmtFormula newFormula) throws AssertionParseException
	public AssrtLogFormula addFormula(AssrtBoolFormula newFormula) //throws AssertionParseException
	{		
		return this.formula == null
				? new AssrtLogFormula(newFormula.formula, newFormula.getVars())
				:	JavaSmtWrapper.getInstance().addFormula(this, newFormula);
	}
	
	@Override
	public String toString()
	{
		return super.toString();  // FIXME: prints this.formula which is a Java-SMT formula, not AssrtBoolFormula like others
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtLogFormula))
		{
			return false;
		}
		AssrtLogFormula f = (AssrtLogFormula) o;
		return super.equals(this)  // Does canEqual
				&& this.formula.equals(f.formula) && this.vars.equals(f.vars); 
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtLogFormula;
	}

	@Override
	public int hashCode()  // This is the only Formula class to use this.formula
	{
		int hash = 5923;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.formula.toString().hashCode();  // FIXME HACK
		hash = 31 * hash + this.vars.hashCode();
		return hash;
	}
}

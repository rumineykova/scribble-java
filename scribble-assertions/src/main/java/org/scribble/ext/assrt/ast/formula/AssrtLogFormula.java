package org.scribble.ext.assrt.ast.formula;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;

public class AssrtLogFormula extends AssrtBoolFormula
{
	Set<AssrtDataTypeVar> vars; 
	
	public AssrtLogFormula(BooleanFormula f1, Set<AssrtDataTypeVar> vars)
	{
		this.vars = Collections.unmodifiableSet(vars); 	
		this.formula = f1;  
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
}

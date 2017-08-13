package org.scribble.ext.assrt.type.formula;

import java.util.Collections;
import java.util.Set;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;

public class AssrtFalseFormula extends AssrtBoolFormula
{
	public static final AssrtFalseFormula FALSE = new AssrtFalseFormula();
	
	private AssrtFalseFormula()
	{
		
	}
	
	@Override
	public AssrtBoolFormula squash()
	{
		return this;
	}

	@Override
	public AssrtFalseFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		return this;
	}
	
	@Override
	public String toSmt2Formula()
	{
		return "false";
	}
	
	@Override
	protected BooleanFormula toJavaSmtFormula() //throws AssertionParseException {
	{
		return JavaSmtWrapper.getInstance().bfm.makeFalse();
	}
	
	@Override
	public Set<AssrtDataTypeVar> getIntVars()
	{
		return Collections.emptySet(); 
	}

	@Override
	public String toString()
	{
		return "False"; 
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtFalseFormula))
		{
			return false;
		}
		return super.equals(this);  // Does canEqual
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtFalseFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 5881;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
}

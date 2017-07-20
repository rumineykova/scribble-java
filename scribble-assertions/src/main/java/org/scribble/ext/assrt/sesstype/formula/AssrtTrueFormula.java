package org.scribble.ext.assrt.sesstype.formula;

import java.util.Collections;
import java.util.Set;

import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;

// FIXME: declare singleton constant
public class AssrtTrueFormula extends AssrtBoolFormula
{
	public static final AssrtTrueFormula TRUE = new AssrtTrueFormula();
	
	private AssrtTrueFormula()
	{
		
	}
	
	@Override
	public String toString()
	{
		return "True"; 
	}
	
	@Override
	protected BooleanFormula toJavaSmtFormula() //throws AssertionParseException {
	{
		return JavaSmtWrapper.getInstance().bfm.makeTrue();
	}
	
	@Override
	public Set<AssrtDataTypeVar> getVars()
	{
		return Collections.emptySet(); 
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtTrueFormula))
		{
			return false;
		}
		return super.equals(this);  // Does canEqual
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtTrueFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 5881;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
}

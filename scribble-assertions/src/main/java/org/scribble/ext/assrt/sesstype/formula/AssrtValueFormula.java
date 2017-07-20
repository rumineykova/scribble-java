package org.scribble.ext.assrt.sesstype.formula;

import java.util.Collections;
import java.util.Set;

import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

// Integer literal
public class AssrtValueFormula extends AssrtArithFormula
{
	public final int value; 

	public AssrtValueFormula(String value)
	{
		this.value = Integer.parseInt(value); 
	}
	
	@Override
	public String toString()
	{
		return Integer.toString(this.value); 
	}
	
	@Override
	public IntegerFormula toJavaSmtFormula()
	{
		IntegerFormulaManager fmanager = JavaSmtWrapper.getInstance().ifm;
		return fmanager.makeNumber(this.value);  
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
		if (!(o instanceof AssrtValueFormula))
		{
			return false;
		}
		return super.equals(this)  // Does canEqual
				&& this.value == ((AssrtValueFormula) o).value;
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtValueFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 5897;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.value;
		return hash;
	}
}

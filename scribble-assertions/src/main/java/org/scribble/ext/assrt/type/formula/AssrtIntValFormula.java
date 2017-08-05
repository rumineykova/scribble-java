package org.scribble.ext.assrt.type.formula;

import java.util.Collections;
import java.util.Set;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

// Integer literal
public class AssrtIntValFormula extends AssrtArithFormula
{
	public final int val; 

	protected AssrtIntValFormula(int i)
	{
		this.val = i; 
	}

	@Override
	public AssrtIntValFormula squash()
	{
		return AssrtFormulaFactory.AssrtIntVal(this.val);
	}

	@Override
	public AssrtArithFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		return this;
	}
		
	@Override
	public String toSmt2Formula()
	{
		//return "(" + Integer.toString(this.val) + ")";
		return Integer.toString(this.val);
	}
	
	@Override
	public IntegerFormula toJavaSmtFormula()
	{
		IntegerFormulaManager fmanager = JavaSmtWrapper.getInstance().ifm;
		return fmanager.makeNumber(this.val);  
	}
	
	@Override
	public Set<AssrtDataTypeVar> getVars()
	{
		return Collections.emptySet();	
	}
	
	@Override
	public String toString()
	{
		return Integer.toString(this.val); 
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtIntValFormula))
		{
			return false;
		}
		return super.equals(this)  // Does canEqual
				&& this.val == ((AssrtIntValFormula) o).val;
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtIntValFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 5897;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.val;
		return hash;
	}
}

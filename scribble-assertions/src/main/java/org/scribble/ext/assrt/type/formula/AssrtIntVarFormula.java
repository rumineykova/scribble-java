package org.scribble.ext.assrt.type.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

// Variable occurrence
public class AssrtIntVarFormula extends AssrtArithFormula
{
	public final String name; 

	public AssrtIntVarFormula(String name)
	{
		this.name = name; 
	}
	
	// i.e., to "type"
	public AssrtDataTypeVar toName()
	{
		return new AssrtDataTypeVar(this.name);
	}
	
	@Override
	public String toString()
	{
		return this.name; 
	}
	
	@Override
	public IntegerFormula toJavaSmtFormula()
	{
		IntegerFormulaManager fmanager = JavaSmtWrapper.getInstance().ifm;
		return fmanager.makeVariable(this.name);   
	}
	
	@Override
	public Set<AssrtDataTypeVar> getVars()
	{
		Set<AssrtDataTypeVar> vars = new HashSet<>();
		vars.add(new AssrtDataTypeVar(this.name)); 
		return vars; 
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtIntVarFormula))
		{
			return false;
		}
		return super.equals(this)  // Does canEqual
				&& this.name.equals(((AssrtIntVarFormula) o).name);
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtIntVarFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 5903;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.name.hashCode();
		return hash;
	}
}

package org.scribble.ext.assrt.core.type.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.core.type.name.AssrtIntVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

// Variable occurrence
// FIXME: currently also used for roles -- probably need to parse as "ambig" and disamb later
public class AssrtIntVarFormula extends AssrtAFormula
{
	public final String name; 

	protected AssrtIntVarFormula(String name)
	{
		this.name = name; 
	}
	
	// i.e., to "type"
	public AssrtIntVar toName()
	{
		return new AssrtIntVar(this.name);
	}

	@Override
	public AssrtIntVarFormula squash()
	{
		return AssrtFormulaFactory.AssrtIntVar(this.name);
	}

	@Override
	public AssrtIntVarFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		return this.equals(old) ? neu : this;
	}
		
	@Override
	public String toSmt2Formula()
	{
		/*if (this.name.startsWith("_dum"))  // FIXME
		{
			throw new RuntimeException("[assrt] Use squash first: " + this);
		}*/
		//return "(" + this.name + ")";
		return this.name;
	}
	
	@Override
	public IntegerFormula toJavaSmtFormula()
	{
		IntegerFormulaManager fmanager = JavaSmtWrapper.getInstance().ifm;
		return fmanager.makeVariable(this.name);   
	}
	
	@Override
	public Set<AssrtIntVar> getIntVars()
	{
		Set<AssrtIntVar> vars = new HashSet<>();
		vars.add(new AssrtIntVar(this.name));  // FIXME: currently may also be a role
		return vars; 
	}
	
	@Override
	public String toString()
	{
		return this.name; 
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

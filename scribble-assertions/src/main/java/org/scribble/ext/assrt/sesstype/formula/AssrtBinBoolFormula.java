package org.scribble.ext.assrt.sesstype.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;

// Binary boolean
// Top-level formula of assertions
public class AssrtBinBoolFormula extends AssrtBoolFormula
{
	public enum Op
	{
		And, 
		Or;

		@Override
		public String toString()
		{
			switch (this)
			{
				case And: return "&&";
				case Or: return "||";
				default: throw new RuntimeException("Won't get in here: " + this);
			}
		}
	}
		
	public final Op op; 
	public final AssrtBoolFormula left; 
	public final AssrtBoolFormula right; 
	//BooleanFormula formula;   // FIXME
	
	public AssrtBinBoolFormula(Op op, AssrtBoolFormula left, AssrtBoolFormula right)
	{
		this.left = left; 
		this.right = right; 
		this.op = op;
		/*switch (op) {
		case "&&": 
			this.op = AssrtBinBoolOp.And; 
			break; 
		case "||":
			this.op = AssrtBinBoolOp.Or;
			break;
		default:
			throw new RuntimeException("[assrt] Shouldn't get in here: " + op);
		}*/
	}
	
	@Override
	public String toString()
	{
		return "(" + this.left.toString() + ' '  + this.op + ' ' + this.right.toString() + ")";  
	}
	
	@Override
	protected BooleanFormula toJavaSmtFormula() //throws AssertionParseException
	{
		BooleanFormulaManager fmanager = JavaSmtWrapper.getInstance().bfm;
		BooleanFormula bleft = (BooleanFormula) this.left.toJavaSmtFormula();
		BooleanFormula bright = (BooleanFormula) this.right.toJavaSmtFormula();
		
		switch(this.op) {
		case And: 
			return fmanager.and(bleft,bright); 
		case Or:
			return fmanager.or(bleft,bright); 
		default:
			//throw new AssertionParseException("No matchin ooperation for boolean formula"); 
			throw new RuntimeException("[assrt] Shouldn't get in here: " + op);
		}		
	}
	
	@Override
	public Set<AssrtDataTypeVar> getVars()
	{
		Set<AssrtDataTypeVar> vars = new HashSet<>(this.left.getVars()); 
		vars.addAll(this.right.getVars()); 
		return vars; 
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtBinBoolFormula))
		{
			return false;
		}
		AssrtBinBoolFormula f = (AssrtBinBoolFormula) o;
		return super.equals(this)  // Does canEqual
				&& this.op.equals(f.op) && this.left.equals(f.left) && this.right.equals(f.right);  
						// Storing left/right as a Set could give commutativity in equals, but not associativity
						// Better to keep "syntactic" equality, and do via additional routines for, e.g., normal forms
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtBinBoolFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 5881;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.op.hashCode();
		hash = 31 * hash + this.left.hashCode();
		hash = 31 * hash + this.right.hashCode();
		return hash;
	}
}

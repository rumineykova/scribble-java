package org.scribble.ext.assrt.type.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

// Binary comparison
public class AssrtBinCompFormula extends AssrtBoolFormula
{
	public enum Op
	{
		GreaterThan, 
		LessThan, 
		Eq;
		
		@Override
		public String toString()
		{
			switch (this)
			{
				case GreaterThan: return ">";
				case LessThan: return "<";
				case Eq: return "=";
				default: throw new RuntimeException("Won't get in here: " + this);
			}
		}
	}

	public final Op op; 
	public final AssrtArithFormula left; 
	public final AssrtArithFormula right; 
	
	protected AssrtBinCompFormula(Op op, AssrtArithFormula left, AssrtArithFormula right)
	{
		this.left = left; 
		this.right = right; 
		this.op = op;
		/*switch (op) {
		case ">": 
			this.op = Op.GreaterThan;
			break; 
		case "<":
			this.op = Op.LessThan;
			break;
		case "=":
			this.op = Op.Eq;
			break;
		default: throw new RuntimeException("[assrt] Shouldn't get in here: " + op);
		}*/
	}
	
	@Override
	public String toString()
	{
		return "(" + this.left.toString() + ' '  + this.op + ' ' + this.right.toString() + ")"; 
	}
	
	@Override
	public BooleanFormula toJavaSmtFormula() //throws AssertionParseException
	{
		IntegerFormulaManager fmanager = JavaSmtWrapper.getInstance().ifm;
		IntegerFormula fleft = (IntegerFormula) this.left.toJavaSmtFormula();
		IntegerFormula fright = (IntegerFormula) this.right.toJavaSmtFormula();
		switch(this.op)
		{
			case GreaterThan: 
				return fmanager.greaterThan(fleft, fright); 
			case LessThan:
				return fmanager.lessThan(fleft, fright);
			case Eq:
				return fmanager.equal(fleft, fright);  
			default:
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
		if (!(o instanceof AssrtBinCompFormula))
		{
			return false;
		}
		AssrtBinCompFormula f = (AssrtBinCompFormula) o;
		return super.equals(this)  // Does canEqual
				&& this.op.equals(f.op) && this.left.equals(f.left) && this.right.equals(f.right);  
						// Storing left/right as a Set could give commutativity in equals, but not associativity
						// Better to keep "syntactic" equality, and do via additional routines for, e.g., normal forms
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtBinCompFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 5897;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.op.hashCode();
		hash = 31 * hash + this.left.hashCode();
		hash = 31 * hash + this.right.hashCode();
		return hash;
	}
}

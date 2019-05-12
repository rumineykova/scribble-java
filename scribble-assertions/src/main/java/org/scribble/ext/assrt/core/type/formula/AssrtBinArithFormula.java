package org.scribble.ext.assrt.core.type.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.core.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;


// Binary arithmetic
public class AssrtBinArithFormula extends AssrtArithFormula implements AssrtBinFormula<IntegerFormula>
{
	public enum Op
	{
		Add,
		Subt,
		Mult;
		
		@Override
		public String toString()
		{
			switch (this)
			{
				case Add: return "+";
				case Mult: return "*";
				case Subt: return "-";
				default: throw new RuntimeException("Won't get in here: " + this);
			}
		}
	}

	public final Op op;
	/*public final AssrtSmtFormula left;
	public final AssrtSmtFormula right;*/
	public final AssrtArithFormula left;
	public final AssrtArithFormula right;

	protected AssrtBinArithFormula(Op op, AssrtArithFormula left, AssrtArithFormula right)
	{
		this.left = left;
		this.right = right;
		this.op = op;
	}
	
	@Override
	public AssrtBinArithFormula squash()
	{
		return AssrtFormulaFactory.AssrtBinArith(this.op, this.left.squash(), this.right.squash());
	}

	@Override
	public AssrtBinArithFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		return AssrtFormulaFactory.AssrtBinArith(this.op, this.left.subs(old, neu), this.right.subs(old, neu));
	}
	
	@Override
	public String toSmt2Formula()
	{
		String left = this.left.toSmt2Formula();
		String right = this.right.toSmt2Formula();
		String op;
		switch(this.op)
		{
			case Add:  op = "+"; break;
			case Subt: op = "-"; break;
			case Mult: op = "*"; break;
			default:   throw new RuntimeException("[assrt] Shouldn't get in here: " + this.op);
		}
		return "(" + op + " " + left + " " + right + ")";
	}

	@Override
	public IntegerFormula toJavaSmtFormula() //throws AssertionParseException
	{
		IntegerFormulaManager fmanager = JavaSmtWrapper.getInstance().ifm;
		IntegerFormula fleft = (IntegerFormula) this.left.toJavaSmtFormula();
		IntegerFormula fright = (IntegerFormula) this.right.toJavaSmtFormula();
		switch(this.op)
		{
			case Add:
				return fmanager.add(fleft, fright);
			case Subt:
				return fmanager.subtract(fleft, fright);
			case Mult:
				return fmanager.multiply(fleft, fright);
			default:
				//throw new AssertionParseException("No matchin ooperation for boolean formula");
				throw new RuntimeException("[assrt] Shouldn't get in here: " + op);
		}
	}

	@Override
	public Set<AssrtDataTypeVar> getIntVars()
	{
		Set<AssrtDataTypeVar> vars = new HashSet<>(this.left.getIntVars());
		vars.addAll(this.right.getIntVars());
		return vars;
	}

	@Override
	public AssrtArithFormula getLeft()
	{
		return this.left;
	}

	@Override
	public AssrtArithFormula getRight()
	{
		return this.right;
	}

	@Override
	public String toString()
	{
		return "(" + this.left.toString() + ' '  + this.op + ' ' + this.right.toString() + ")";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtBinArithFormula))
		{
			return false;
		}
		AssrtBinArithFormula f = (AssrtBinArithFormula) o;
		return super.equals(this)  // Does canEqual
				&& this.op.equals(f.op) && this.left.equals(f.left) && this.right.equals(f.right);  
						// Storing left/right as a Set could give commutativity in equals, but not associativity
						// Better to keep "syntactic" equality, and do via additional routines for, e.g., normal forms
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtBinArithFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 5879;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.op.hashCode();
		hash = 31 * hash + this.left.hashCode();
		hash = 31 * hash + this.right.hashCode();
		return hash;
	}
}

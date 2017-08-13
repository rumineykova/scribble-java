package org.scribble.ext.assrt.type.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;

// Binary boolean
// Top-level formula of assertions
public class AssrtBinBoolFormula extends AssrtBoolFormula implements AssrtBinaryFormula<BooleanFormula>
{
	public enum Op
	{
		And, 
		Or,
		Imply;  // Not currently parsed, only created

		@Override
		public String toString()
		{
			switch (this)
			{
				case And: return "&&";
				case Or: return "||";
				case Imply: return "=>";
				default: throw new RuntimeException("Won't get in here: " + this);
			}
		}
	}
		
	public final Op op; 
	public final AssrtBoolFormula left; 
	public final AssrtBoolFormula right; 
	//BooleanFormula formula;   // FIXME
	
	protected AssrtBinBoolFormula(Op op, AssrtBoolFormula left, AssrtBoolFormula right)
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
	public AssrtBoolFormula squash()
	{
		AssrtBoolFormula left = this.left.squash();
		AssrtBoolFormula right = this.right.squash();
		switch (this.op)
		{
			case And:
			{
				if (left.equals(AssrtFalseFormula.FALSE) || right.equals(AssrtFalseFormula.FALSE))
				{
					return AssrtFalseFormula.FALSE;
				}
				if (left.equals(AssrtTrueFormula.TRUE))
				{
					return right;
				}
				if (right.equals(AssrtTrueFormula.TRUE))
				{
					return left;
				}
				return AssrtFormulaFactory.AssrtBinBool(this.op, left, right);
			}
			case Imply:
			{
				if (left.equals(AssrtFalseFormula.FALSE))
				{	
					return AssrtTrueFormula.TRUE;
				}
				/*if (right.equals(AssrtFalseFormula.FALSE))
				{
					return ..neg.. left;
				}*/
				if (right.equals(AssrtTrueFormula.TRUE))
				{
					return right;
				}
				return AssrtFormulaFactory.AssrtBinBool(this.op, left, right);
			}
			case Or:
			{
				if (left.equals(AssrtTrueFormula.TRUE) || right.equals(AssrtTrueFormula.TRUE))
				{
					return AssrtTrueFormula.TRUE;
				}
				if (left.equals(AssrtFalseFormula.FALSE))
				{
					return right;
				}
				if (right.equals(AssrtFalseFormula.FALSE))
				{
					return left;
				}
				return AssrtFormulaFactory.AssrtBinBool(this.op, left, right);
			}
			default: throw new RuntimeException("[assrt] Shouldn't get in here: " + op);
		}
	}

	@Override
	public AssrtBinBoolFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		return AssrtFormulaFactory.AssrtBinBool(this.op, this.left.subs(old, neu), this.right.subs(old, neu));
	}
	
	@Override
	public String toSmt2Formula()
	{
		String left = this.left.toSmt2Formula();
		String right = this.right.toSmt2Formula();
		String op;
		switch(this.op)
		{
			case And:   op = "and"; break;
			case Or:    op = "or"; break;
			case Imply: op = "=>"; break;
			default:   throw new RuntimeException("[assrt] Shouldn't get in here: " + this.op);
		}
		return "(" + op + " " + left + " " + right + ")";
	}
	
	@Override
	protected BooleanFormula toJavaSmtFormula() //throws AssertionParseException
	{
		BooleanFormulaManager fmanager = JavaSmtWrapper.getInstance().bfm;
		BooleanFormula bleft = (BooleanFormula) this.left.toJavaSmtFormula();
		BooleanFormula bright = (BooleanFormula) this.right.toJavaSmtFormula();
		switch(this.op)
		{
			case And:   return fmanager.and(bleft, bright); 
			case Or:    return fmanager.or(bleft, bright); 
			case Imply: return fmanager.implication(bleft, bright); 
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
	public AssrtBoolFormula getLeft()
	{
		return this.left;
	}

	@Override
	public AssrtBoolFormula getRight()
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

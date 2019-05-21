package org.scribble.ext.assrt.core.type.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.core.type.name.AssrtDataVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

// Binary comparison
public class AssrtBinCompFormula extends AssrtBFormula implements AssrtBinFormula<IntegerFormula>
{
	public enum Op
	{
		Eq,
		LessThan, 
		LessThanEq, 
		GreaterThan, 
		GreaterThanEq;
		
		@Override
		public String toString()
		{
			switch (this)
			{
				case Eq: return "=";
				case LessThan: return "<";
				case LessThanEq: return "<=";
				case GreaterThan: return ">";
				case GreaterThanEq: return ">=";
				default: throw new RuntimeException("Won't get in here: " + this);
			}
		}
	}

	public final Op op; 
	public final AssrtAFormula left; 
	public final AssrtAFormula right; 
	
	protected AssrtBinCompFormula(Op op, AssrtAFormula left, AssrtAFormula right)
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
	public AssrtBFormula getCnf()
	{
		return this;
	}

	@Override
	public boolean isNF(AssrtBinBFormula.Op op)
	{
		return true;
	}

	@Override
	public boolean hasOp(AssrtBinBFormula.Op op)
	{
		return false;
	}

	@Override
	//public AssrtBinCompFormula squash()
	public AssrtBFormula squash()  // For True
	{
		if (this.op.equals(AssrtBinCompFormula.Op.Eq)
				&& (this.left instanceof AssrtIntVarFormula) && this.left.toString().startsWith("_dum"))  // FIXME
		{
			return AssrtTrueFormula.TRUE;
		}
		//return this;  // No: underlying this.formula will not be the same after squashing
		return AssrtFormulaFactory.AssrtBinComp(this.op, this.left.squash(), this.right.squash());
	}

	@Override
	public AssrtBinCompFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		return AssrtFormulaFactory.AssrtBinComp(this.op, this.left.subs(old, neu), this.right.subs(old, neu));
	}
	
	@Override
	public String toSmt2Formula()
	{
		String left = this.left.toSmt2Formula();
		String right = this.right.toSmt2Formula();
		String op;
		switch(this.op)
		{
			case Eq:            op = "=";  break;
			case LessThan:      op = "<";  break;
			case LessThanEq:    op = "<="; break;
			case GreaterThan:   op = ">";  break;
			case GreaterThanEq: op = ">="; break;
			default: throw new RuntimeException("[assrt] Shouldn't get in here: " + this.op);
		}
		return "(" + op + " " + left + " " + right + ")";
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
	public Set<AssrtDataVar> getIntVars()
	{
		Set<AssrtDataVar> vars = new HashSet<>(this.left.getIntVars()); 
		vars.addAll(this.right.getIntVars()); 
		return vars; 
	}

	@Override
	public AssrtAFormula getLeft()
	{
		return this.left;
	}

	@Override
	public AssrtAFormula getRight()
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

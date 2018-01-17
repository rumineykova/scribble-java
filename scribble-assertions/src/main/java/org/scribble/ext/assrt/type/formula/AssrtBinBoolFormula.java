package org.scribble.ext.assrt.type.formula;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;

// Binary boolean
// Top-level formula of assertions
public class AssrtBinBoolFormula extends AssrtBoolFormula implements AssrtBinFormula<BooleanFormula>
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
	public AssrtBoolFormula getCnf()
	{
		switch (this.op)
		{
			case And:
			{
				AssrtBoolFormula l = this.left.getCnf();
				AssrtBoolFormula r = this.right.getCnf();
				return AssrtFormulaFactory.AssrtBinBool(Op.And, l, r);
			}
			case Imply:
			{
				throw new RuntimeException("[assrt-core] TODO: " + this);
			}
			case Or:
			{
				if (this.left.hasOp(Op.And))
				{
					List<AssrtBoolFormula> fs = getCnfClauses(this.left.getCnf());
					AssrtBinBoolFormula res = fs.stream().map(f -> AssrtFormulaFactory.AssrtBinBool(Op.Or, f, this.right))
							.reduce((f1, f2) -> AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, f1, f2)).get();
					return res.getCnf();
				}
				else if (this.right.hasOp(Op.And))  // FIXME: factor out with above
				{
					List<AssrtBoolFormula> fs = getCnfClauses(this.right.getCnf());
					AssrtBinBoolFormula res = fs.stream().map(f -> AssrtFormulaFactory.AssrtBinBool(Op.Or, this.left, f))
							.reduce((f1, f2) -> AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, f1, f2)).get();
					return res.getCnf();
				}
				else
				{
					return this;
				}
			}
			default:
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
			}
		}
	}

	private List<AssrtBoolFormula> getCnfClauses(AssrtBoolFormula f)
	{
		List<AssrtBoolFormula> fs = new LinkedList<>();
		while (f instanceof AssrtBinBoolFormula)
		{
			AssrtBinBoolFormula c = (AssrtBinBoolFormula) f;
			if (c.op != Op.And)
			{
				break;
			}
			fs.add(c.left);
			f = c.right;
		}
		fs.add(f);
		return fs;
	}
	
	//public boolean isDisjunction()
	public boolean isNF(AssrtBinBoolFormula.Op top)
	{
		if (this.op == top)
		{
			return this.left.isNF(top) && this.right.isNF(top);
		}
		else
		{
			return !this.left.hasOp(top) && !this.right.hasOp(top);
		}
	}

	@Override
	public boolean hasOp(AssrtBinBoolFormula.Op op)
	{
		return (this.op == op) || this.left.hasOp(op) || this.right.hasOp(op);
	}

	/*@Override
	public AssrtBoolFormula getDisjunction()
	{
		switch (this.op)
		{
			case And:
			{
				AssrtBoolFormula l = this.left.getCnf();
				AssrtBoolFormula r = this.right.getCnf();
				return AssrtFormulaFactory.AssrtBinBool(Op.And, l, r);
			}
			case Imply:
			{
				throw new RuntimeException("[assrt-core] TODO: " + this);
			}
			case Or:
			{
				AssrtBoolFormula l = this.left.getCnf();
				AssrtBoolFormula r = this.right.getCnf();
				return AssrtFormulaFactory.AssrtBinBool(Op.And, l, r);
			}
			default:
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
			}
		}
	}*/
	
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

package org.scribble.ext.assrt.ast.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

// Binary comparison
public class AssrtBinCompFormula extends AssrtBoolFormula
{
	enum CompOp
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
	

	public final CompOp op; 
	public final AssrtArithFormula left; 
	public final AssrtArithFormula right; 
	
	public AssrtBinCompFormula(String op, AssrtArithFormula left, AssrtArithFormula right)
	{
		this.left = left; 
		this.right = right; 
		switch (op) {
		case ">": 
			this.op = CompOp.GreaterThan;
			break; 
		case "<":
			this.op = CompOp.LessThan;
			break;
		case "=":
			this.op = CompOp.Eq;
			break;
		default: throw new RuntimeException("[assrt] Shouldn't get in here: " + op);
		}
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
		
		switch(this.op) {
		case GreaterThan: 
			return fmanager.greaterThan(fleft,fright); 
		case LessThan:
			return fmanager.lessThan(fleft,fright);
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
}

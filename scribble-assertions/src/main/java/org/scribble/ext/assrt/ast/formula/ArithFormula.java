package org.scribble.ext.assrt.ast.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;


// Binary arithmetic
public class ArithFormula extends SmtFormula
{
	enum ArithOp
	{
		Add,
		Substract,
		Mult;
		
		@Override
		public String toString()
		{
			switch (this)
			{
				case Add: return "+";
				case Mult: return "*";
				case Substract: return "-";
				default: throw new RuntimeException("Won't get in here: " + this);
			}
		}
	}

	ArithOp op;
	SmtFormula left;
	SmtFormula right;

	public ArithFormula(String op, SmtFormula left, SmtFormula right)
	{
		this.left = left;
		this.right = right;
		switch (op) {
		case "+":
			this.op = ArithOp.Add;
			break;
		case "-":
			this.op = ArithOp.Substract;
			break;
		case "*":
			this.op = ArithOp.Mult;
			break;
		}
	}

	@Override
	public String toString() {
		return "(" + this.left.toString() + ' '  + this.op + ' ' + this.right.toString() + ")";
	}

	@Override
	public IntegerFormula toFormula() throws AssertionParseException {
		IntegerFormulaManager fmanager = JavaSmtWrapper.getInstance().imanager;
		IntegerFormula fleft = (IntegerFormula) this.left.toFormula();
		IntegerFormula fright = (IntegerFormula) this.right.toFormula();

		switch(this.op) {
		case Add:
			return fmanager.add(fleft, fright);
		case Substract:
			return fmanager.subtract(fleft,fright);
		case Mult:
			return fmanager.multiply(fleft, fright);
		default:
			throw new AssertionParseException("No matchin ooperation for boolean formula");
		}
	}

	@Override
	public Set<String> getVars(){
		Set<String> vars = new HashSet<String>(this.left.getVars());
		vars.addAll(this.right.getVars());
		return vars;
	}
}

package org.scribble.ext.assrt.ast.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.util.SMTWrapper;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

public class ArithFormula extends SmtFormula {

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
		return this.left.toString() + ' '  + this.op + ' ' + this.right.toString();
	}

	@Override
	public IntegerFormula toFormula() throws AssertionException {
		IntegerFormulaManager fmanager = SMTWrapper.getInstance().imanager;
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
			throw new AssertionException("No matchin ooperation for boolean formula");
		}
	}

	@Override
	public Set<String> getVars(){
		Set<String> vars = new HashSet<String>(this.left.getVars());
		vars.addAll(this.right.getVars());
		return vars;
	}

	enum ArithOp{
		Add,
		Substract,
		Mult
	}

}

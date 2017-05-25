package org.scribble.assertions;

import java.util.HashSet;
import java.util.Set;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

public class CompFormula extends StmFormula {

	CompOp op; 
	StmFormula left; 
	StmFormula right; 
	
	public CompFormula(String op, StmFormula left, StmFormula right)
	{
		this.left = left; 
		this.right = right; 
		switch (op) {
		case ">": 
			this.op = CompOp.BiggerThan;
			break; 
		case "<":
			this.op = CompOp.LessThan;
			break;
		case "=":
			this.op = CompOp.Eq;
			break;
		}
	}
	
	@Override
	public String toString() {
		return this.left.toString() + ' '  + this.op + ' ' + this.right.toString(); 
	}
	
	@Override
	public BooleanFormula toFormula() throws AssertionException {
		IntegerFormulaManager fmanager = SMTWrapper.getInstance().imanager;
		IntegerFormula fleft = (IntegerFormula) this.left.toFormula();
		IntegerFormula fright = (IntegerFormula) this.right.toFormula();
		
		switch(this.op) {
		case BiggerThan: 
			return fmanager.greaterThan(fleft,fright); 
		case LessThan:
			return fmanager.lessThan(fleft,fright);
		case Eq:
			return fmanager.equal(fleft, fright);  
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
	
	enum CompOp{
		BiggerThan, 
		LessThan, 
		Eq
	}
	
}

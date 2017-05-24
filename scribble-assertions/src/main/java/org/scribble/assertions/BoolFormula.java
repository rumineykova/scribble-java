package org.scribble.assertions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;

public class BoolFormula extends StmFormula {

	BoolOp op; 
	StmFormula left; 
	StmFormula right; 
	BooleanFormula formula; 
	
	public BoolFormula(String op, StmFormula left, StmFormula right)
	{
		this.left = left; 
		this.right = right; 
		switch (op) {
		case "&&": 
			this.op = BoolOp.And; 
			break; 
		case "||":
			this.op = BoolOp.Or;
			break;
		}
		
		
	}
	
	@Override
	public String toString() {
		return this.left.toString() + ' '  + this.op + ' ' + this.right.toString(); 
	}
	
	@Override
	protected BooleanFormula toFormula() throws AssertionException {
		BooleanFormulaManager fmanager = FormulaUtil.getInstance().bmanager;
		BooleanFormula bleft = (BooleanFormula) this.left.toFormula();
		BooleanFormula bright = (BooleanFormula) this.right.toFormula();
		
		switch(this.op) {
		case And: 
			return fmanager.and(bleft,bright); 
		case Or:
			return fmanager.or(bleft,bright); 
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
	
	enum BoolOp{
		And, 
		Or
	}
}

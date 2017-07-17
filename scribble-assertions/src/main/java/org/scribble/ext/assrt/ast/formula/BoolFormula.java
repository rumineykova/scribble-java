package org.scribble.ext.assrt.ast.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.util.SMTWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;

// Binary boolean
public class BoolFormula extends SmtFormula
{
	enum BoolOp
	{
		And, 
		Or;
		
		@Override
		public String toString()
		{
			switch (this)
			{
				case And: return "&&";
				case Or: return "||";
				default: throw new RuntimeException("Won't get in here: " + this);
			}
		}
	}

	BoolOp op; 
	SmtFormula left; 
	SmtFormula right; 
	BooleanFormula formula; 
	
	public BoolFormula(String op, SmtFormula left, SmtFormula right)
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
		return "(" + this.left.toString() + ' '  + this.op + ' ' + this.right.toString() + ")";  
	}
	
	@Override
	protected BooleanFormula toFormula() throws AssertionParseException {
		BooleanFormulaManager fmanager = SMTWrapper.getInstance().bmanager;
		BooleanFormula bleft = (BooleanFormula) this.left.toFormula();
		BooleanFormula bright = (BooleanFormula) this.right.toFormula();
		
		switch(this.op) {
		case And: 
			return fmanager.and(bleft,bright); 
		case Or:
			return fmanager.or(bleft,bright); 
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

package org.scribble.ext.assrt.ast.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;

// Binary boolean
// Top-level formula of assertions
public class AssrtBinBoolFormula extends AssrtBoolFormula
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

	public final BoolOp op; 
	public final AssrtBoolFormula left; 
	public final AssrtBoolFormula right; 
	//BooleanFormula formula;   // FIXME
	
	public AssrtBinBoolFormula(String op, AssrtBoolFormula left, AssrtBoolFormula right)
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
		default:
			throw new RuntimeException("[assrt] Shouldn't get in here: " + op);
		}
	}
	
	@Override
	public String toString()
	{
		return "(" + this.left.toString() + ' '  + this.op + ' ' + this.right.toString() + ")";  
	}
	
	@Override
	protected BooleanFormula toJavaSmtFormula() throws AssertionParseException
	{
		BooleanFormulaManager fmanager = JavaSmtWrapper.getInstance().bmanager;
		BooleanFormula bleft = (BooleanFormula) this.left.toJavaSmtFormula();
		BooleanFormula bright = (BooleanFormula) this.right.toJavaSmtFormula();
		
		switch(this.op) {
		case And: 
			return fmanager.and(bleft,bright); 
		case Or:
			return fmanager.or(bleft,bright); 
		default:
			//throw new AssertionParseException("No matchin ooperation for boolean formula"); 
			throw new RuntimeException("[assrt] Shouldn't get in here: " + op);
		}		
	}
	
	@Override
	public Set<String> getVars()
	{
		Set<String> vars = new HashSet<String>(this.left.getVars()); 
		vars.addAll(this.right.getVars()); 
		return vars; 
	}
}

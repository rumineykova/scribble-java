package org.scribble.ext.assrt.ast.formula;

import java.util.Collections;
import java.util.Set;

import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;

public class AssertionLogFormula extends SmtFormula {
	
	Set<String> vars; 
	
	public AssertionLogFormula(Formula f1, Set<String> vars)
	{
		this.vars = Collections.unmodifiableSet(vars); 	
		this.formula = f1;  
	}
	
	@Override
	protected BooleanFormula toFormula() throws AssertionParseException {
		return (BooleanFormula) this.formula; 
	}
	
	@Override
	public Set<String> getVars(){
		return vars; 
	}
	
	public AssertionLogFormula addFormula(SmtFormula newFormula) throws AssertionParseException{		
		return this.formula==null? 
				new AssertionLogFormula(newFormula.formula, newFormula.getVars()):	
				JavaSmtWrapper.getInstance().addFormula(this, newFormula);
	}
}

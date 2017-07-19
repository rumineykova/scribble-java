package org.scribble.ext.assrt.ast.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

// Integer literal
public class ValueFormula extends SmtFormula {

	private Integer value; 
	public ValueFormula(String value){
		this.value = Integer.parseInt(value); 
	}
	
	@Override
	public String toString()
	{
		return this.value.toString(); 
	}
	
	@Override
	public IntegerFormula toFormula() {
		IntegerFormulaManager fmanager = JavaSmtWrapper.getInstance().imanager;
		return fmanager.makeNumber(this.value);  
	}
	
	@Override
	public Set<String> getVars(){
		Set<String> vars = new HashSet<String>(); 
		return vars; 
	}
}

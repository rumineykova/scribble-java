package org.scribble.assertions;

import java.util.HashSet;
import java.util.Set;

import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

public class VariableFormula extends StmFormula {

	private String name; 
	public VariableFormula(String name){
		this.name = name; 
	}
	
	@Override
	public String toString()
	{
		return this.name; 
	}
	
	@Override
	public IntegerFormula toFormula() {
		IntegerFormulaManager fmanager = FormulaUtil.getInstance().imanager;
		return fmanager.makeVariable(this.name);   
	}
	
	@Override
	public Set<String> getVars(){
		Set<String> vars = new HashSet<String>();
		vars.add(this.name); 
		return vars; 
	}
}

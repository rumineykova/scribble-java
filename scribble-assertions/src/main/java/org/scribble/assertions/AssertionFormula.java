package org.scribble.assertions;

import org.antlr.runtime.tree.CommonTree;
import parser.AssertionsParseException;
import parser.AssertionsScribParser;

import java.util.ArrayList;
import java.util.List;


public class AssertionFormula {
	public List<String> assertions = new ArrayList<String>(); 
	CommonTree source; 
	StmFormula stmFormula;
	
	public AssertionFormula() {
	}
	
	public AssertionFormula(CommonTree ct) {
		try {
			this.source = ct; 
			this.stmFormula = AssertionsScribParser.getInstance().parse((CommonTree)ct.getChild(0));
		} catch (AssertionsParseException e)
		{
			System.err.println("cannot parse the formula" + e.getMessage());
		}
	}
	
	public StmFormula getFormula(){
		return this.stmFormula; 
	} 
	
	/*
	public AssertionFormula(StmFormula f1, StmFormula f2) {
		
		this.stmFormula = (StmFormula) FormulaUtil.getInstance().addFormula(f1, f2);
	}
	
	public AssertionFormula(BooleanFormula f1) {
		this.stmFormula = f1;
	}
		
	
	public static Boolean IsValid(BoolFormula contex) {
		return FormulaUtil.getInstance().isSat(this, contex); 
	}*/
}

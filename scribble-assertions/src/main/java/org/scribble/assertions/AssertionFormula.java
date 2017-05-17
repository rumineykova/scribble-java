package org.scribble.assertions;

import org.antlr.runtime.tree.CommonTree;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;

import parser.AssertionsParseException;
import parser.AssertionsScribParser;

import java.util.ArrayList;
import java.util.List;


public class AssertionFormula {
	public List<String> assertions = new ArrayList<String>(); 
	CommonTree source; 
	//StmFormula formula;
	BooleanFormula z3formula; 
	
	public AssertionFormula() {
	}
	
	public AssertionFormula(CommonTree ct) {
		try {
			this.source = ct; 
			StmFormula formula = AssertionsScribParser.getInstance().parse((CommonTree)ct.getChild(0));
			this.z3formula = (BooleanFormula) formula.toFormula();  
		} catch (AssertionsParseException e)
		{
			System.err.println("cannot parse the formula" + e.getMessage());
		}
		catch (AssertionException e)
		{
			System.err.println("cannot convert the formula to z3 pormula" + e.getMessage());
		}
	}
	
	public AssertionFormula(BooleanFormula f1, BooleanFormula f2) {
		
		this.z3formula = FormulaUtil.getInstance().addFormula(f1, f2);
	}
	
	public AssertionFormula(BooleanFormula f1) {
		this.z3formula = f1;
	}
		
	public BooleanFormula getZ3Formula() {
		return this.z3formula; 
	}
	
	public Boolean IsValid(BooleanFormula contex) {
		return FormulaUtil.getInstance().IsValid(this.z3formula, contex); 
	}
}

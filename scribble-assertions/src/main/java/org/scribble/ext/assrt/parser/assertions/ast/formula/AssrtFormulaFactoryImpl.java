package org.scribble.ext.assrt.parser.assertions.ast.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.formula.AssrtArithFormula;
import org.scribble.ext.assrt.ast.formula.AssrtBinArithFormula;
import org.scribble.ext.assrt.ast.formula.AssrtBinBoolFormula;
import org.scribble.ext.assrt.ast.formula.AssrtBinCompFormula;
import org.scribble.ext.assrt.ast.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.ast.formula.AssrtFalseFormula;
import org.scribble.ext.assrt.ast.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.ast.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.ast.formula.AssrtValueFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;


public class AssrtFormulaFactoryImpl {

	public static AssrtBoolFormula parseBoolFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		return null;
	}

	public static AssrtValueFormula parseValue(CommonTree ct, String text) {
		return new AssrtValueFormula(text);
	}

	public static AssrtIntVarFormula parseVariable(CommonTree ct, String text) {
		return new AssrtIntVarFormula(text);
	}

	public static AssrtBinCompFormula CompFormula(String op, AssrtArithFormula left, AssrtArithFormula right) {
			return new AssrtBinCompFormula(op, left, right); 
	}
	
	public static AssrtBinArithFormula ArithFormula(String op, AssrtArithFormula left, AssrtArithFormula right) {
		return new AssrtBinArithFormula(op, left, right); 
	}
	
	public static AssrtBinBoolFormula BinBoolFormula(String op, AssrtBoolFormula left, AssrtBoolFormula right) {
		return new AssrtBinBoolFormula(op, left, right); 
	}
	
	public static AssrtTrueFormula AssrtTrueFormula() 
	{
		return new AssrtTrueFormula();
	}
	
	public static AssrtFalseFormula AssrtFalseFormula() 
	{
		return new AssrtFalseFormula();
	}
}

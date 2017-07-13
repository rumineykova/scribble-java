package org.scribble.ext.assrt.parser.assertions.ast.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.formula.ArithFormula;
import org.scribble.ext.assrt.ast.formula.AssertionVariableFormula;
import org.scribble.ext.assrt.ast.formula.BoolFormula;
import org.scribble.ext.assrt.ast.formula.CompFormula;
import org.scribble.ext.assrt.ast.formula.SmtFormula;
import org.scribble.ext.assrt.ast.formula.ValueFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;


public class FormulaFactoryImpl {

	public static BoolFormula parseBoolFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		return null;
	}

	public static ValueFormula parseValue(CommonTree ct, String text) {
		return new ValueFormula(text);
	}

	public static AssertionVariableFormula parseVariable(CommonTree ct, String text) {
		return new AssertionVariableFormula(text);
	}

	public static CompFormula CompFormula(String op, SmtFormula left, SmtFormula right) {
			return new CompFormula(op, left, right); 
	}
	
	public static BoolFormula BoolFormula(String op, SmtFormula left, SmtFormula right) {
		return new BoolFormula(op, left, right); 
	}
	
	public static ArithFormula ArithFormula(String op, SmtFormula left, SmtFormula right) {
		return new ArithFormula(op, left, right); 
	}
	
}

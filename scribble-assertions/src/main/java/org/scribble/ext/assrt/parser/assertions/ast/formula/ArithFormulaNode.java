package org.scribble.ext.assrt.parser.assertions.ast.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.formula.ArithFormula;
import org.scribble.ext.assrt.ast.formula.SmtFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;


public class ArithFormulaNode implements FormulaNode {

	private static Integer CHILD_OP_INDEX = 0; 
	private static Integer CHILD_LEFT_FORMULA_INDEX = 1;
	private static Integer CHILD_RIGHT_FORMULA_INDEX = 2;
	
	public static ArithFormula parseArithFormula(
			AssrtAssertParser parser, CommonTree ct) { //throws AssertionsParseException {
	
		String op = ct.getChild(CHILD_OP_INDEX).getText(); 
		SmtFormula left = parser.parse((CommonTree)ct.getChild(CHILD_LEFT_FORMULA_INDEX)); 
		SmtFormula right = parser.parse((CommonTree)ct.getChild(CHILD_RIGHT_FORMULA_INDEX));
		
		return FormulaFactoryImpl.ArithFormula(op, left, right); 
	
	
	}

}

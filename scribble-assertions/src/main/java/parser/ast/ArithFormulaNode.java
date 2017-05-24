package parser.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.assertions.ArithFormula;
import org.scribble.assertions.StmFormula;

import parser.AssertionsParseException;
import parser.AssertionsScribParser;
import parser.FormulaFactoryImpl;


public class ArithFormulaNode implements FormulaNode {

	private static Integer CHILD_OP_INDEX = 1; 
	private static Integer CHILD_LEFT_FORMULA_INDEX = 0;
	private static Integer CHILD_RIGHT_FORMULA_INDEX = 2;
	
	public static ArithFormula parseArithFormula(
			AssertionsScribParser parser, CommonTree ct) throws AssertionsParseException {
	
		String op = ct.getChild(CHILD_OP_INDEX).getText(); 
		StmFormula left = parser.parse((CommonTree)ct.getChild(CHILD_LEFT_FORMULA_INDEX)); 
		StmFormula right = parser.parse((CommonTree)ct.getChild(CHILD_RIGHT_FORMULA_INDEX));
		
		return FormulaFactoryImpl.ArithFormula(op, left, right); 
	
	
	}

}

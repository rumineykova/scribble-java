package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;
import org.scribble.ext.assrt.sesstype.formula.AssrtArithFormula;
import org.scribble.ext.assrt.sesstype.formula.AssrtBinArithFormula;


public class AntlrArithFormula
{
	private static Integer CHILD_OP_INDEX = 1; 
	private static Integer CHILD_LEFT_FORMULA_INDEX = 0;
	private static Integer CHILD_RIGHT_FORMULA_INDEX = 2;
	
	public static AssrtBinArithFormula parseArithFormula(
			AssrtAssertParser parser, CommonTree ct) { //throws AssertionsParseException {
	
		String op = ct.getChild(CHILD_OP_INDEX).getText(); 
		AssrtArithFormula left = (AssrtArithFormula) parser.parse((CommonTree)ct.getChild(CHILD_LEFT_FORMULA_INDEX)); 
		AssrtArithFormula right = (AssrtArithFormula) parser.parse((CommonTree)ct.getChild(CHILD_RIGHT_FORMULA_INDEX));
		
		return AssrtFormulaFactory.ArithFormula(op, left, right); 
	
	
	}

}


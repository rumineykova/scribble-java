package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;
import org.scribble.ext.assrt.sesstype.formula.AssrtBoolFormula;

// N.B. parsed to type objects, not AST (source not recorded -- e.g., for equals/hashCode)
// To record source, need additional AST classes from which these type objects should be derived
public class AntlrBoolFormula
{	
	private static Integer CHILD_OP_INDEX = 1; 
	private static Integer CHILD_LEFT_FORMULA_INDEX = 0;
	private static Integer CHILD_RIGHT_FORMULA_INDEX = 2;
	
	public static AssrtBoolFormula parseBoolFormula(
			AssrtAssertParser parser, CommonTree ct) { //throws AssertionsParseException {
		
		String op = ct.getChild(CHILD_OP_INDEX).getText(); 
		AssrtBoolFormula left = (AssrtBoolFormula) parser.parse((CommonTree)ct.getChild(CHILD_LEFT_FORMULA_INDEX)); 
		AssrtBoolFormula right = (AssrtBoolFormula) parser.parse((CommonTree)ct.getChild(CHILD_RIGHT_FORMULA_INDEX));
		
		return AssrtFormulaFactory.BinBoolFormula(op, left, right); 
		
	}
}

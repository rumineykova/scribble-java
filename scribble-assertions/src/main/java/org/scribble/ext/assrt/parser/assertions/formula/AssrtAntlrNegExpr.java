package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;


public class AssrtAntlrNegExpr
{
	private static Integer CHILD_FORMULA_INDEX = 0;
	
	public static AssrtSmtFormula<?> parseNegExpr(AssrtAntlrToFormulaParser parser, CommonTree root) //throws AssertionsParseException {
	{	
		AssrtBoolFormula child = (AssrtBoolFormula) parser.parse(getChild(root)); 
		return AssrtFormulaFactory.AssrtNeg(child); 
	}
	
	public static CommonTree getChild(CommonTree root)
	{
		return (CommonTree) root.getChild(CHILD_FORMULA_INDEX);
	}
}


package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;


public class AssrtAntlrNegExpr
{
	private static Integer CHILD_FORMULA_INDEX = 0;
	
	public static AssrtSmtFormula<?> parseNegExpr(AssrtAntlrToFormulaParser parser, CommonTree root) //throws AssertionsParseException {
	{	
		AssrtBFormula child = (AssrtBFormula) parser.parse(getChild(root)); 
		return AssrtFormulaFactory.AssrtNeg(child); 
	}
	
	public static CommonTree getChild(CommonTree root)
	{
		return (CommonTree) root.getChild(CHILD_FORMULA_INDEX);
	}
}


package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBinAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;


public class AssrtAntlrArithExpr
{
	private static Integer CHILD_OP_INDEX = 1; 
	private static Integer CHILD_LEFT_FORMULA_INDEX = 0;
	private static Integer CHILD_RIGHT_FORMULA_INDEX = 2;
	
	//public static AssrtBinArithFormula parseBinArithFormula(AssrtAntlrToFormulaParser parser, CommonTree root) //throws AssertionsParseException {
	public static AssrtSmtFormula<?> parseArithExpr(AssrtAntlrToFormulaParser parser, CommonTree root) //throws AssertionsParseException {
	{	
		AssrtSmtFormula<?> left = parser.parse(getLeftChild(root)); 
		if (root.getChildCount() < 2)
		{
			return left;
		}
		AssrtBinAFormula.Op op = parseOp(getOpChild(root)); 
		AssrtAFormula right = (AssrtAFormula) parser.parse(getRightChild(root));
		return AssrtFormulaFactory.AssrtBinArith(op, (AssrtAFormula) left, right); 
	}
	
	public static CommonTree getOpChild(CommonTree root)
	{
		return (CommonTree) root.getChild(CHILD_OP_INDEX);
	}
	
	public static CommonTree getLeftChild(CommonTree root)
	{
		return (CommonTree) root.getChild(CHILD_LEFT_FORMULA_INDEX);
	}
	
	public static CommonTree getRightChild(CommonTree root)
	{
		return (CommonTree) root.getChild(CHILD_RIGHT_FORMULA_INDEX);
	}

	private static AssrtBinAFormula.Op parseOp(CommonTree op) 
	{
		switch (op.getText()) 
		{
			case "+": return AssrtBinAFormula.Op.Add;
			case "-": return AssrtBinAFormula.Op.Subt;
			case "*": return AssrtBinAFormula.Op.Mult;
			default:  throw new RuntimeException("[assrt] Shouldn't get in here: " + op.getText());  // Due to AssrtAssertions.g
		}
	}
}


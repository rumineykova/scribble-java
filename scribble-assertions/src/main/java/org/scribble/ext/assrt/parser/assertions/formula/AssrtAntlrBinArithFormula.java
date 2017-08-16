package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBinArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;


public class AssrtAntlrBinArithFormula
{
	private static Integer CHILD_OP_INDEX = 1; 
	private static Integer CHILD_LEFT_FORMULA_INDEX = 0;
	private static Integer CHILD_RIGHT_FORMULA_INDEX = 2;
	
	//public static AssrtBinArithFormula parseBinArithFormula(AssrtAntlrToFormulaParser parser, CommonTree root) //throws AssertionsParseException {
	public static AssrtSmtFormula<?> parseBinArithFormula(AssrtAntlrToFormulaParser parser, CommonTree root) //throws AssertionsParseException {
	{	
		AssrtSmtFormula<?> left = parser.parse(getLeftChild(root)); 
		if (root.getChildCount() < 2)
		{
			return left;
		}
		AssrtBinArithFormula.Op op = parseOp(getOpChild(root)); 
		AssrtArithFormula right = (AssrtArithFormula) parser.parse(getRightChild(root));
		return AssrtFormulaFactory.AssrtBinArith(op, (AssrtArithFormula) left, right); 
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

	private static AssrtBinArithFormula.Op parseOp(CommonTree op) 
	{
		switch (op.getText()) 
		{
			case "+": return AssrtBinArithFormula.Op.Add;
			case "-": return AssrtBinArithFormula.Op.Subt;
			case "*": return AssrtBinArithFormula.Op.Mult;
			default:  throw new RuntimeException("[assrt] Shouldn't get in here: " + op.getText());  // Due to AssrtAssertions.g
		}
	}
}


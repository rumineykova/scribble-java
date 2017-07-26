package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;
import org.scribble.ext.assrt.sesstype.formula.AssrtArithFormula;
import org.scribble.ext.assrt.sesstype.formula.AssrtBinCompFormula;

public class AntlrBinCompFormula
{
	private static Integer CHILD_OP_INDEX = 1; 
	private static Integer CHILD_LEFT_FORMULA_INDEX = 0;
	private static Integer CHILD_RIGHT_FORMULA_INDEX = 2;
	
	public static AssrtBinCompFormula parseBinCompFormula(AssrtAssertParser parser, CommonTree root) //throws AssertionsParseException {
	{
		AssrtBinCompFormula.Op op = parseOp(getOpChild(root)); 
		AssrtArithFormula left = (AssrtArithFormula) parser.parse(getLeftChild(root)); 
		AssrtArithFormula right = (AssrtArithFormula) parser.parse(getRightChild(root));
		return AssrtFormulaFactory.AssrtBinComp(op, left, right); 
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

	private static AssrtBinCompFormula.Op parseOp(CommonTree op) 
	{
		switch (op.getText()) 
		{
			case "=": return AssrtBinCompFormula.Op.Eq;
			case "<": return AssrtBinCompFormula.Op.LessThan;
			case ">": return AssrtBinCompFormula.Op.GreaterThan;
			default:  throw new RuntimeException("[assrt] Shouldn't get in here: " + op.getText());  // Due to AssrtAssertions.g
		}
	}
}

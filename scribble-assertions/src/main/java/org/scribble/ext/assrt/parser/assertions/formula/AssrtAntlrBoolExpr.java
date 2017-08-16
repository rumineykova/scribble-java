package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.type.formula.AssrtBinBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;

// N.B. parsed to type objects, not AST (source not recorded -- e.g., for equals/hashCode)
// To record source, need additional AST classes from which these type objects should be derived
public class AssrtAntlrBoolExpr
{	
	private static Integer CHILD_OP_INDEX = 1; 
	private static Integer CHILD_LEFT_FORMULA_INDEX = 0;
	private static Integer CHILD_RIGHT_FORMULA_INDEX = 2;
	
	public static AssrtSmtFormula<?> parseBoolExpr(AssrtAntlrToFormulaParser parser, CommonTree root) //throws AssertionsParseException {
	{	
		AssrtSmtFormula<?> left = parser.parse(getLeftChild(root)); 
		if (root.getChildCount() < 2)
		{
			return left;
		}
		AssrtBinBoolFormula.Op op = parseOp(getOpChild(root)); 
		AssrtBoolFormula right = (AssrtBoolFormula) parser.parse(getRightChild(root));
		return AssrtFormulaFactory.AssrtBinBool(op, (AssrtBoolFormula) left, right); 
		
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

	private static AssrtBinBoolFormula.Op parseOp(CommonTree op) 
	{
		switch (op.getText()) 
		{
			case "&&": return AssrtBinBoolFormula.Op.And;
			case "||": return AssrtBinBoolFormula.Op.Or;
			default:  throw new RuntimeException("[assrt] Shouldn't get in here: " + op.getText());  // Due to AssrtAssertions.g
		}
	}
}

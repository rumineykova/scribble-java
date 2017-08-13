package org.scribble.ext.assrt.parser.assertions.formula;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtUnPredicateFormula;

public class AssrtAntlrUnPredicateFormula
{
	public static final int NAME_INDEX = 0;
	public static final int ARITH_EXPR_LIST_INDEX = 1;
	
	public static AssrtUnPredicateFormula parseUnPredicate(AssrtAntlrToFormulaParser parser, CommonTree root)
	{
		String name = getNameChild(root).getText();
		List<AssrtArithFormula> args = parseArithExprList(parser, getArithExprListChild(root));
		return AssrtFormulaFactory.AssrtUnPredicate(name, args);
	}
	
	public static List<AssrtArithFormula> parseArithExprList(AssrtAntlrToFormulaParser parser, CommonTree expr)
	{
		return ((List<?>) expr.getChildren()).stream()
				.map(c -> (AssrtArithFormula) parser.parse((CommonTree) c)).collect(Collectors.toList());
	}
	
	public static CommonTree getNameChild(CommonTree root)
	{
		return (CommonTree) root.getChild(NAME_INDEX);
	}
	
	public static CommonTree getArithExprListChild(CommonTree root)
	{
		return (CommonTree) root.getChild(ARITH_EXPR_LIST_INDEX);
	}
}

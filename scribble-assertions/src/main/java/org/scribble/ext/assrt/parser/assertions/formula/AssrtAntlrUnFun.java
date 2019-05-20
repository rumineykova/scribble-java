package org.scribble.ext.assrt.parser.assertions.formula;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtUnintPredicateFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;

public class AssrtAntlrUnFun
{
	public static final int NAME_INDEX = 0;
	public static final int UNFUNARGLIST_INDEX = 1;
	
	public static AssrtUnintPredicateFormula parseUnFun(AssrtAntlrToFormulaParser parser, CommonTree root)
	{
		String name = getNameChild(root).getText();
		CommonTree arglist = getUnFunArgListChild(root);
		List<AssrtAFormula> args = (arglist.getChildCount() > 0)
				? parseUnFunArgList(parser, arglist)
				: Collections.emptyList();
		return AssrtFormulaFactory.AssrtUnPredicate(name, args);  // Currently assumed to be a predicate on ints
	}
	
	public static List<AssrtAFormula> parseUnFunArgList(AssrtAntlrToFormulaParser parser, CommonTree args)
	{
		return ((List<?>) args.getChildren()).stream()
				.map(c -> (AssrtAFormula) parser.parse((CommonTree) c)).collect(Collectors.toList());
	}
	
	public static CommonTree getNameChild(CommonTree root)
	{
		return (CommonTree) root.getChild(NAME_INDEX);
	}
	
	public static CommonTree getUnFunArgListChild(CommonTree root)
	{
		return (CommonTree) root.getChild(UNFUNARGLIST_INDEX);
	}
}

package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtIntValFormula;

public class AssrtAntlrIntVal
{
	public static AssrtIntValFormula parseIntVal(AssrtAntlrToFormulaParser parser, CommonTree root)
	{
		return AssrtFormulaFactory.AssrtIntVal(Integer.parseInt(root.getChild(0).getText()));
	}

	public static AssrtIntValFormula parseNegIntVal(AssrtAntlrToFormulaParser parser, CommonTree root)
	{
		return AssrtFormulaFactory.AssrtIntVal(0 - Integer.parseInt(root.getChild(0).getText()));
	}
}

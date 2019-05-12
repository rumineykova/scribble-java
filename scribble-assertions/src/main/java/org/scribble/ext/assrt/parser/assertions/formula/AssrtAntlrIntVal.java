package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.core.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtIntValFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;

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

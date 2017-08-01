package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtIntValFormula;

public class AssrtAntlrIntValFormula
{
	public static AssrtIntValFormula parseIntValFormula(AssrtAntlrToFormulaParser assertionsScribParser, CommonTree root)
	{
		return AssrtFormulaFactory.AssrtIntVal(Integer.parseInt(root.getChild(0).getText()));
	}
}

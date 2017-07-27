package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;

public class AssrtAntlrIntVarFormula
{
	public static AssrtIntVarFormula parseIntVarFormula(AssrtAntlrToFormulaParser assertionsScribParser, CommonTree ct)
	{
		return AssrtFormulaFactory.AssrtIntVar(ct.getChild(0).getText());
	}
}

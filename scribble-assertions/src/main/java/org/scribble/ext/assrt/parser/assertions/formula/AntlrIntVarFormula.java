package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;

public class AntlrIntVarFormula
{
	public static AssrtIntVarFormula parseIntVarFormula(AssrtAssertParser assertionsScribParser, CommonTree ct)
	{
		return AssrtFormulaFactory.AssrtIntVar(ct.getChild(0).getText());
	}
}

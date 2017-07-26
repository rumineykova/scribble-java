package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;
import org.scribble.ext.assrt.type.formula.AssrtIntValFormula;

public class AntlrIntValFormula
{
	public static AssrtIntValFormula parseIntValFormula(AssrtAssertParser assertionsScribParser, CommonTree ct)
	{
		return AssrtFormulaFactory.AssrtIntVal(ct.getChild(0).getText());
	}
}

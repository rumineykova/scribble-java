package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;
import org.scribble.ext.assrt.sesstype.formula.AssrtIntValFormula;

public class AntlrIntValFormula
{
	public static AssrtIntValFormula parseValueFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		return AssrtFormulaFactory.parseValue(ct, ct.getChild(0).getText());
	}
}

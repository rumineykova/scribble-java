package org.scribble.ext.assrt.parser.assertions.ast.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;
import org.scribble.ext.assrt.sesstype.formula.AssrtValueFormula;

public class AntlrValueNode implements AntlrFormulaNode {

	public static AssrtValueFormula parseValueFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		return AssrtFormulaFactoryImpl.parseValue(ct, ct.getChild(0).getText());
	}
}

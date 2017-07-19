package org.scribble.ext.assrt.parser.assertions.ast.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.formula.ValueFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;

public class AntlrValueNode implements AntlrFormulaNode {

	public static ValueFormula parseValueFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		return AssrtFormulaFactoryImpl.parseValue(ct, ct.getChild(0).getText());
	}
}

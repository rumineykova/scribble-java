package parser.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.assertions.ValueFormula;

import parser.AssertionsScribParser;
import parser.FormulaFactoryImpl;

public class ValueNode implements FormulaNode {

	public static ValueFormula parseValueFormula(
			AssertionsScribParser assertionsScribParser, CommonTree ct) {
		return FormulaFactoryImpl.parseValue(ct, ct.getChild(0).getText());
	}
}

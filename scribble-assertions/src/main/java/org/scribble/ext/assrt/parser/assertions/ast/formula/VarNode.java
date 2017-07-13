package org.scribble.ext.assrt.parser.assertions.ast.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.formula.AssertionVariableFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;

public class VarNode implements FormulaNode {

	public static AssertionVariableFormula parseVarFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		
		return FormulaFactoryImpl.parseVariable(ct, ct.getChild(0).getText());
	}

}

package org.scribble.ext.assrt.parser.assertions.ast.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.formula.AssertionVariableFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;

public class AntlrVarNode implements AntlrFormulaNode {

	public static AssertionVariableFormula parseVarFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		
		return AntlrFormulaFactoryImpl.parseVariable(ct, ct.getChild(0).getText());
	}

}

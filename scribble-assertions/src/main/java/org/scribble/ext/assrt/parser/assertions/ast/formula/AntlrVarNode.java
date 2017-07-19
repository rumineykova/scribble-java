package org.scribble.ext.assrt.parser.assertions.ast.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;

public class AntlrVarNode implements AntlrFormulaNode {

	public static AssrtIntVarFormula parseVarFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		
		return AssrtFormulaFactoryImpl.parseVariable(ct, ct.getChild(0).getText());
	}

}

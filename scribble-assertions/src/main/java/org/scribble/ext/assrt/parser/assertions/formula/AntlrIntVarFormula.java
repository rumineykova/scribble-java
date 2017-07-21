package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;
import org.scribble.ext.assrt.sesstype.formula.AssrtIntVarFormula;

public class AntlrIntVarFormula
{
	public static AssrtIntVarFormula parseVarFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		
		return AssrtFormulaFactory.parseVariable(ct, ct.getChild(0).getText());
	}

}
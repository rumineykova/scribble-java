package org.scribble.ext.assrt.parser.assertions.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;

public class AssrtAntlrIntVar
{
	public static AssrtIntVarFormula parseIntVar(AssrtAntlrToFormulaParser parser, CommonTree root)
	{
		return AssrtFormulaFactory.AssrtIntVar(root.getChild(0).getText());
	}
}

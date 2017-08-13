package org.scribble.ext.assrt.parser.assertions;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertionsAntlrConstants.AssrtAntlrNodeType;

public class AssrtAntlrToFormulaParserUtil
{
	public static AssrtAntlrNodeType getAntlrNodeType(CommonTree ct)
	{
		String type = ct.getToken().getText();
		switch (type)
		{
			case AssrtAssertionsAntlrConstants.BINBOOLEXPR_NODE_TYPE:  return AssrtAntlrNodeType.BINBOOLEXPR;
			case AssrtAssertionsAntlrConstants.BINCOMPEXPR_NODE_TYPE:  return AssrtAntlrNodeType.BINCOMPEXPR;
			case AssrtAssertionsAntlrConstants.BINARITHEXPR_NODE_TYPE: return AssrtAntlrNodeType.BINARITHEXPR;
			
			case AssrtAssertionsAntlrConstants.UNPRED_NODE_TYPE:          return AssrtAntlrNodeType.UNPRED;
			case AssrtAssertionsAntlrConstants.ARITH_EXPR_LIST_NODE_TYPE: return AssrtAntlrNodeType.ARITH_EXPR_LIST;

			case AssrtAssertionsAntlrConstants.INTVAR_NODE_TYPE:       return AssrtAntlrNodeType.INTVAR;
			case AssrtAssertionsAntlrConstants.INTVAL_NODE_TYPE:       return AssrtAntlrNodeType.INTVAL;

			case AssrtAssertionsAntlrConstants.FALSE_NODE_TYPE:        return AssrtAntlrNodeType.FALSE;
			case AssrtAssertionsAntlrConstants.TRUE_NODE_TYPE:         return AssrtAntlrNodeType.TRUE;

			// Nodes without a "node type", e.g. parameter names, fall in here
			default: throw new RuntimeException("Unknown ANTLR node type label for assertion of type: " + type);
		}
	}
}

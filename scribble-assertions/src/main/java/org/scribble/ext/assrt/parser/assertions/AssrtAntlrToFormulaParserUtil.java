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
			case "ROOT": return AssrtAntlrNodeType.ROOT;

			case AssrtAssertionsAntlrConstants.BOOLEXPR_NODE_TYPE:     return AssrtAntlrNodeType.BOOLEXPR;
			case AssrtAssertionsAntlrConstants.COMPEXPR_NODE_TYPE:     return AssrtAntlrNodeType.COMPEXPR;
			case AssrtAssertionsAntlrConstants.ARITHEXPR_NODE_TYPE:    return AssrtAntlrNodeType.ARITHEXPR;
			case AssrtAssertionsAntlrConstants.NEGEXPR_NODE_TYPE:      return AssrtAntlrNodeType.NEGEXPR;
			
			case AssrtAssertionsAntlrConstants.UNFUN_NODE_TYPE:        return AssrtAntlrNodeType.UNFUN;
			case AssrtAssertionsAntlrConstants.UNFUNARGLIST_NODE_TYPE: return AssrtAntlrNodeType.UNFUNARGLIST;

			case AssrtAssertionsAntlrConstants.INTVAR_NODE_TYPE:        return AssrtAntlrNodeType.INTVAR;
			case AssrtAssertionsAntlrConstants.INTVAL_NODE_TYPE:        return AssrtAntlrNodeType.INTVAL;
			case AssrtAssertionsAntlrConstants.NEGINTVAL_NODE_TYPE:     return AssrtAntlrNodeType.NEGINTVAL;

			case AssrtAssertionsAntlrConstants.FALSE_NODE_TYPE:         return AssrtAntlrNodeType.FALSE;
			case AssrtAssertionsAntlrConstants.TRUE_NODE_TYPE:          return AssrtAntlrNodeType.TRUE;
			
			//case AssrtAssertionsAntlrConstants.ASSRT_STATEVARDECLLIST_NODE_TYPE: return AssrtAntlrNodeType.ASSRT_STATEVARDECLLIST;

			// Nodes without a "node type", e.g. parameter names, fall in here
			default: throw new RuntimeException("Unknown ANTLR node type label for assertion of type: " + type);
		}
	}
}

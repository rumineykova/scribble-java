package org.scribble.ext.assrt.parser.assertions;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrConstants.AssrtAntlrNodeType;

public class AssrtParserUtil  // Cf. ScribParserUtil
{
	public static AssrtAntlrNodeType getAntlrNodeType(CommonTree ct)
	{
		String type = ct.getToken().getText();
		switch (type)
		{
			case AssrtAntlrConstants.BEXPR_NODE_TYPE: return AssrtAntlrNodeType.BEXPR;
			case AssrtAntlrConstants.CEXPR_NODE_TYPE: return AssrtAntlrNodeType.CEXPR;
			case AssrtAntlrConstants.AEXPR_NODE_TYPE: return AssrtAntlrNodeType.AEXPR;
			case AssrtAntlrConstants.VAR_NODE_TYPE:   return AssrtAntlrNodeType.VAR;
			case AssrtAntlrConstants.VALUE_NODE_TYPE: return AssrtAntlrNodeType.VALUE;

			case AssrtAntlrConstants.FALSE_NODE_TYPE: return AssrtAntlrNodeType.FALSE;
			case AssrtAntlrConstants.TRUE_NODE_TYPE: return AssrtAntlrNodeType.TRUE;

			// Nodes without a "node type", e.g. parameter names, fall in here
			default: throw new RuntimeException("Unknown ANTLR node type label for assertion of type: " + type);
		}
	}
}

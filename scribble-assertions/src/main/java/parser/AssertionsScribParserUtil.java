package parser;

import org.antlr.runtime.tree.CommonTree;

import parser.AssertionsAntlrConstants.AssertionNodeType;

public class AssertionsScribParserUtil {

	public static AssertionNodeType getAntlrNodeType(CommonTree n)
	{
		String type = n.getToken().getText();
		switch (type)
		{
			//case AntlrConstants.EMPTY_PARAMETERDECLLST_NODE_TYPE: return AntlrNodeType.EMPTY_PARAMETERDECLLST;
			case AssertionsAntlrConstants.BEXPR_NODE_TYPE: return AssertionNodeType.BEXPR;
			case AssertionsAntlrConstants.CEXPR_NODE_TYPE: return AssertionNodeType.CEXPR;
			case AssertionsAntlrConstants.AEXPR_NODE_TYPE: return AssertionNodeType.AEXPR;
			case AssertionsAntlrConstants.VAR_NODE_TYPE: return AssertionNodeType.VAR;
			case AssertionsAntlrConstants.VALUE_NODE_TYPE: return AssertionNodeType.VALUE;
			default:
			{
				// Nodes without a "node type", e.g. parameter names, fall in here
				throw new RuntimeException("Unknown ANTLR node type label for assertion of type: " + type);
			} 
		}
	}
}

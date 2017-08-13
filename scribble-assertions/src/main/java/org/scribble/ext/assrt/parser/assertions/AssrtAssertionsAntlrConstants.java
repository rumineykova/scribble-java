package org.scribble.ext.assrt.parser.assertions;

// Constants declared in Assertions.g ANTLR grammar -- cf. ScribbleAntlrConstants
public class AssrtAssertionsAntlrConstants
{
	public static final String EMPTY_LIST = "EMPTY_LIST";

	// For AssrtScribParser
	public static final String BINBOOLEXPR_NODE_TYPE = "BINBOOLEXPR";
	public static final String BINCOMPEXPR_NODE_TYPE = "BINCOMPEXPR";
	public static final String BINARITHEXPR_NODE_TYPE = "BINARITHEXPR";
	
	public static final String UNPRED_NODE_TYPE = "UNPRED";
	public static final String ARITH_EXPR_LIST_NODE_TYPE = "ARITH_EXPR_LIST";
	
	public static final String INTVAR_NODE_TYPE = "INTVAR";
	public static final String INTVAL_NODE_TYPE = "INTVAL";

	public static final String FALSE_NODE_TYPE = "FALSE";
	public static final String TRUE_NODE_TYPE = "TRUE";

	public enum AssrtAntlrNodeType
	{
		// For AssrtScribParser
		BINBOOLEXPR, BINCOMPEXPR, BINARITHEXPR, UNPRED, ARITH_EXPR_LIST, INTVAR, INTVAL, FALSE, TRUE
	}
}

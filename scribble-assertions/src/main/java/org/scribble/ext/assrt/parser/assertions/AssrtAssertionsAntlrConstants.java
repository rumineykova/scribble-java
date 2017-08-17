package org.scribble.ext.assrt.parser.assertions;

// Constants declared in Assertions.g ANTLR grammar -- cf. ScribbleAntlrConstants
public class AssrtAssertionsAntlrConstants
{
	public static final String EMPTY_LIST = "EMPTY_LIST";

	// For AssrtScribParser
	public static final String BOOLEXPR_NODE_TYPE = "BOOLEXPR";
	public static final String COMPEXPR_NODE_TYPE = "COMPEXPR";
	public static final String ARITHEXPR_NODE_TYPE = "ARITHEXPR";
	public static final String NEGEXPR_NODE_TYPE = "NEGEXPR";
	
	public static final String UNFUN_NODE_TYPE = "UNFUN";
	public static final String UNFUNARGLIST_NODE_TYPE = "UNFUNARGLIST";
	
	public static final String INTVAR_NODE_TYPE = "INTVAR";
	public static final String INTVAL_NODE_TYPE = "INTVAL";
	public static final String NEGINTVAL_NODE_TYPE = "NEGINTVAL";

	public static final String FALSE_NODE_TYPE = "FALSE";
	public static final String TRUE_NODE_TYPE = "TRUE";
	
	//public static final String ASSRT_STATEVARDECLLIST_NODE_TYPE = "ASSRT_STATEVARDECLLIST";  // Parsed internally by AssrtAntlrGProtocolHeader

	public enum AssrtAntlrNodeType
	{
		// For AssrtScribParser
		BOOLEXPR, COMPEXPR, ARITHEXPR, NEGEXPR,
		UNFUN, UNFUNARGLIST,
		INTVAR, INTVAL, NEGINTVAL, FALSE, TRUE,
		//ASSRT_STATEVARDECLLIST
	}
}

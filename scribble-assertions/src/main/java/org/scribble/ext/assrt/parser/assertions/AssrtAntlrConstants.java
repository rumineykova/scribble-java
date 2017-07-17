package org.scribble.ext.assrt.parser.assertions;

public class AssrtAntlrConstants  // Cf. AntlrConstants
{
	// For AssrtScribParser
	public static final String BEXPR_NODE_TYPE = "BEXPR";
	public static final String CEXPR_NODE_TYPE = "CEXPR";
	public static final String AEXPR_NODE_TYPE = "AEXPR";
	public static final String VAR_NODE_TYPE = "VAR";
	public static final String VALUE_NODE_TYPE = "VALUE";

	public static final String FALSE_NODE_TYPE = "FALSE_FORMULA";
	public static final String TRUE_NODE_TYPE = "TRUE_FORMULA";

	public enum AssrtAntlrNodeType
	{
		// For AssrtScribParser
		BEXPR, CEXPR, AEXPR, VAR, VALUE, FALSE, TRUE
	}
}

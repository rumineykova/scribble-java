package org.scribble.ext.assrt.ast.formula;

@Deprecated  // Parsing errors should be caught by ANTLR
public class AssertionParseException extends Exception
{
	private static final long serialVersionUID = 1L;

	public AssertionParseException(String string) {
		// TODO Auto-generated constructor stub
		super(string);
	}

}

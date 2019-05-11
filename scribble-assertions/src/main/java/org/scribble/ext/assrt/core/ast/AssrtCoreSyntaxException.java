package org.scribble.ext.assrt.core.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.util.AntlrSourceException;

// For parsing errors due to core syntax restrictions (vs. "full" Scribble) -- distinction used for JUnit testing (i.e., to indicate non core syntax that are otherwise valid protocols)
// i.e., should only be thrown by AssrtCoreGProtocolDeclTranslator
// N.B. so should not be used for actual "semantic" WF errors
public class AssrtCoreSyntaxException extends AntlrSourceException   // N.B. not Scribble/AssrttException -- cf. AssrtCoreTestBase::tests
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public AssrtCoreSyntaxException(String arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public AssrtCoreSyntaxException(Throwable arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public AssrtCoreSyntaxException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public AssrtCoreSyntaxException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public AssrtCoreSyntaxException(CommonTree blame, String arg0)
	{
		super(blame, arg0);
		// TODO Auto-generated constructor stub
	}

}

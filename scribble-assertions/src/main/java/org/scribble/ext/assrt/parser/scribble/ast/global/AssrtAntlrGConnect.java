package org.scribble.ext.assrt.parser.scribble.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GConnect;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.AssrtBExprNode;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.parser.scribble.AssrtAntlrToScribParser;
import org.scribble.parser.scribble.AntlrToScribParser;
import org.scribble.parser.scribble.ast.global.AntlrGMessageTransfer;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

public class AssrtAntlrGConnect
{
	// "Original" indices "shifted down" -- FIXME: better pattern
	public static final int MESSAGE_CHILD_INDEX = 3;  // Different than AssrtAntlrGMessageTransfer
	public static final int SOURCE_CHILD_INDEX = 1;
	public static final int DESTINATION_CHILD_INDEX = 2;

	public static final int ASSERTION_CHILD_INDEX = 0;

	public static GConnect parseAssrtGConnect(AntlrToScribParser parser, CommonTree ct, AssrtAstFactory af) throws ScribParserException
	{
		RoleNode src = AntlrSimpleName.toRoleNode(getSourceChild(ct), af);
		MessageNode msg = AntlrGMessageTransfer.parseMessage(parser, getMessageChild(ct), af);
		RoleNode dest = AntlrSimpleName.toRoleNode(getDestinationChild(ct), af);

		// FIXME: factor out of AssrtAntlrGMessageTransfer
		AssrtBExprNode ass = AssrtAntlrGMessageTransfer.parseAssertion(((AssrtAntlrToScribParser) parser).ap, getAssertionChild(ct), af);

		return ((AssrtAstFactory) af).AssrtGConnect(ct, msg, src, dest, ass);
	}

	// Originals re-defined to use new indices -- FIXME: better pattern
	public static CommonTree getMessageChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(AssrtAntlrGConnect.MESSAGE_CHILD_INDEX);
	}

	public static CommonTree getSourceChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(AssrtAntlrGConnect.SOURCE_CHILD_INDEX);
	}

	public static CommonTree getDestinationChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(AssrtAntlrGConnect.DESTINATION_CHILD_INDEX);
	}

	public static CommonTree getAssertionChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(AssrtAntlrGConnect.ASSERTION_CHILD_INDEX);
	}
}

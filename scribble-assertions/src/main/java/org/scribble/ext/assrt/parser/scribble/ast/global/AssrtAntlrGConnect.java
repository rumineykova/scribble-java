package org.scribble.ext.assrt.parser.scribble.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GConnect;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.parser.scribble.AssrtScribParser;
import org.scribble.parser.scribble.ScribParser;
import org.scribble.parser.scribble.ast.global.AntlrGMessageTransfer;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

public class AssrtAntlrGConnect
{
	public static final int ASSERTION_CHILD_INDEX = 0;

	// "Original" indices "shifted down" -- FIXME: better pattern
	public static final int MESSAGE_CHILD_INDEX = 3;  // Different than AssrtAntlrGMessageTransfer
	public static final int SOURCE_CHILD_INDEX = 1;
	public static final int DESTINATION_CHILD_INDEX = 2;

	public static GConnect parseAssrtGConnect(ScribParser parser, CommonTree ct, AssrtAstFactory af) throws ScribParserException
	{
		//AssrtAssertionNode assertion = parseAssertion(((AssrtScribParser) parser).ap, getAssertionChild(ct));   
		AssrtAssertion ass = AssrtAntlrGMessageTransfer.parseAssertion(((AssrtScribParser) parser).ap, getAssertionChild(ct), af);   

		/*RoleNode src = AntlrSimpleName.toRoleNode(getSourceChild(ct), af);
		MessageNode msg = AntlrGMessageTransfer.parseMessage(parser, getMessageChild(ct), af);
		List<RoleNode> dests = getDestChildren(ct).stream()
				.map(d -> AntlrSimpleName.toRoleNode(d, af)).collect(Collectors.toList());
		return ((AssrtAstFactory) af).AssrtGMessageTransfer(ct, src, msg, dests, assertion);*/

		RoleNode src = AntlrSimpleName.toRoleNode(getSourceChild(ct), af);
		MessageNode msg = AntlrGMessageTransfer.parseMessage(parser, getMessageChild(ct), af);
		RoleNode dest = AntlrSimpleName.toRoleNode(getDestinationChild(ct), af);
		return ((AssrtAstFactory) af).AssrtGConnect(ct, src, msg, dest, ass);
	}

	public static CommonTree getAssertionChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(ASSERTION_CHILD_INDEX);
	}

	// The following are re-defined to use new indices -- FIXME: better pattern
	public static CommonTree getMessageChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(MESSAGE_CHILD_INDEX);
	}

	public static CommonTree getSourceChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(SOURCE_CHILD_INDEX);
	}

	public static CommonTree getDestinationChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(DESTINATION_CHILD_INDEX);
	}
}

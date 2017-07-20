package org.scribble.ext.assrt.parser.scribble.ast.global;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertParser;
import org.scribble.ext.assrt.parser.scribble.AssrtScribParser;
import org.scribble.ext.assrt.sesstype.formula.AssrtBoolFormula;
import org.scribble.parser.scribble.ScribParser;
import org.scribble.parser.scribble.ScribParserUtil;
import org.scribble.parser.scribble.ast.global.AntlrGMessageTransfer;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

public class AssrtAntlrGMessageTransfer
{
	public static final int ASSERTION_CHILD_INDEX = 0;

	// "Original" indices "shifted down" -- FIXME: better pattern
	public static final int MESSAGE_CHILD_INDEX = 1;
	public static final int SOURCE_CHILD_INDEX = 2;
	public static final int DESTINATION_CHILDREN_START_INDEX = 3;

	public static GMessageTransfer parseAssrtGMessageTransfer(ScribParser parser, CommonTree ct, AssrtAstFactory af) throws ScribParserException
	{
		//AssrtAssertionNode assertion = parseAssertion(((AssrtScribParser) parser).ap, getAssertionChild(ct));   
		AssrtAssertion assertion = parseAssertion(((AssrtScribParser) parser).ap, getAssertionChild(ct), af);   

		RoleNode src = AntlrSimpleName.toRoleNode(getSourceChild(ct), af);
		MessageNode msg = AntlrGMessageTransfer.parseMessage(parser, getMessageChild(ct), af);
		List<RoleNode> dests = getDestChildren(ct).stream()
				.map(d -> AntlrSimpleName.toRoleNode(d, af)).collect(Collectors.toList());
		return ((AssrtAstFactory) af).AssrtGMessageTransfer(ct, src, msg, dests, assertion);
	}
	
	public static AssrtAssertion parseAssertion(AssrtAssertParser ap, CommonTree ct, AssrtAstFactory af)
	{
		ScribParser.checkForAntlrErrors(ct);  // Check ct root

		//return AssrtAstFactoryImpl.FACTORY.AssertionNode(ct, ct.getText());
		CommonTree tmp = (CommonTree) ct.getChild(0);  // Formula node to parse  // FIXME: factor out?
		//SmtFormula f = ap.parse(tmp);
		AssrtBoolFormula f = (AssrtBoolFormula) ap.parse(tmp);  // By AssrtAssertions.g
		return ((AssrtAstFactory) af).AssrtAssertion(ct, f);
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

	public static List<CommonTree> getDestChildren(CommonTree ct)
	{
		return ScribParserUtil.toCommonTreeList(ct.getChildren().subList(DESTINATION_CHILDREN_START_INDEX, ct.getChildCount()));
	}
}

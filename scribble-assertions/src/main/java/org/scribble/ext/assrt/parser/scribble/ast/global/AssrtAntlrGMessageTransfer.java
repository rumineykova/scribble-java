package org.scribble.ext.assrt.parser.scribble.ast.global;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.parser.scribble.AssrtAntlrToScribParser;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.parser.scribble.AntlrToScribParser;
import org.scribble.parser.scribble.AntlrToScribParserUtil;
import org.scribble.parser.scribble.ast.global.AntlrGMessageTransfer;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

public class AssrtAntlrGMessageTransfer
{
	// "Original" indices "shifted down" -- FIXME: better pattern
	public static final int MESSAGE_CHILD_INDEX = 1;
	public static final int SOURCE_CHILD_INDEX = 2;
	public static final int DESTINATION_CHILDREN_START_INDEX = 3;

	public static final int ASSERTION_CHILD_INDEX = 0;

	public static GMessageTransfer parseAssrtGMessageTransfer(AntlrToScribParser parser, CommonTree root, AssrtAstFactory af) throws ScribParserException
	{
		RoleNode src = AntlrSimpleName.toRoleNode(getSourceChild(root), af);
		MessageNode msg = AntlrGMessageTransfer.parseMessage(parser, getMessageChild(root), af);
		List<RoleNode> dests = getDestChildren(root).stream()
				.map(d -> AntlrSimpleName.toRoleNode(d, af)).collect(Collectors.toList());

		//AssrtAssertionNode assertion = parseAssertion(((AssrtScribParser) parser).ap, getAssertionChild(ct));   
		AssrtAssertion ass = parseAssertion(((AssrtAntlrToScribParser) parser).ap, getAssertionChild(root), af);   

		return ((AssrtAstFactory) af).AssrtGMessageTransfer(root, src, msg, dests, ass);
	}
	
	public static AssrtAssertion parseAssertion(AssrtAntlrToFormulaParser ap, CommonTree assTree, AssrtAstFactory af)
	{
		AntlrToScribParser.checkForAntlrErrors(assTree);  // Check ct root

		//return AssrtAstFactoryImpl.FACTORY.AssertionNode(ct, ct.getText());
		CommonTree tmp = (CommonTree) assTree.getChild(0);  // Formula node to parse  // FIXME: factor out?
		//SmtFormula f = ap.parse(tmp);
		AssrtBoolFormula f = (AssrtBoolFormula) ap.parse(tmp);  // By AssrtAssertions.g
		return af.AssrtAssertion(assTree, f);
	}

	// The following are re-defined to use new indices -- FIXME: better pattern
	public static CommonTree getMessageChild(CommonTree root)
	{
		return (CommonTree) root.getChild(AssrtAntlrGMessageTransfer.MESSAGE_CHILD_INDEX);
	}

	public static CommonTree getSourceChild(CommonTree root)
	{
		return (CommonTree) root.getChild(AssrtAntlrGMessageTransfer.SOURCE_CHILD_INDEX);
	}

	public static List<CommonTree> getDestChildren(CommonTree root)
	{
		return AntlrToScribParserUtil
				.toCommonTreeList(root.getChildren().subList(AssrtAntlrGMessageTransfer.DESTINATION_CHILDREN_START_INDEX, root.getChildCount()));
	}

	public static CommonTree getAssertionChild(CommonTree root)
	{
		return (CommonTree) root.getChild(AssrtAntlrGMessageTransfer.ASSERTION_CHILD_INDEX);
	}
}

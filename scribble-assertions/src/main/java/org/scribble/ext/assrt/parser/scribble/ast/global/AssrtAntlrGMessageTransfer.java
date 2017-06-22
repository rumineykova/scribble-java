/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.ext.assrt.parser.scribble.ast.global;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.AssrtAssertionNode;
import org.scribble.ext.assrt.ast.AssrtAstFactoryImpl;
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

	public static GMessageTransfer parseAnnotGMessageTransfer(ScribParser parser, CommonTree ct, AstFactory af) throws ScribParserException
	{
		AssrtAssertionNode assertion = parseAssertion(getAssertionChild(ct));   
		RoleNode src = AntlrSimpleName.toRoleNode(getSourceChild(ct), af);
		MessageNode msg = AntlrGMessageTransfer.parseMessage(parser, getMessageChild(ct), af);
		List<RoleNode> dests = 
			getDestChildren(ct).stream().map(d -> AntlrSimpleName.toRoleNode(d, af)).collect(Collectors.toList());
		return AssrtAstFactoryImpl.FACTORY.GMessageTransfer(ct, src, msg, dests, assertion);
	}

	public static CommonTree getAssertionChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(ASSERTION_CHILD_INDEX);
	}
	
	public static AssrtAssertionNode parseAssertion(CommonTree ct)
	{
		return AssrtAstFactoryImpl.FACTORY.AssertionNode(ct, ct.getText());
	}

	// Following need to be refined to use new indices -- FIXME: better pattern
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

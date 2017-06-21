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
package org.scribble.ext.assrt.parser.ast.global;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.AssrtAssertionNode;
import org.scribble.ext.assrt.ast.AssrtAstFactoryImpl;
import org.scribble.parser.ScribParser;
import org.scribble.parser.ast.global.AntlrGMessageTransfer;
import org.scribble.parser.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

public class AssrtAntlrGMessageTransfer
{
	public static final int ASSERTION_CHILD_INDEX = 0;

	public static GMessageTransfer parseAnnotGMessageTransfer(ScribParser parser, CommonTree ct) throws ScribParserException
	{
		AssrtAssertionNode assertion = parseAssertion(getAssertionChild(ct));   
		RoleNode src = AntlrSimpleName.toRoleNode(AntlrGMessageTransfer.getSourceChild(ct));
		MessageNode msg = AntlrGMessageTransfer.parseMessage(parser, AntlrGMessageTransfer.getMessageChild(ct));
		List<RoleNode> dests = 
			AntlrGMessageTransfer.getDestChildren(ct).stream().map((d) -> AntlrSimpleName.toRoleNode(d)).collect(Collectors.toList());
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
}

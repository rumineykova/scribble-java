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
package org.scribble.ast;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.global.AGMessageTransfer;
import org.scribble.ast.local.ALSend;
import org.scribble.ast.name.simple.RoleNode;


public interface AAstFactory extends AstFactory
{
	ALSend LSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode assertion);

	AAssertionNode AssertionNode(CommonTree source, String assertion);

	AGMessageTransfer GMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode assertion);

}

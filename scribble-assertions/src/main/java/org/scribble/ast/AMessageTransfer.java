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

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.ProtocolKind;
import org.scribble.visit.AstVisitor;

// FIXME: don't extend here, extend assertion field lower down
@Deprecated
public abstract class AMessageTransfer<K extends ProtocolKind> extends MessageTransfer<K>
{
	public AAssertionNode assertion;  // null if none specified syntactically

	protected AMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		this(source, src, msg,new LinkedList<>(dests), null); 
	}
	
	protected AMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode assertion)
	{
		super(source, src, msg, dests);
		this.assertion = assertion; 
	}

	@Override
	public AMessageTransfer<K> reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		return reconstruct(src, msg, dests, null);
	}
	
	public abstract AMessageTransfer<K> reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode assertion);

	@Override
	public AMessageTransfer<K> visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleNode src = (RoleNode) visitChild(this.src, nv);
		MessageNode msg = (MessageNode) visitChild(this.msg, nv);
		List<RoleNode> dests = visitChildListWithClassEqualityCheck(this, this.dests, nv);

		AAssertionNode ass = this.assertion;  // FIXME: visit

		return reconstruct(src, msg, dests, ass);
	}
}

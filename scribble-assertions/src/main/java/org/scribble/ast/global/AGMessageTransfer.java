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
package org.scribble.ast.global;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AAssertionNode;
import org.scribble.ast.AAstFactoryImpl;
import org.scribble.ast.Constants;
import org.scribble.ast.MessageNode;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.local.LInteractionSeq;
import org.scribble.ast.local.LNode;
import org.scribble.ast.local.LSend;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.ScribDel;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.Global;
import org.scribble.sesstype.name.Role;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

public class AGMessageTransfer extends GMessageTransfer
{
	public final AAssertionNode assertion;  // null if none specified syntactically  
			// Duplicated in ALSend/Receive -- could factour out to in Del, but need to consider immutable pattern

	public AGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode assertion)
	{
		super(source, src, msg, dests);
		this.assertion = assertion;
	}
	
	@Override
	public LNode project(Role self)
	{
		LNode proj = super.project(self);
		if (proj instanceof LInteractionSeq)
		{
			throw new RuntimeException("[scrib-assert] Self-communication not supported: " + proj);
		}
		else if (proj instanceof LSend)
		{
			LSend ls = (LSend) proj;
			proj = AAstFactoryImpl.FACTORY.LSend(ls.getSource(), ls.src, ls.msg, ls.getDestinations(), this.assertion);
		}
		return proj;
	}

	@Override
	protected AGMessageTransfer copy()
	{
		return new AGMessageTransfer(this.source, this.src, this.msg, getDestinations(), this.assertion);
	}
	
	@Override
	public AGMessageTransfer clone()
	{
		RoleNode src = this.src.clone();
		MessageNode msg = this.msg.clone();
		List<RoleNode> dests = ScribUtil.cloneList(getDestinations());
		
		// FIXME: assertion
		
		return AAstFactoryImpl.FACTORY.GMessageTransfer(this.source, src, msg, dests, this.assertion);
	}

	@Override
	public AGMessageTransfer reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		throw new RuntimeException("Shouldn't get in here: " + this);
	}

	public AGMessageTransfer reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode assertion)
	{
		ScribDel del = del();
		AGMessageTransfer gmt = new AGMessageTransfer(this.source, src, msg, dests, assertion);
		gmt = (AGMessageTransfer) gmt.del(del);
		return gmt;
	}

	@Override
	public MessageTransfer<Global> visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleNode src = (RoleNode) visitChild(this.src, nv);
		MessageNode msg = (MessageNode) visitChild(this.msg, nv);
		List<RoleNode> dests = visitChildListWithClassEqualityCheck(this, this.dests, nv);

		AAssertionNode ass = this.assertion;  // FIXME: visit

		return reconstruct(src, msg, dests, ass);
	}

	@Override
	public String toString()
	{
		return "[" + this.assertion + "]\n"
					+ this.msg + " " + Constants.FROM_KW + " " + this.src + " " + Constants.TO_KW + " "
					+ getDestinations().stream().map((dest) -> dest.toString()).collect(Collectors.joining(", ")) + ";";
	}
}

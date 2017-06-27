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
package org.scribble.ext.assrt.ast.global;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.Constants;
import org.scribble.ast.MessageNode;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.local.LInteractionSeq;
import org.scribble.ast.local.LNode;
import org.scribble.ast.local.LSend;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtAssertionNode;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.Global;
import org.scribble.sesstype.name.Role;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

public class AssrtGMessageTransfer extends GMessageTransfer
{
	public final AssrtAssertionNode ass;  // null if not specified
			// Duplicated in ALSend -- could factour out to in Del, but need to consider immutable pattern

	public AssrtGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		this(source, src, msg, dests, null);
	}

	public AssrtGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertionNode assertion)
	{
		super(source, src, msg, dests);
		this.ass = assertion;
	}
	
	@Override
	public LNode project(AstFactory af, Role self)
	{
		LNode proj = super.project(af, self);
		if (proj instanceof LInteractionSeq)  // From super, if self communication
		{
			throw new RuntimeException("[scrib-assert] Self-communication not supported: " + proj);
		}
		else if (proj instanceof LSend)
		{
			LSend ls = (LSend) proj;
			proj = ((AssrtAstFactory) af).AssrtLSend(ls.getSource(), ls.src, ls.msg, ls.getDestinations(), this.ass);
		}
		return proj;
	}

	@Override
	protected AssrtGMessageTransfer copy()
	{
		return new AssrtGMessageTransfer(this.source, this.src, this.msg, getDestinations(), this.ass);
	}
	
	@Override
	public AssrtGMessageTransfer clone(AstFactory af)
	{
		RoleNode src = this.src.clone(af);
		MessageNode msg = this.msg.clone(af);
		List<RoleNode> dests = ScribUtil.cloneList(af, getDestinations());
		AssrtAssertionNode assertion = (this.ass == null) ? null : this.ass.clone(af);
		return ((AssrtAstFactory) af).AssrtGMessageTransfer(this.source, src, msg, dests, assertion);
	}

	@Override
	public AssrtGMessageTransfer reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		throw new RuntimeException("[scrib-assert] Shouldn't get in here: " + this);
	}

	public AssrtGMessageTransfer reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertionNode assertion)
	{
		ScribDel del = del();
		AssrtGMessageTransfer gmt = new AssrtGMessageTransfer(this.source, src, msg, dests, assertion);
		gmt = (AssrtGMessageTransfer) gmt.del(del);
		return gmt;
	}

	@Override
	public MessageTransfer<Global> visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleNode src = (RoleNode) visitChild(this.src, nv);
		MessageNode msg = (MessageNode) visitChild(this.msg, nv);
		List<RoleNode> dests = visitChildListWithClassEqualityCheck(this, this.dests, nv);
		AssrtAssertionNode ass = (this.ass == null) ? null : (AssrtAssertionNode) visitChild(this.ass, nv);
		return reconstruct(src, msg, dests, ass);
	}

	@Override
	public String toString()
	{
		return "[" + this.ass + "]\n"
					+ this.msg + " " + Constants.FROM_KW + " " + this.src + " " + Constants.TO_KW + " "
					+ getDestinations().stream().map((dest) -> dest.toString()).collect(Collectors.joining(", ")) + ";";
	}
}
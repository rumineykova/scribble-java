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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AssertionNode;
import org.scribble.ast.AAstFactoryImpl;
import org.scribble.ast.Constants;
import org.scribble.ast.MessageNode;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.AMessageTransfer;
import org.scribble.ast.local.LInteractionNode;
import org.scribble.ast.local.LNode;
import org.scribble.ast.local.ALReceive;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.AScribDel;
import org.scribble.sesstype.kind.Global;
import org.scribble.sesstype.kind.RoleKind;
import org.scribble.sesstype.name.Role;
import org.scribble.util.ScribUtil;

public class AGMessageTransfer extends AMessageTransfer<Global> implements GSimpleInteractionNode
{
	public AGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		this(source, src, msg, dests, null);
	}

	public AGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssertionNode assertion)
	{
		super(source, src, msg, dests, assertion);
	}
	
	public LNode project(Role self)
	{
		Role srcrole = this.src.toName();
		List<Role> destroles = this.getDestinationRoles();
		LNode projection = null;
		if (srcrole.equals(self) || destroles.contains(self))
		{
			RoleNode src = (RoleNode) AAstFactoryImpl.FACTORY.SimpleNameNode(this.src.getSource(), RoleKind.KIND, this.src.toName().toString());  // clone?
			MessageNode msg = (MessageNode) this.msg.project();  // FIXME: need namespace prefix update?
			List<RoleNode> dests =
					this.getDestinations().stream().map((rn) ->
							(RoleNode) AAstFactoryImpl.FACTORY.SimpleNameNode(rn.getSource(), RoleKind.KIND, rn.toName().toString())).collect(Collectors.toList());
			
			if (srcrole.equals(self))
			{
				projection = AAstFactoryImpl.FACTORY.LSend(this.source, src, msg, dests, this.assertion);
			}
			if (destroles.contains(self))
			{
				if (projection == null)
				{
					projection = AAstFactoryImpl.FACTORY.LReceive(this.source, src, msg, dests);
				}
				else
				{
					ALReceive lr = AAstFactoryImpl.FACTORY.LReceive(this.source, src, msg, dests);
					List<LInteractionNode> lis = Arrays.asList(new LInteractionNode[]{(LInteractionNode) projection, lr});
					projection = AAstFactoryImpl.FACTORY.LInteractionSeq(this.source, lis);
				}
			}
		}
		return projection;
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
		return AAstFactoryImpl.FACTORY.AGMessageTransfer(this.source, src, msg, dests, this.assertion);
	}

	@Override
	public AGMessageTransfer reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests, AssertionNode assertion)
	{
		AScribDel del = del();
		AGMessageTransfer gmt = new AGMessageTransfer(this.source, src, msg, dests, assertion);
		gmt = (AGMessageTransfer) gmt.del(del);
		return gmt;
	}

	// FIXME: shouldn't be needed, but here due to Eclipse bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=436350
	@Override
	public Global getKind()
	{
		return GSimpleInteractionNode.super.getKind();
	}

	@Override
	public String toString()
	{
		return this.msg + " " + Constants.FROM_KW + " " + this.src + " " + Constants.TO_KW + " "
					+ getDestinations().stream().map((dest) -> dest.toString()).collect(Collectors.joining(", ")) + ";";
	}
}

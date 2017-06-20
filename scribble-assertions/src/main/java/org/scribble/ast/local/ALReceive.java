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
package org.scribble.ast.local;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AAssertionNode;
import org.scribble.ast.Constants;
import org.scribble.ast.MessageNode;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.ScribDel;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.Local;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

@Deprecated
public class ALReceive extends LReceive
{
	public final AAssertionNode assertion;  // null if none specified syntactically
			// Duplicated in ALSend -- could factour out to in Del, but need to consider immutable pattern

	public ALReceive(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode ass)
	{
		super(source, src, msg, dests);
		this.assertion = ass;
	}

	@Override
	protected ScribNodeBase copy()
	{
		return new ALReceive(this.source, this.src, this.msg, getDestinations(), this.assertion);
	}
	
	@Override
	public ALReceive clone()
	{
		RoleNode src = this.src.clone();
		MessageNode msg = this.msg.clone();
		List<RoleNode> dests = ScribUtil.cloneList(getDestinations());
		
		// FIXME: assertion
		
		//return AAstFactoryImpl.FACTORY.LReceive(this.source, src, msg, dests);
		return null;
	}

	@Override
	public ALReceive reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		throw new RuntimeException("Shouldn't get in here: " + this);
	}
	
	public ALReceive reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode assertion)
	{
		ScribDel del = (ScribDel) del();
		ALReceive lr = new ALReceive(this.source, src, msg, dests, assertion);  // FIXME: assertion
		lr = (ALReceive) lr.del(del);
		return lr;
	}

	@Override
	public MessageTransfer<Local> visitChildren(AstVisitor nv) throws ScribbleException
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
		return "[" + this.assertion + "]\n" + this.msg + " " + Constants.FROM_KW + " " + this.src + ";";
	}
}

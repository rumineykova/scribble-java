package org.scribble.ast.local;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AssertionNode;
import org.scribble.ast.MessageNode;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.sesstype.kind.Local;

public abstract class LMessageTransfer extends MessageTransfer<Local> implements LSimpleInteractionNode
{
	public LMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		this(source, src, msg, dests, null);
	}
	
	protected LMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssertionNode assertion)
	{
		super(source, src, msg, dests, assertion);
	}
}

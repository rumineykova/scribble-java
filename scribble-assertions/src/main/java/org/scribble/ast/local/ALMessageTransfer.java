package org.scribble.ast.local;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AMessageTransfer;
import org.scribble.ast.AAssertionNode;
import org.scribble.ast.MessageNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.sesstype.kind.Local;

@Deprecated
public abstract class ALMessageTransfer extends AMessageTransfer<Local> implements LSimpleInteractionNode
{
	public ALMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		this(source, src, msg, dests, null);
	}
	
	protected ALMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode assertion)
	{
		super(source, src, msg, dests, assertion);
	}
}

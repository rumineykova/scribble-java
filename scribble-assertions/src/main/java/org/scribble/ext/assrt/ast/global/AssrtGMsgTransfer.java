package org.scribble.ext.assrt.ast.global;

import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.Token;
import org.scribble.ast.DirectedInteraction;
import org.scribble.ast.MsgNode;
import org.scribble.ast.global.GMsgTransfer;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.core.type.kind.Global;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtActionAssertNode;
import org.scribble.ext.assrt.ast.AssrtBExprNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

public class AssrtGMsgTransfer extends GMsgTransfer
		implements AssrtActionAssertNode
{
	// CHECKME: not that "safe" as an "override" pattern (original constants still avail)
	public static final int DST_CHILD_INDEX = 2;
	public static final int ASS_CHILD_INDEX = 3;  // May be null (means "true")

	// ScribTreeAdaptor#create constructor
	public AssrtGMsgTransfer(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public AssrtGMsgTransfer(AssrtGMsgTransfer node)
	{
		super(node);
	}

	public RoleNode getDestinationChild()
	{
		return (RoleNode) getChild(DST_CHILD_INDEX);
	}

	@Override
	public List<RoleNode> getDestinationChildren()
	{
		return Arrays.asList(getDestinationChild());
	}

	// N.B. may be null
	@Override
	public AssrtBExprNode getAssertionChild()
	{
		return (AssrtBExprNode) getChild(ASS_CHILD_INDEX);
	}

	@Override
	public void addScribChildren(MsgNode msg, RoleNode src, List<RoleNode> dsts)
	{
		throw new RuntimeException(
				"[assert] Deprecated for " + getClass() + ":\n\t" + this);
	}

	// "add", not "set"
	public void addScribChildren(MsgNode msg, RoleNode src, RoleNode dst,
			AssrtBExprNode ass)
	{
		// Cf. above getters and Scribble.g children order
		addChild(msg);
		addChild(src);
		addChild(dst);
		addChild(ass);
	}

	@Override
	public AssrtGMsgTransfer dupNode()
	{
		return new AssrtGMsgTransfer(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtGMsgTransfer(this);
	}

	@Override
	public DirectedInteraction<Global> reconstruct(MsgNode msg, RoleNode src,
			List<RoleNode> dsts)
	{
		throw new RuntimeException(
				"[assrt] Deprecated for " + getClass() + ": " + this);
	}

	public AssrtGMsgTransfer reconstruct(MsgNode msg, RoleNode src,
			RoleNode dst, AssrtBExprNode ass)
	{
		AssrtGMsgTransfer dup = dupNode();
		// Same order as getter indices
		dup.addScribChildren(msg, src, dst, ass);
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public AssrtGMsgTransfer visitChildren(AstVisitor v)
			throws ScribException
	{
		MsgNode msg = (MsgNode) visitChild(getMessageNodeChild(), v);
		RoleNode src = (RoleNode) visitChild(getSourceChild(), v);
		RoleNode dst = (RoleNode) visitChild(getDestinationChild(), v);
		AssrtBExprNode tmp = getAssertionChild();
		AssrtBExprNode ass = (tmp == null) 
				? null
				: (AssrtBExprNode) visitChild(tmp, v);
		return reconstruct(msg, src, dst, ass);
	}

	@Override
	public String toString()
	{
		AssrtBExprNode bexpr = getAssertionChild();
		return super.toString() + ((bexpr == null) ? "" : " " + bexpr);
	}
}
















	/*
	public final AssrtAssertion ass;  // null if not specified -- should be the "true" formula in principle, but not syntactically
			// Duplicated in, e.g., ALSend -- could factour out to in Del, but need to consider immutable pattern

	public AssrtGMsgTransfer(CommonTree source, RoleNode src, MsgNode msg, List<RoleNode> dests)
	{
		this(source, src, msg, dests, null);
	}

	public AssrtGMsgTransfer(CommonTree source, RoleNode src, MsgNode msg, List<RoleNode> dests, AssrtAssertion ass)
	{
		super(source, src, msg, dests);
		this.ass = ass;
	}
	
	@Override
	public LNode project(AstFactory af, Role self)
	{
		LNode proj = super.project(af, self);
		if (proj instanceof LInteractionSeq)  // From super, if self communication
		{
			throw new RuntimeException("[assrt] Self-communication not supported: " + proj);
		}
		else if (proj instanceof LSend)
		{
			LSend ls = (LSend) proj;
			proj = ((AssrtAstFactory) af).AssrtLSend(ls.getSource(), ls.src, ls.msg, ls.getDestinations(), this.ass);
		}
		return proj;
	}
	*/
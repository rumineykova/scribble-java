package org.scribble.ext.assrt.ast.global;

import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.Token;
import org.scribble.ast.MsgNode;
import org.scribble.ast.global.GConnect;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtActionAssertNode;
import org.scribble.ext.assrt.ast.AssrtBExprNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

public class AssrtGConnect extends GConnect implements AssrtActionAssertNode
{
	// CHECKME: not that "safe" as an "override" pattern (original constants still avail)
	public static final int DST_CHILD_INDEX = 2;
	public static final int ASS_CHILD_INDEX = 3;  // May be null (means "true")

	// ScribTreeAdaptor#create constructor
	public AssrtGConnect(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public AssrtGConnect(AssrtGConnect node)
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
		return (AssrtBExprNode) getChild(ASS_CHILD_INDEX);  // CHECKME: OK to getChild if null?
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
	public AssrtGConnect dupNode()
	{
		return new AssrtGConnect(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtGConnect(this);
	}

	@Override
	public AssrtGConnect reconstruct(MsgNode msg, RoleNode src,
			List<RoleNode> dsts)
	{
		throw new RuntimeException(
				"[assert] Deprecated for " + getClass() + ":\n\t" + this);
	}

	public AssrtGConnect reconstruct(RoleNode src, MsgNode msg, RoleNode dst,
			AssrtBExprNode ass)
	{
		AssrtGConnect dup = dupNode();
		// Same order as getter indices
		dup.addScribChildren(msg, src, dst, ass);
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public AssrtGConnect visitChildren(AstVisitor v)
			throws ScribException
	{
		//DirectedInteraction<Global> sup = super.visitChildren(v);  // No: base reconstruct "deprecated" to runtime exception
		MsgNode msg = (MsgNode) visitChild(getMessageNodeChild(), v);
		RoleNode src = (RoleNode) visitChild(getSourceChild(), v);
		RoleNode dst = (RoleNode) visitChild(getDestinationChild(), v);
		AssrtBExprNode tmp = getAssertionChild();
		AssrtBExprNode ass = (tmp == null) 
				? null
				: (AssrtBExprNode) visitChild(tmp, v);
		return reconstruct(src, msg, dst, ass);
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
			// Duplicated from AssrtGMessageTransfer

	public AssrtGConnect(CommonTree source, RoleNode src, MsgNode msg, RoleNode dest)
	{
		this(source, src, msg, dest, null);
	}

	public AssrtGConnect(CommonTree source, RoleNode src, MsgNode msg, RoleNode dest, AssrtAssertion ass)
	{
		super(source, src, msg, dest);
		this.ass = ass;
	}

	public LNode project(AstFactory af, Role self)
	{
		LNode proj = super.project(af, self);
		if (proj instanceof LRequest)
		{
			LRequest lc = (LRequest) proj;
			proj = ((AssrtAstFactory) af).AssrtLConnect(lc.getSource(), lc.src, lc.msg, lc.dest, this.ass);
		}
		// FIXME: 
		return proj;
	}
*/
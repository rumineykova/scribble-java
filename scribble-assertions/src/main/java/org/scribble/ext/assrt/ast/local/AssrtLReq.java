package org.scribble.ext.assrt.ast.local;

import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.Token;
import org.scribble.ast.MsgNode;
import org.scribble.ast.local.LReq;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtActionAssertNode;
import org.scribble.ext.assrt.ast.AssrtBExprNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

public class AssrtLReq extends LReq implements AssrtActionAssertNode
{
	public static final int DST_CHILD_INDEX = 2;
	public static final int ASS_CHILD_INDEX = 3;  // May be null (means "true")

	// ScribTreeAdaptor#create constructor
	public AssrtLReq(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public AssrtLReq(AssrtLReq node)
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
	public AssrtLReq dupNode()
	{
		return new AssrtLReq(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtLReq(this);
	}

	@Override
	public AssrtLReq reconstruct(MsgNode msg, RoleNode src,
			List<RoleNode> dsts)
	{
		throw new RuntimeException(
				"[assert] Deprecated for " + getClass() + ":\n\t" + this);
	}

	public AssrtLReq reconstruct(RoleNode src, MsgNode msg, RoleNode dst,
			AssrtBExprNode ass)
	{
		AssrtLReq dup = dupNode();
		// Same order as getter indices
		dup.addScribChildren(msg, src, dst, ass);
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public AssrtLReq visitChildren(AstVisitor v)
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
		return super.toString() + " " + getAssertionChild();
	}
}










/*
	public final AssrtAssertion ass;  // null if none specified syntactically  
			// Duplicated from AssrtLSend

	public AssrtLReq(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest)
	{
		this(source, src, msg, dest, null);
	}

	public AssrtLReq(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion ass)
	{
		super(source, src, msg, dest);
		this.ass = ass;
	}
*/
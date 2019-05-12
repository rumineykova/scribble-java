package org.scribble.ext.assrt.ast.global;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.MsgNode;
import org.scribble.ast.global.GMsgTransfer;
import org.scribble.ast.local.LInteractionSeq;
import org.scribble.ast.local.LSend;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.core.lang.local.LNode;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.Role;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtActionAssertNode;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.main.ScribbleException;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

public class AssrtGMsgTransfer extends GMsgTransfer implements AssrtActionAssertNode
{
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

	@Override
	protected AssrtGMsgTransfer copy()
	{
		return new AssrtGMsgTransfer(this.source, this.src, this.msg, getDestinations(), this.ass);  // null ass fine
	}
	
	@Override
	public AssrtGMsgTransfer clone(AstFactory af)
	{
		RoleNode src = this.src.clone(af);
		MsgNode msg = this.msg.clone(af);
		List<RoleNode> dests = ScribUtil.cloneList(af, getDestinations());
		AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);
		return ((AssrtAstFactory) af).AssrtGMessageTransfer(this.source, src, msg, dests, ass);
	}

	@Override
	public AssrtGMsgTransfer reconstruct(RoleNode src, MsgNode msg, List<RoleNode> dests)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtGMsgTransfer reconstruct(RoleNode src, MsgNode msg, List<RoleNode> dests, AssrtAssertion ass)
	{
		ScribDel del = del();
		AssrtGMsgTransfer gmt = new AssrtGMsgTransfer(this.source, src, msg, dests, ass);
		gmt = (AssrtGMsgTransfer) gmt.del(del);
		return gmt;
	}

	@Override
	public MessageTransfer<Global> visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleNode src = (RoleNode) visitChild(this.src, nv);
		MsgNode msg = (MsgNode) visitChild(this.msg, nv);
		List<RoleNode> dests = visitChildListWithClassEqualityCheck(this, this.dests, nv);
		AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);
		return reconstruct(src, msg, dests, ass);
	}

	@Override
	public AssrtAssertion getAssertion()
	{
		return this.ass;
	}

	@Override
	public String toString()
	{
		return
				/*  this.msg + " " + Constants.FROM_KW + " " + this.src + " " + Constants.TO_KW + " "
				+ getDestinations().stream().map(dest -> dest.toString()).collect(Collectors.joining(", "))*/
				super.toString() + " " + this.ass;
	}
}

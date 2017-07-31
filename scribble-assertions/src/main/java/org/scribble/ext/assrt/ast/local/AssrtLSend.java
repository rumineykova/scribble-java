package org.scribble.ext.assrt.ast.local;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.MessageNode;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.local.LSend;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Local;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

public class AssrtLSend extends LSend
{
	public final AssrtAssertion ass;  // null if none specified syntactically  
			// Duplicated in AGMessageTransfer -- could factour out to in Del, but need to consider immutable pattern
			// (But no ALReceive -- receive has no assertions)

	public AssrtLSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		this(source, src, msg, dests, null);
	}

	public AssrtLSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion ass)
	{
		super(source, src, msg, dests);
		this.ass = ass;
	}

	@Override
	protected ScribNodeBase copy()
	{
		return new AssrtLSend(this.source, this.src, this.msg, getDestinations(), this.ass);  // null ass fine
	}
	
	@Override
	public AssrtLSend clone(AstFactory af)
	{
		RoleNode src = this.src.clone(af);
		MessageNode msg = this.msg.clone(af);
		List<RoleNode> dests = ScribUtil.cloneList(af, getDestinations());
		AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);
		return ((AssrtAstFactory) af).AssrtLSend(this.source, src, msg, dests, ass);
	}

	@Override
	public AssrtLSend reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}
	
	public AssrtLSend reconstruct(RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion ass)
	{
		ScribDel del = del();
		AssrtLSend ls = new AssrtLSend(this.source, src, msg, dests, ass);  // FIXME: assertion
		ls = (AssrtLSend) ls.del(del);
		return ls;
	}

	@Override
	public MessageTransfer<Local> visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleNode src = (RoleNode) visitChild(this.src, nv);
		MessageNode msg = (MessageNode) visitChild(this.msg, nv);
		List<RoleNode> dests = visitChildListWithClassEqualityCheck(this, this.dests, nv);
		AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);
		return reconstruct(src, msg, dests, ass);
	}

	@Override
	public String toString()
	{
		return 
				/*  this.msg + " " + Constants.TO_KW + " "
				+ getDestinations().stream().map(dest -> dest.toString()).collect(Collectors.joining(", "))*/
				super.toString() + " " + this.ass;
	}
}

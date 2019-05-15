package org.scribble.ext.assrt.ast.local;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ConnectionAction;
import org.scribble.ast.MessageNode;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.local.LRequest;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtActionAssertNode;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Local;
import org.scribble.visit.AstVisitor;

public class AssrtLReq extends LReq implements AssrtActionAssertNode
{
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

	@Override
	protected ScribNodeBase copy()
	{
		return new AssrtLReq(this.source, this.src, this.msg, this.dest, this.ass);  // null ass fine
	}
	
	@Override
	public AssrtLReq clone(AstFactory af)
	{
		RoleNode src = this.src.clone(af);
		MessageNode msg = this.msg.clone(af);
		RoleNode dest = this.dest.clone(af);
		AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);
		return ((AssrtAstFactory) af).AssrtLConnect(this.source, src, msg, dest, ass);
	}

	@Override
	public AssrtLReq reconstruct(RoleNode src, MessageNode msg, RoleNode dest)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}
	
	public AssrtLReq reconstruct(RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion ass)
	{
		ScribDel del = del();
		AssrtLReq ls = new AssrtLReq(this.source, src, msg, dest, ass);  // FIXME: assertion
		ls = (AssrtLReq) ls.del(del);
		return ls;
	}

	@Override
	public ConnectionAction<Local> visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleNode src = (RoleNode) visitChild(this.src, nv);
		MessageNode msg = (MessageNode) visitChild(this.msg, nv);
		RoleNode dest = (RoleNode) visitChild(this.dest, nv);
		AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);
		return reconstruct(src, msg, dest, ass);
	}

	@Override
	public AssrtAssertion getAssertionChild()
	{
		return this.ass;
	}

	@Override
	public String toString()
	{
		return super.toString() + " @" + this.ass + ";";
	}
}

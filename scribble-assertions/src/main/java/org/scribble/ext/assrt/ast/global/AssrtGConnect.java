package org.scribble.ext.assrt.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ConnectionAction;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GConnect;
import org.scribble.ast.local.LConnect;
import org.scribble.ast.local.LNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Global;
import org.scribble.type.name.Role;
import org.scribble.visit.AstVisitor;

public class AssrtGConnect extends GConnect
{
	public final AssrtAssertion ass;  // null if not specified -- should be the "true" formula in principle, but not syntactically
			// Duplicated from AssrtGMessageTransfer

	public AssrtGConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest)
	{
		this(source, src, msg, dest, null);
	}

	public AssrtGConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion ass)
	{
		super(source, src, msg, dest);
		this.ass = ass;
	}

	public LNode project(AstFactory af, Role self)
	{
		LNode proj = super.project(af, self);
		if (proj instanceof LConnect)
		{
			LConnect lc = (LConnect) proj;
			proj = ((AssrtAstFactory) af).AssrtLConnect(lc.getSource(), lc.src, lc.msg, lc.dest, this.ass);
		}
		// FIXME: 
		return proj;
	}

	@Override
	protected AssrtGConnect copy()
	{
		return new AssrtGConnect(this.source, this.src, this.msg, this.dest, this.ass);  // null ass fine
	}
	
	@Override
	public AssrtGConnect clone(AstFactory af)
	{
		RoleNode src = this.src.clone(af);
		MessageNode msg = this.msg.clone(af);
		RoleNode dest = this.dest.clone(af);
		AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);
		return ((AssrtAstFactory) af).AssrtGConnect(this.source, src, msg, dest, ass);
	}

	@Override
	public AssrtGConnect reconstruct(RoleNode src, MessageNode msg, RoleNode dest)
	{
		throw new RuntimeException("[scrib-assert] Shouldn't get in here: " + this);
	}

	public AssrtGConnect reconstruct(RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion ass)
	{
		ScribDel del = del();
		AssrtGConnect gc = new AssrtGConnect(this.source, src, msg, dest, ass);
		gc = (AssrtGConnect) gc.del(del);
		return gc;
	}

	@Override
	public ConnectionAction<Global> visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleNode src = (RoleNode) visitChild(this.src, nv);
		MessageNode msg = (MessageNode) visitChild(this.msg, nv);
		RoleNode dest = (RoleNode) visitChild(this.dest, nv);
		AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);
		return reconstruct(src, msg, dest, ass);
	}

	@Override
	public String toString()
	{
		return super.toString() + " " + this.ass;
	}
}

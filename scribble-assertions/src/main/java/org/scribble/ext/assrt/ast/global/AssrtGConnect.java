package org.scribble.ext.assrt.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ConnectionAction;
import org.scribble.ast.MsgNode;
import org.scribble.ast.global.GConnect;
import org.scribble.ast.local.LRequest;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.core.lang.local.LNode;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.Role;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtActionAssertNode;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

public class AssrtGConnect extends GConnect implements AssrtActionAssertNode
{
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

	@Override
	protected AssrtGConnect copy()
	{
		return new AssrtGConnect(this.source, this.src, this.msg, this.dest, this.ass);  // null ass fine
	}
	
	@Override
	public AssrtGConnect clone(AstFactory af)
	{
		RoleNode src = this.src.clone(af);
		MsgNode msg = this.msg.clone(af);
		RoleNode dest = this.dest.clone(af);
		AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);
		return ((AssrtAstFactory) af).AssrtGConnect(this.source, src, msg, dest, ass);
	}

	@Override
	public AssrtGConnect reconstruct(RoleNode src, MsgNode msg, RoleNode dest)
	{
		throw new RuntimeException("[assert] Shouldn't get in here: " + this);
	}

	public AssrtGConnect reconstruct(RoleNode src, MsgNode msg, RoleNode dest, AssrtAssertion ass)
	{
		ScribDel del = del();
		AssrtGConnect gc = new AssrtGConnect(this.source, src, msg, dest, ass);
		gc = (AssrtGConnect) gc.del(del);
		return gc;
	}

	@Override
	public ConnectionAction<Global> visitChildren(AstVisitor nv) throws ScribException
	{
		RoleNode src = (RoleNode) visitChild(this.src, nv);
		MsgNode msg = (MsgNode) visitChild(this.msg, nv);
		RoleNode dest = (RoleNode) visitChild(this.dest, nv);
		AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);
		return reconstruct(src, msg, dest, ass);
	}

	@Override
	public AssrtAssertion getAssertion()
	{
		return this.ass;
	}

	@Override
	public String toString()
	{
		return super.toString() + " " + this.ass;
	}
}

package org.scribble.ext.assrt.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.global.GDo;
import org.scribble.ast.local.LDo;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.qualified.ProtocolNameNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithAnnotation;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.local.AssrtLDo;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Global;
import org.scribble.type.name.Role;
import org.scribble.visit.AstVisitor;

public class AssrtGDo extends GDo
{
	public final AssrtArithAnnotation annot;
	
	public AssrtGDo(CommonTree source, RoleArgList roles, NonRoleArgList args, GProtocolNameNode proto)
	{
		this(source, roles, args, proto, null);
	}

	public AssrtGDo(CommonTree source, RoleArgList roles, NonRoleArgList args, GProtocolNameNode proto, AssrtArithAnnotation annot)
	{
		super(source, roles, args, proto);
		this.annot = annot;
	}

	public LDo project(AstFactory af, Role self, LProtocolNameNode fullname)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public LDo project(AstFactory af, Role self, LProtocolNameNode fullname, AssrtArithAnnotation annot)
	{
		RoleArgList roleinstans = this.roles.project(af, self);
		NonRoleArgList arginstans = this.args.project(af, self);
		AssrtLDo ld = ((AssrtAstFactory) af).AssrtLDo(this.source, roleinstans, arginstans, fullname, annot);
		return ld;
	}

	@Override
	protected ScribNodeBase copy()
	{
		return new AssrtGDo(this.source, this.roles, this.args, getProtocolNameNode(), this.annot);
	}
	
	@Override
	public AssrtGDo clone(AstFactory af)
	{
		RoleArgList roles = this.roles.clone(af);
		NonRoleArgList args = this.args.clone(af);
		GProtocolNameNode proto = this.getProtocolNameNode().clone(af);
		AssrtArithAnnotation annot = (this.annot == null) ? null : this.annot.clone(af);
		return ((AssrtAstFactory) af).AssrtGDo(this.source, roles, args, proto, annot);
	}

	@Override
	public AssrtGDo reconstruct(RoleArgList roles, NonRoleArgList args, ProtocolNameNode<Global> proto)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtGDo reconstruct(RoleArgList roles, NonRoleArgList args, ProtocolNameNode<Global> proto, AssrtArithAnnotation annot)
	{
		ScribDel del = del();
		AssrtGDo gd = new AssrtGDo(this.source, roles, args, (GProtocolNameNode) proto, annot);
		gd = (AssrtGDo) gd.del(del);
		return gd;
	}

	@Override
	public AssrtGDo visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleArgList ril = (RoleArgList) visitChild(this.roles, nv);
		NonRoleArgList al = (NonRoleArgList) visitChild(this.args, nv);
		GProtocolNameNode proto = visitChildWithClassEqualityCheck(this, getProtocolNameNode(), nv);
		AssrtArithAnnotation annot = (this.annot == null) ? null : (AssrtArithAnnotation) visitChild(this.annot, nv);  // FIXME: visitChildWithClassEqualityCheck
		return reconstruct(ril, al, proto, annot);
	}
}

package org.scribble.ext.assrt.ast.local;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.local.LDo;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.qualified.ProtocolNameNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithAnnotation;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Local;
import org.scribble.visit.AstVisitor;

public class AssrtLDo extends LDo
{
	public final AssrtArithAnnotation annot;

	public AssrtLDo(CommonTree source, RoleArgList roleinstans, NonRoleArgList arginstans, LProtocolNameNode proto)
	{
		this(source, roleinstans, arginstans, proto, null);
	}

	public AssrtLDo(CommonTree source, RoleArgList roleinstans, NonRoleArgList arginstans, LProtocolNameNode proto, AssrtArithAnnotation annot)
	{
		super(source, roleinstans, arginstans, proto);
		this.annot = annot;
	}

	@Override
	protected AssrtLDo copy()
	{
		return new AssrtLDo(this.source, this.roles, this.args, getProtocolNameNode(), this.annot);
	}
	
	@Override
	public AssrtLDo clone(AstFactory af)
	{
		RoleArgList roles = this.roles.clone(af);
		NonRoleArgList args = this.args.clone(af);
		LProtocolNameNode proto = this.getProtocolNameNode().clone(af);
		AssrtArithAnnotation annot = (this.annot == null) ? null : this.annot.clone(af);
		return ((AssrtAstFactory) af).AssrtLDo(this.source, roles, args, proto, annot);
	}
	
	@Override
	public AssrtLDo reconstruct(RoleArgList roles, NonRoleArgList args, ProtocolNameNode<Local> proto)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLDo reconstruct(RoleArgList roles, NonRoleArgList args, ProtocolNameNode<Local> proto, AssrtArithAnnotation annot)
	{
		ScribDel del = del();
		AssrtLDo ld = new AssrtLDo(this.source, roles, args, (LProtocolNameNode) proto, annot);
		ld = (AssrtLDo) ld.del(del);
		return ld;
	}

	@Override
	public AssrtLDo visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleArgList ril = (RoleArgList) visitChild(this.roles, nv);
		NonRoleArgList al = (NonRoleArgList) visitChild(this.args, nv);
		LProtocolNameNode proto = visitChildWithClassEqualityCheck(this, getProtocolNameNode(), nv);
		AssrtArithAnnotation annot = (this.annot == null) ? null : (AssrtArithAnnotation) visitChild(this.annot, nv);  // FIXME: visitChildWithClassEqualityCheck
		return reconstruct(ril, al, proto, annot);
	}
}

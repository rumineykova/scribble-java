package org.scribble.ext.assrt.ast.global;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.AssrtStateVarArgAnnotNode;
import org.scribble.ext.assrt.ast.local.AssrtLDo;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Global;
import org.scribble.type.name.Role;
import org.scribble.visit.AstVisitor;

public class AssrtGDo extends GDo implements AssrtStateVarArgAnnotNode
{
	//public final AssrtArithExpr annot;
	public final List<AssrtArithExpr> annotexprs;
	
	public AssrtGDo(CommonTree source, RoleArgList roles, NonRoleArgList args, GProtocolNameNode proto)
	{
		this(source, roles, args, proto, //null);
				Collections.emptyList());
	}

	public AssrtGDo(CommonTree source, RoleArgList roles, NonRoleArgList args, GProtocolNameNode proto, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		super(source, roles, args, proto);
		//this.annot = annot;
		this.annotexprs = Collections.unmodifiableList(annotexprs);
	}

	public LDo project(AstFactory af, Role self, LProtocolNameNode fullname)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public LDo project(AstFactory af, Role self, LProtocolNameNode fullname, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		RoleArgList roleinstans = this.roles.project(af, self);
		NonRoleArgList arginstans = this.args.project(af, self);
		AssrtLDo ld = ((AssrtAstFactory) af).AssrtLDo(this.source, roleinstans, arginstans, fullname, //annot);
				annotexprs);
		return ld;
	}

	@Override
	protected ScribNodeBase copy()
	{
		return new AssrtGDo(this.source, this.roles, this.args, getProtocolNameNode(), //this.annot);
				this.annotexprs);
	}
	
	@Override
	public AssrtGDo clone(AstFactory af)
	{
		RoleArgList roles = this.roles.clone(af);
		NonRoleArgList args = this.args.clone(af);
		GProtocolNameNode proto = this.getProtocolNameNode().clone(af);
		//AssrtArithExpr annot = (this.annot == null) ? null : this.annot.clone(af);

		List<AssrtArithExpr> annotexprs = this.annotexprs.stream().map(e -> e.clone(af)).collect(Collectors.toList());

		return ((AssrtAstFactory) af).AssrtGDo(this.source, roles, args, proto, //annot);
				annotexprs);
	}

	@Override
	public AssrtGDo reconstruct(RoleArgList roles, NonRoleArgList args, ProtocolNameNode<Global> proto)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtGDo reconstruct(RoleArgList roles, NonRoleArgList args, ProtocolNameNode<Global> proto, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		ScribDel del = del();
		AssrtGDo gd = new AssrtGDo(this.source, roles, args, (GProtocolNameNode) proto, //annot);
				annotexprs);
		gd = (AssrtGDo) gd.del(del);
		return gd;
	}

	@Override
	public AssrtGDo visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleArgList ril = (RoleArgList) visitChild(this.roles, nv);
		NonRoleArgList al = (NonRoleArgList) visitChild(this.args, nv);
		GProtocolNameNode proto = visitChildWithClassEqualityCheck(this, getProtocolNameNode(), nv);
		//AssrtArithExpr annot = (this.annot == null) ? null : (AssrtArithExpr) visitChild(this.annot, nv);  // FIXME: visitChildWithClassEqualityCheck

		List<AssrtArithExpr> annotexprs = visitChildListWithClassEqualityCheck(this, this.annotexprs, nv);

		return reconstruct(ril, al, proto, //annot);
				annotexprs);
	}

	@Override
	public List<AssrtArithExpr> getAnnotExprs()
	{
		return this.annotexprs;
	}

	@Override
	public String toString()
	{
		return super.toString() + annotToString();
	}
}

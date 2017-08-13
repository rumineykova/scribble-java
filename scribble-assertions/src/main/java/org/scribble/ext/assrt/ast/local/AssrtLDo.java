package org.scribble.ext.assrt.ast.local;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.local.LDo;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.qualified.ProtocolNameNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.AssrtStateVarArgAnnotNode;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Local;
import org.scribble.visit.AstVisitor;

public class AssrtLDo extends LDo implements AssrtStateVarArgAnnotNode
{
	//public final AssrtArithExpr annot;
	public final List<AssrtArithExpr> annotexprs;

	public AssrtLDo(CommonTree source, RoleArgList roleinstans, NonRoleArgList arginstans, LProtocolNameNode proto)
	{
		this(source, roleinstans, arginstans, proto, //null);
				Collections.emptyList());
	}

	public AssrtLDo(CommonTree source, RoleArgList roleinstans, NonRoleArgList arginstans, LProtocolNameNode proto, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		super(source, roleinstans, arginstans, proto);
		//this.annot = annot;
		this.annotexprs = Collections.unmodifiableList(annotexprs);
	}

	@Override
	protected AssrtLDo copy()
	{
		return new AssrtLDo(this.source, this.roles, this.args, getProtocolNameNode(), //this.annot);
				this.annotexprs);
	}
	
	@Override
	public AssrtLDo clone(AstFactory af)
	{
		RoleArgList roles = this.roles.clone(af);
		NonRoleArgList args = this.args.clone(af);
		LProtocolNameNode proto = this.getProtocolNameNode().clone(af);
		//AssrtArithExpr annot = (this.annot == null) ? null : this.annot.clone(af);

		List<AssrtArithExpr> annotexprs = this.annotexprs.stream().map(e -> e.clone(af)).collect(Collectors.toList());

		return ((AssrtAstFactory) af).AssrtLDo(this.source, roles, args, proto, //annot);
				annotexprs);
	}
	
	@Override
	public AssrtLDo reconstruct(RoleArgList roles, NonRoleArgList args, ProtocolNameNode<Local> proto)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLDo reconstruct(RoleArgList roles, NonRoleArgList args, ProtocolNameNode<Local> proto, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		ScribDel del = del();
		AssrtLDo ld = new AssrtLDo(this.source, roles, args, (LProtocolNameNode) proto, //annot);
				annotexprs);
		ld = (AssrtLDo) ld.del(del);
		return ld;
	}

	@Override
	public AssrtLDo visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleArgList ril = (RoleArgList) visitChild(this.roles, nv);
		NonRoleArgList al = (NonRoleArgList) visitChild(this.args, nv);
		LProtocolNameNode proto = visitChildWithClassEqualityCheck(this, getProtocolNameNode(), nv);

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

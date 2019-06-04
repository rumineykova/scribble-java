package org.scribble.ext.assrt.ast.global;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.Token;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GDo;
import org.scribble.ast.name.qualified.GProtoNameNode;
import org.scribble.ast.name.qualified.ProtoNameNode;
import org.scribble.core.type.kind.Global;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtAExprNode;
import org.scribble.ext.assrt.ast.AssrtStateVarArgNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

// Cf. AssrtGContinue
public class AssrtGDo extends GDo implements AssrtStateVarArgNode
{
	public static final int STATEVAREXPR_CHILDREN_START_INDEX = 3;

	// ScribTreeAdaptor#create constructor
	public AssrtGDo(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public AssrtGDo(AssrtGDo node)
	{
		super(node);
	}

	@Override
	public List<AssrtAExprNode> getAnnotExprChildren()
	{
		List<? extends ScribNode> cs = getChildren();
		return cs.subList(STATEVAREXPR_CHILDREN_START_INDEX, cs.size()).stream()
				.map(x -> (AssrtAExprNode) x).collect(Collectors.toList());
	}

	// "add", not "set"
	public void addScribChildren(ProtoNameNode<Global> proto, NonRoleArgList as,
			RoleArgList rs, List<AssrtAExprNode> sexprs)
	{
		// Cf. above getters and Scribble.g children order
		super.addScribChildren(proto, as, rs);
		addChild(proto);  // Order re. getter indices
		addChild(as);
		addChild(rs);
		addChildren(sexprs);
	}
	
	@Override
	public AssrtGDo dupNode()
	{
		return new AssrtGDo(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtGDo(this);
	}

	public AssrtGDo reconstruct(ProtoNameNode<Global> proto, RoleArgList rs,
			NonRoleArgList as, List<AssrtAExprNode> sexprs)
	{
		AssrtGDo dup = dupNode();
		dup.addScribChildren(proto, as, rs, sexprs);
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public AssrtGDo visitChildren(AstVisitor v) throws ScribException
	{
		GProtoNameNode proto = visitChildWithClassEqualityCheck(this,
				getProtoNameChild(), v);
		RoleArgList rs = (RoleArgList) visitChild(getRoleListChild(), v);
		NonRoleArgList as = (NonRoleArgList) visitChild(getNonRoleListChild(), v);
		List<AssrtAExprNode> sexprs = visitChildListWithClassEqualityCheck(this,
				getAnnotExprChildren(), v);  // Supports empty list
		return reconstruct(proto, rs, as, sexprs);
	}

	@Override
	public String toString()
	{
		return super.toString() + annotToString();
	}
}














/*
	public final List<AssrtArithExpr> exprs;
	
	public AssrtGDo(CommonTree source, RoleArgList roles, NonRoleArgList args,
			GProtoNameNode proto)
	{
		this(source, roles, args, proto, //null);
				Collections.emptyList());
	}

	public AssrtGDo(CommonTree source, RoleArgList roles, NonRoleArgList args,
			GProtoNameNode proto, List<AssrtArithExpr> exprs)
	{
		super(source, roles, args, proto);
		//this.annot = annot;
		this.exprs = Collections.unmodifiableList(exprs);
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
*/
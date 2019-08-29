package org.scribble.ext.assrt.ast.local;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.Token;
import org.scribble.ast.Do;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LDo;
import org.scribble.ast.name.qualified.LProtoNameNode;
import org.scribble.ast.name.qualified.ProtoNameNode;
import org.scribble.core.type.kind.Local;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtAExprNode;
import org.scribble.ext.assrt.ast.AssrtStateVarArgNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

public class AssrtLDo extends LDo implements AssrtStateVarArgNode
{
	public static final int STATEVAREXPR_CHILDREN_START_INDEX = 3;

	// ScribTreeAdaptor#create constructor
	public AssrtLDo(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public AssrtLDo(AssrtLDo node)
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
	public void addScribChildren(ProtoNameNode<Local> proto, NonRoleArgList as,
			RoleArgList rs, List<AssrtAExprNode> sexprs)
	{
		// Cf. above getters and Scribble.g children order
		super.addScribChildren(proto, as, rs);
		addChildren(sexprs);
	}
	
	@Override
	public AssrtLDo dupNode()
	{
		return new AssrtLDo(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtLDo(this);
	}

	public AssrtLDo reconstruct(ProtoNameNode<Local> proto, RoleArgList rs,
			NonRoleArgList as, List<AssrtAExprNode> sexprs)
	{
		AssrtLDo dup = dupNode();
		dup.addScribChildren(proto, as, rs, sexprs);
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public AssrtLDo visitChildren(AstVisitor v) throws ScribException
	{
		LProtoNameNode proto = visitChildWithClassEqualityCheck(this,
				getProtoNameChild(), v);
		RoleArgList rs = (RoleArgList) visitChild(getRoleListChild(), v);
		NonRoleArgList as = (NonRoleArgList) visitChild(getNonRoleListChild(), v);
		List<AssrtAExprNode> sexprs = visitChildListWithClassEqualityCheck(this,
				getAnnotExprChildren(), v);
		return reconstruct(proto, rs, as, sexprs);
	}

	@Override
	public String toString()
	{
		return super.toString() + annotToString();
	}
}










/*
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
//*/
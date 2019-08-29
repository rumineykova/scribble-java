package org.scribble.ext.assrt.ast.local;

import org.antlr.runtime.Token;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.ProtoHeader;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.local.LProtoHeader;
import org.scribble.ast.name.qualified.ProtoNameNode;
import org.scribble.core.type.kind.Local;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtBExprNode;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclList;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

// Based on AssrtGProtocolHeader
public class AssrtLProtoHeader extends LProtoHeader
		implements AssrtStateVarDeclNode
{
	//public static final int ROLEDECLLIST_CHILD = 2;
	public static final int ASSRT_STATEVARDECLLIST_CHILD_INDEX = 3;  // null if no @-annot; o/w may be empty but not null (cf. ParamDeclList child)
	public static final int ASSRT_ASSERTION_CHILD_INDEX = 4;  // null if no @-annot; o/w may still be null

	// ScribTreeAdaptor#create constructor
	public AssrtLProtoHeader(Token t)
	{
		super(t);
	}
	
	// Tree#dupNode constructor
	protected AssrtLProtoHeader(AssrtLProtoHeader node)
	{
		super(node);
	}

	@Override
	public AssrtStateVarDeclList getStateVarDeclListChild()
	{
		return (AssrtStateVarDeclList) getChild(ASSRT_STATEVARDECLLIST_CHILD_INDEX);
	}

	// N.B. null if not specified -- currently duplicated from AssrtGMessageTransfer
	@Override
	public AssrtBExprNode getAnnotAssertChild()
	{
		return (AssrtBExprNode) getChild(ASSRT_STATEVARDECLLIST_CHILD_INDEX);
	}

	// "add", not "set"
	public void addScribChildren(ProtoNameNode<Local> name,
			NonRoleParamDeclList ps, RoleDeclList rs, AssrtStateVarDeclList svars,
			AssrtBExprNode ass)
	{
		// Cf. above getters and Scribble.g children order
		super.addScribChildren(name, ps, rs);
		addChild(svars);
		addChild(ass);
	}
	
	@Override
	public AssrtLProtoHeader dupNode()
	{
		return new AssrtLProtoHeader(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtLProtoHeader(this);
	}

	public AssrtLProtoHeader reconstruct(ProtoNameNode<Local> name,
			NonRoleParamDeclList ps, RoleDeclList rs,
			AssrtStateVarDeclList svars, AssrtBExprNode ass)
	{
		AssrtLProtoHeader dup = dupNode();
		dup.addScribChildren(name, ps, rs, svars, ass);
		dup.setDel(del());  // No copy
		return dup;
	}
	
	@Override
	public LProtoHeader visitChildren(AstVisitor v) throws ScribException
	{
		ProtoHeader<Local> sup = super.visitChildren(v);
		AssrtStateVarDeclList svars = getStateVarDeclListChild();
		if (svars != null)  // CHECKME: now never null? (or shouldn't be?)
		{
			svars = (AssrtStateVarDeclList) visitChild(svars, v);
		}
		AssrtBExprNode ass = getAnnotAssertChild();
		if (ass != null) 
		{
			ass = (AssrtBExprNode) visitChild(ass, v);
		}
		return reconstruct(sup.getNameNodeChild(), sup.getParamDeclListChild(),
				sup.getRoleDeclListChild(), svars, ass);//, sexprs);
	}
	
	@Override
	public String toString()
	{
		return super.toString() //+ " " + this.ass;
				+ annotToString();
	}
}

















/*
	public final List<AssrtIntVarNameNode> annotvars;
	public final List<AssrtArithExpr> annotexprs;
	public final AssrtAssertion ass;  // null if not specified -- currently duplicated from AssrtGMessageTransfer

	public AssrtLProtoHeader(CommonTree source, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls)
	{
		this(source, name, roledecls, paramdecls, //null);
				Collections.emptyList(), Collections.emptyList(),
				null);
	}

	public AssrtLProtoHeader(CommonTree source, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		super(source, name, roledecls, paramdecls);
		this.annotvars = Collections.unmodifiableList(annotvars);
		this.annotexprs = Collections.unmodifiableList(annotexprs);
		this.ass = ass;
	}
	
	// CHECKME: define restrictions directly in ANTLR grammar, and make a separate AST class for protocol header var init-decl annotations
	// Pre: ass != null
	//public AssrtBinCompFormula getAnnotDataTypeVarInitDecl()  // Cf. AssrtAnnotDataTypeElem (no "initializer")
	public Map<AssrtDataTypeVar, AssrtArithFormula> getAnnotDataTypeVarDecls()  // Cf. AssrtAnnotDataTypeElem (no "initializer")
	{
		//return (this.ass == null) ? null : (AssrtBinCompFormula) this.ass.getFormula();
		//return (AssrtBinCompFormula) this.ass.getFormula();
		Iterator<AssrtArithExprNode> exprs = getAnnotExprChildren().iterator();
		return getAnnotVarChildren().stream().collect(
				Collectors.toMap(v -> v.toName(), v -> exprs.next().getFormula()));
	}
	
	@Override
	public List<AssrtIntVarNameNode> getAnnotVarChildren()
	{
		List<? extends ScribNode> cs = getChildren();
		return cs.subList(ASSRT_ASSERTION_CHILDREN_START_INDEX, cs.size()).stream()  // TODO: refactor, cf. Module::getMemberChildren
				.filter(x -> x instanceof AssrtIntVarNameNode)
				.map(x -> (AssrtIntVarNameNode) x).collect(Collectors.toList());
	}

	@Override
	public List<AssrtAExprNode> getAnnotExprChildren()
	{
		List<? extends ScribNode> cs = getChildren();
		return cs.subList(ASSRT_ASSERTION_CHILDREN_START_INDEX, cs.size()).stream()  // TODO: refactor, cf. Module::getMemberChildren
				.filter(x -> x instanceof AssrtAExprNode)
				.map(x -> (AssrtAExprNode) x).collect(Collectors.toList());
	}

	// Because svars never null -- no: null better for super addScribChildren/reconstruct pattern
	@Override
	public void addScribChildren(ProtoNameNode<Local> name,
			NonRoleParamDeclList ps, RoleDeclList rs)
	{
		throw new RuntimeException("Deprecated for " + getClass() + ":\n\t" + this);
	}

	@Override
	public AssrtLProtoHeader reconstruct(ProtoNameNode<Local> name,
			NonRoleParamDeclList ps, RoleDeclList rs)
	{
		throw new RuntimeException(
				"[assrt] Deprecated for " + getClass() + ":\n\t" + this);
	}
//*/
	
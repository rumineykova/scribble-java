package org.scribble.ext.assrt.ast.global;

import org.antlr.runtime.Token;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.ProtoHeader;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.global.GProtoHeader;
import org.scribble.ast.name.qualified.ProtoNameNode;
import org.scribble.core.type.kind.Global;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtBExprNode;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclList;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

public class AssrtGProtoHeader extends GProtoHeader
		implements AssrtStateVarDeclNode
{
	/*public static final int ASSRT_ANNOT_CHILD_INDEX = 3;  // null if no @-annotation

	// N.B. EXTID-parsed children of ASSRT_CHILD_INDEX subtree (i.e., grandchildren of this) -- cf. Assertions.g
	public static final int ANNOT_ASSERT_CHILD_INDEX = 0;  // null if not specified (means "true", but not written syntactically)
	public static final int ANNOT_STATEVAR_CHILDREN_START_INDEX = 1;*/

	//public static final int ROLEDECLLIST_CHILD = 2;
	public static final int ASSRT_STATEVARDECLLIST_CHILD_INDEX = 3;  // null if no @-annot; o/w may be empty (cf. ParamDeclList child) -- FIXME (deprecate empty)
	public static final int ASSRT_ASSERTION_CHILD_INDEX = 4;  // null if no @-annot; o/w may still be null

	// ScribTreeAdaptor#create constructor
	public AssrtGProtoHeader(Token t)
	{
		super(t);
	}
	
	// Tree#dupNode constructor
	protected AssrtGProtoHeader(AssrtGProtoHeader node)
	{
		super(node);
	}

	/*@Override
	public CommonTree getAnnotChild()
	{
		return (CommonTree) getChild(ASSRT_ANNOT_CHILD_INDEX);
	}*/
	
/*	@Override
	public List<AssrtIntVarNameNode> getAnnotVarChildren()
	{
//		List<? extends ScribNode> cs = getChildren();
//		return cs.subList(ANNOT_CHILDREN_START_INDEX, cs.size()).stream()  // TODO: refactor, cf. Module::getMemberChildren
//				.filter(x -> x instanceof AssrtIntVarNameNode)
//				.map(x -> (AssrtIntVarNameNode) x).collect(Collectors.toList());
		return Collections.emptyList();  // FIXME:
	}

	@Override
	public List<AssrtAExprNode> getAnnotExprChildren()
	{
//		List<? extends ScribNode> cs = getChildren();
//		return cs.subList(ANNOT_CHILDREN_START_INDEX, cs.size()).stream()  // TODO: refactor, cf. Module::getMemberChildren
//				.filter(x -> x instanceof AssrtArithExpr)
//				.map(x -> (AssrtArithExpr) x).collect(Collectors.toList());
		return Collections.emptyList();  // FIXME:
	}*/

	@Override
	public AssrtStateVarDeclList getStateVarDeclListChild()
	{
		return (AssrtStateVarDeclList) getChild(ASSRT_STATEVARDECLLIST_CHILD_INDEX);
	}

	// N.B. null if not specified -- currently duplicated from AssrtGMessageTransfer
	@Override
	public AssrtBExprNode getAnnotAssertChild()
	{
		/*CommonTree ext = getAnnotChild();
		if (ext == null)
		{
			return null;
		}
		Tree n = ext.getChild(ANNOT_ASSERT_CHILD_INDEX);
		
		System.out.println("3333: " + n);
		
		return (n.getText().equals("ASSRT_EMPTYASS"))  // TODO: factor out constant
				? null
				: (AssrtBExprNode) n;*/
		return (AssrtBExprNode) getChild(ASSRT_ASSERTION_CHILD_INDEX);
	}

	/*@Override
	public void addScribChildren(ProtoNameNode<Global> name,
			NonRoleParamDeclList ps, RoleDeclList rs)
	{
		throw new RuntimeException("Deprecated for " + getClass() + ":\n\t" + this);
	}*/

	// "add", not "set"
	public void addScribChildren(ProtoNameNode<Global> name,
			NonRoleParamDeclList ps, RoleDeclList rs, //List<AssrtIntVarNameNode> svars, List<AssrtAExprNode> sexprs)
			AssrtStateVarDeclList svars, AssrtBExprNode ass)
	{
		// Cf. above getters and Scribble.g children order
		super.addScribChildren(name, ps, rs);
		addChild(svars);
		addChild(ass);
		//addChildren(sexprs);
	}
	
	@Override
	public AssrtGProtoHeader dupNode()
	{
		return new AssrtGProtoHeader(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtGProtoHeader(this);
	}

	/*@Override
	public AssrtGProtoHeader reconstruct(ProtoNameNode<Global> name,
			NonRoleParamDeclList ps, RoleDeclList rs)
	{
		throw new RuntimeException(
				"[assrt] Deprecated for " + getClass() + ":\n\t" + this);
	}*/

	public AssrtGProtoHeader reconstruct(ProtoNameNode<Global> name,
			NonRoleParamDeclList ps, RoleDeclList rs, //List<AssrtIntVarNameNode> svars, List<AssrtAExprNode> sexprs)
			AssrtStateVarDeclList svars, AssrtBExprNode ass)
	{
		AssrtGProtoHeader dup = dupNode();
		dup.addScribChildren(name, ps, rs, svars, ass);//, sexprs);
		dup.setDel(del());  // No copy
		return dup;
	}
	
	@Override
	public GProtoHeader visitChildren(AstVisitor v) throws ScribException
	{
		/*ProtocolNameNode<K> nameNodeChild = (ProtocolNameNode<K>) visitChild(
				getNameNodeChild(), nv);* /  // Don't really need to visit, and can avoid generic cast
		RoleDeclList rs = (RoleDeclList) visitChild(getRoleDeclListChild(), v);
		NonRoleParamDeclList ps = (NonRoleParamDeclList) 
				visitChild(getParamDeclListChild(), v);*/
		ProtoHeader<Global> sup = super.visitChildren(v);
		/*List<AssrtIntVarNameNode> svars = visitChildListWithClassEqualityCheck(
				this, getAnnotVarChildren(), v);
		List<AssrtAExprNode> sexprs = visitChildListWithClassEqualityCheck(this,
				getAnnotExprChildren(), v);*/
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

	public AssrtGProtoHeader(CommonTree source, GProtoNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls)
	{
		this(source, name, roledecls, paramdecls, //null);
				Collections.emptyList(), Collections.emptyList(), null);
	}

	//Pre: annotvars.size() == annotexprs.size()
	public AssrtGProtoHeader(CommonTree source, GProtoNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass)
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
	public Map<AssrtDataTypeVar, AssrtArithFormula> getAnnotDataVarDecls()  // Cf. AssrtAnnotDataTypeElem (no "initializer")
	{
		//return (this.ass == null) ? null : (AssrtBinCompFormula) this.ass.getFormula();
		//return (AssrtBinCompFormula) this.ass.getFormula();
		Iterator<AssrtArithExprNode> exprs = getAnnotExprChildren().iterator();
		return getAnnotVarChildren().stream().collect(
				Collectors.toMap(v -> v.toName(), v -> exprs.next().getFormula()));
	}

	// project method pattern is similar to reconstruct
	@Override
	public AssrtLProtocolHeader project(AstFactory af, Role self, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls)
	{
		//return ((AssrtAstFactory) af).AssrtLProtocolHeader(this.source, name, roledecls, paramdecls, null);
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	// FIXME: make a delegate and move there?
	public AssrtLProtocolHeader project(AstFactory af, Role self, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		return ((AssrtAstFactory) af).AssrtLProtocolHeader(this.source, name, roledecls, paramdecls, //ass);
				annotvars, annotexprs,
				ass);
	}
*/
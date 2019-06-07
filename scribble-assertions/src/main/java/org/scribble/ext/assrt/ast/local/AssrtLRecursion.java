package org.scribble.ext.assrt.ast.local;

import org.antlr.runtime.Token;
import org.scribble.ast.ProtoBlock;
import org.scribble.ast.Recursion;
import org.scribble.ast.local.LRecursion;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.core.type.kind.Local;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtBExprNode;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclList;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.Constants;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

@Deprecated
public class AssrtLRecursion extends LRecursion
		implements AssrtStateVarDeclNode
{
	//public static final int BODY_CHILD_INDEX = 1;
	public static final int ASSRT_STATEVARDECLLIST_CHILD_INDEX = 2;  // null if no @-annot; o/w may be empty (cf. ParamDeclList child) -- FIXME: currently never null?
	public static final int ASSRT_ASSERTION_CHILD_INDEX = 3;  // null if no @-annot; o/w may still be null

	// ScribTreeAdaptor#create constructor
	public AssrtLRecursion(Token t)
	{
		super(t);
	}
	
	// Tree#dupNode constructor
	protected AssrtLRecursion(AssrtLRecursion node)
	{
		super(node);
	}

	// Following duplicated from AssrtGProtoHeader

	@Override
	public AssrtStateVarDeclList getStateVarDeclListChild()
	{
		return (AssrtStateVarDeclList) getChild(ASSRT_STATEVARDECLLIST_CHILD_INDEX);
	}

	// N.B. null if not specified -- currently duplicated from AssrtGMessageTransfer
	@Override
	public AssrtBExprNode getAnnotAssertChild()
	{
		return (AssrtBExprNode) getChild(ASSRT_ASSERTION_CHILD_INDEX);
	}

	// "add", not "set"
	public void addScribChildren(RecVarNode rv, ProtoBlock<Local> block,
			AssrtStateVarDeclList svars, AssrtBExprNode ass)
	{
		// Cf. above getters and Scribble.g children order
		super.addScribChildren(rv, block);
		addChild(svars);
		addChild(ass);
	}
	
	@Override
	public AssrtLRecursion dupNode()
	{
		return new AssrtLRecursion(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtLRecursion(this);
	}

	public AssrtLRecursion reconstruct(RecVarNode recvar, ProtoBlock<Local> block,
			AssrtStateVarDeclList svars, AssrtBExprNode ass)
	{
		AssrtLRecursion dup = dupNode();
		dup.addScribChildren(recvar, block, svars, ass);
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public AssrtLRecursion visitChildren(AstVisitor v) throws ScribException
	{
		Recursion<Local> sup = super.visitChildren(v);
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
		return reconstruct(sup.getRecVarChild(), sup.getBlockChild(), svars, ass);
	}

	@Override
	public String toString()
	{
		return Constants.REC_KW + " " + getRecVarChild() //+ " " + this.ass
				+ annotToString()
				+ " " + getBlockChild();
	}
}








/*
	public final List<AssrtIntVarNameNode> avars;
	public final List<AssrtArithExpr> axprs;
	public final AssrtAssertion ass;  // cf. AssrtGProtoHeader  // FIXME: make specific syntactic expr
	
	public AssrtLRecursion(CommonTree source, RecVarNode recvar,
			LProtoBlock block)
	{
		this(source, recvar, block, //null);
				Collections.emptyList(), Collections.emptyList(),
				null);
	}

	public AssrtLRecursion(CommonTree source, RecVarNode recvar,
			LProtoBlock block, List<AssrtIntVarNameNode> avars,
			List<AssrtArithExpr> aexprs, AssrtAssertion ass)
	{
		super(source, recvar, block);
		this.avars = Collections.unmodifiableList(avars);
		this.axprs = Collections.unmodifiableList(aexprs);
		this.ass = ass;
	}
//*/



	/*//public static final int BODY_CHILD_INDEX = 1;
	// FIXME: no: Assertions.g gives back a subtree containing all
	public static final int ASSERT_CHILD_INDEX = 2;  // May be null (means "true")
	public static final int ANNOT_CHILDREN_START_INDEX = 3;*/

	// N.B. null if not specified -- currently duplicated from AssrtGMessageTransfer
	/*@Override
	public List<AssrtIntVarNameNode> getAnnotVarChildren()
	{
		List<? extends ScribNode> cs = getChildren();
		return cs.subList(ANNOT_CHILDREN_START_INDEX, cs.size()).stream()  // TODO: refactor, cf. Module::getMemberChildren
				.filter(x -> x instanceof AssrtIntVarNameNode)
				.map(x -> (AssrtIntVarNameNode) x).collect(Collectors.toList());
	}

	@Override
	public List<AssrtAExprNode> getAnnotExprChildren()
	{
		List<? extends ScribNode> cs = getChildren();
		return cs.subList(ANNOT_CHILDREN_START_INDEX, cs.size()).stream()  // TODO: refactor, cf. Module::getMemberChildren
				.filter(x -> x instanceof AssrtAExprNode)
				.map(x -> (AssrtAExprNode) x).collect(Collectors.toList());
	}*/

	/*// Because svars never null -- no: null better for super addScribChildren/reconstruct pattern
	@Override
	public void addScribChildren(RecVarNode rv, ProtoBlock<Local> block)
	{
		throw new RuntimeException("Deprecated for " + getClass() + ":\n\t" + this);
	}*/

	/*@Override
	public AssrtLRecursion reconstruct(RecVarNode recvar,
			ProtoBlock<Local> block)
	{
		throw new RuntimeException(
				"[assrt] Deprecated for " + getClass() + ": " + this);
	}*/
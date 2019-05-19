package org.scribble.ext.assrt.ast.local;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.Token;
import org.scribble.ast.ProtoBlock;
import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LProtoBlock;
import org.scribble.ast.local.LRecursion;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.core.type.kind.Local;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclAnnotNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.Constants;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

public class AssrtLRecursion extends LRecursion
		implements AssrtStateVarDeclAnnotNode
{
	//public static final int BODY_CHILD_INDEX = 1;
	// FIXME: no: Assertions.g gives back a subtree containing all
	public static final int ASSERT_CHILD_INDEX = 2;  // May be null (means "true")
	public static final int ANNOT_CHILDREN_START_INDEX = 3;

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

	// N.B. null if not specified -- currently duplicated from AssrtGMessageTransfer
	@Override
	public AssrtAssertion getAnnotAssertChild()
	{
		return (AssrtAssertion) getChild(ASSERT_CHILD_INDEX);
	}
	
	@Override
	public List<AssrtIntVarNameNode> getAnnotVarChildren()
	{
		List<? extends ScribNode> cs = getChildren();
		return cs.subList(ANNOT_CHILDREN_START_INDEX, cs.size()).stream()  // TODO: refactor, cf. Module::getMemberChildren
				.filter(x -> x instanceof AssrtIntVarNameNode)
				.map(x -> (AssrtIntVarNameNode) x).collect(Collectors.toList());
	}

	@Override
	public List<AssrtArithExpr> getAnnotExprChildren()
	{
		List<? extends ScribNode> cs = getChildren();
		return cs.subList(ANNOT_CHILDREN_START_INDEX, cs.size()).stream()  // TODO: refactor, cf. Module::getMemberChildren
				.filter(x -> x instanceof AssrtArithExpr)
				.map(x -> (AssrtArithExpr) x).collect(Collectors.toList());
	}

	@Override
	public void addScribChildren(RecVarNode rv, ProtoBlock<Local> block)
	{
		throw new RuntimeException("Deprecated for " + getClass() + ":\n\t" + this);
	}

	// "add", not "set"
	public void addScribChildren(RecVarNode rv, ProtoBlock<Local> block,
			AssrtAssertion assrt, List<AssrtIntVarNameNode> avars,
			List<AssrtArithExpr> aexprs)
	{
		// Cf. above getters and Scribble.g children order
		super.addScribChildren(rv, block);
		addChild(assrt);
		addChildren(avars);
		addChildren(aexprs);
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

	@Override
	public AssrtLRecursion reconstruct(RecVarNode recvar,
			ProtoBlock<Local> block)
	{
		throw new RuntimeException(
				"[assrt] Deprecated for " + getClass() + ": " + this);
	}

	public AssrtLRecursion reconstruct(RecVarNode recvar,
			ProtoBlock<Local> block, AssrtAssertion ass,
			List<AssrtIntVarNameNode> avars, List<AssrtArithExpr> aexprs)
	{
		AssrtLRecursion dup = dupNode();
		dup.addScribChildren(recvar, block, ass, avars, aexprs);
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public AssrtLRecursion visitChildren(AstVisitor v) throws ScribException
	{
		RecVarNode recvar = (RecVarNode) visitChild(getRecVarChild(), v);
		LProtoBlock block = visitChildWithClassEqualityCheck(this, getBlockChild(),
				v);
		AssrtAssertion tmp = getAnnotAssertChild();
		AssrtAssertion ass = (tmp == null) 
				? null
				: (AssrtAssertion) visitChild(tmp, v);
		List<AssrtIntVarNameNode> avars = visitChildListWithClassEqualityCheck(
				this, getAnnotVarChildren(), v);
		List<AssrtArithExpr> aexprs = visitChildListWithClassEqualityCheck(this,
				getAnnotExprChildren(), v);
		return reconstruct(recvar, block, ass, avars, aexprs);
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

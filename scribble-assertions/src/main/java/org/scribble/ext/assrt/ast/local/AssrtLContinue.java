package org.scribble.ext.assrt.ast.local;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.Token;
import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LContinue;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtAExprNode;
import org.scribble.ext.assrt.ast.AssrtStateVarArgNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

@Deprecated
public class AssrtLContinue extends LContinue implements AssrtStateVarArgNode
{
	public static final int EXPR_CHILDREN_START_INDEX = 1;  // cf. AssrtGDo

	// ScribTreeAdaptor#create constructor
	public AssrtLContinue(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	protected AssrtLContinue(AssrtLContinue node)
	{
		super(node);
	}
	
	@Override
	public List<AssrtAExprNode> getAnnotExprChildren()
	{
		List<? extends ScribNode> cs = getChildren();
		return cs.subList(EXPR_CHILDREN_START_INDEX, cs.size()).stream()
				.map(x -> (AssrtAExprNode) x).collect(Collectors.toList());
	}
	
	@Override
	public void addScribChildren(RecVarNode rv)
	{
		throw new RuntimeException(
				"[assrt] Deprecated for " + getClass() + ":\n\t" + this);
	}

	// "add", not "set"
	public void addScribChildren(RecVarNode rv, List<AssrtAExprNode> aexprs)
	{
		// Cf. above getters and Scribble.g children order
		addChild(rv);
		addChildren(aexprs);
	}

	@Override
	public AssrtLContinue dupNode()
	{
		return new AssrtLContinue(this);
	}

	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtLContinue(this);
	}

	@Override
	public AssrtLContinue reconstruct(RecVarNode rv)
	{
		throw new RuntimeException(
				"[assrt] Deprecated for " + getClass() + ":\n\t" + this);
	}

	public AssrtLContinue reconstruct(RecVarNode rv, List<AssrtAExprNode> aexprs)
	{
		AssrtLContinue dup = dupNode();
		dup.addScribChildren(rv, aexprs);
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public LContinue visitChildren(AstVisitor v) throws ScribException
	{
		RecVarNode rv = (RecVarNode) visitChild(getRecVarChild(), v);
		List<AssrtAExprNode> aexprs = visitChildListWithClassEqualityCheck(this,
				getAnnotExprChildren(), v);
		return reconstruct(rv, aexprs);
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
	
	public AssrtLContinue(CommonTree source, RecVarNode recvar)
	{
		this(source, recvar, //null);
				Collections.emptyList());
	}

	public AssrtLContinue(CommonTree source, RecVarNode recvar, //AssrtArithExpr annot)
			List<AssrtArithExpr> annotexprs)
	{
		super(source, recvar);
		//this.annot = annot;
		this.annotexprs = Collections.unmodifiableList(annotexprs);
	}
	*/

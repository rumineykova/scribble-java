package org.scribble.ext.assrt.ast;

import org.antlr.runtime.Token;
import org.scribble.ast.ScribNodeBase;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.del.AssrtDelFactory;

// Based on AssrtAssertion
public class AssrtAExprNode extends ScribNodeBase implements AssrtExprNode
{
	public final AssrtAFormula expr;

	// ScribTreeAdaptor#create constructor
	public AssrtAExprNode(int ttype, Token t, AssrtAFormula aexpr)  // EXTID<AssrtBExprNode>[$EXTID, ...], cf. Scribble.g simple names
	{
		super(t);
		this.expr = aexpr;
	}

	// Tree#dupNode constructor
	protected AssrtAExprNode(AssrtAExprNode node)
	{
		super(node);
		this.expr = node.expr;
	}

	@Override
	public AssrtAExprNode dupNode()
	{
		return new AssrtAExprNode(this);
	}

	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtArithExpr(this);
	}

	@Override
	public AssrtAFormula getFormula()
	{
		return this.expr;
	}

	@Override
	public String toString()
	{
		return this.expr.toString();
	}
}

/*
	private AssrtArithFormula expr;

	public AssrtArithExpr(CommonTree source, AssrtArithFormula expr)
	{
		super(source);
		this.expr = expr; 
	}
*/
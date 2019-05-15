package org.scribble.ext.assrt.ast;

import org.antlr.runtime.Token;
import org.scribble.ast.ScribNodeBase;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.del.AssrtDelFactory;

// Based on AssrtAssertion
public class AssrtArithExpr extends ScribNodeBase implements AssrtFormulaNode
{	
	public final AssrtArithFormula expr;

	// ScribTreeAdaptor#create constructor
	public AssrtArithExpr(Token t, AssrtArithFormula expr)
	{
		super(t);
		this.expr = expr;
	}

	// Tree#dupNode constructor
	protected AssrtArithExpr(AssrtArithExpr node)
	{
		super(node);
		this.expr = node.expr;
	}
	
	@Override
	public AssrtArithExpr dupNode()
	{
		return new AssrtArithExpr(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtArithExpr(this);
	}

	@Override
	public AssrtArithFormula getFormula()
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
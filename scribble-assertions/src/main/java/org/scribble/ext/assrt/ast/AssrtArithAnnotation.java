package org.scribble.ext.assrt.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ScribNode;
import org.scribble.ast.ScribNodeBase;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.main.ScribbleException;
import org.scribble.visit.AstVisitor;

// Based on AssrtAssertion
public class AssrtArithAnnotation extends ScribNodeBase 
{	
	private AssrtArithFormula expr;

	public AssrtArithAnnotation(CommonTree source, AssrtArithFormula expr)
	{
		super(source);
		this.expr = expr; 
	}
	
	@Override
	protected AssrtArithAnnotation copy()
	{
		return new AssrtArithAnnotation(this.source, this.expr);
	}
	
	@Override
	public AssrtArithAnnotation clone(AstFactory af)
	{
		return (AssrtArithAnnotation) ((AssrtAstFactory) af).AssrtArithAnnotation(this.source, this.expr);  // expr is immutable
	}

	protected AssrtArithAnnotation reconstruct(AssrtArithFormula f)
	{
		ScribDel del = del();
		AssrtArithAnnotation an = new AssrtArithAnnotation(this.source, f);
		an = (AssrtArithAnnotation) an.del(del);
		return an;
	}

	@Override
	public ScribNode visitChildren(AstVisitor nv) throws ScribbleException
	{
		return reconstruct(this.expr);  // formula cannot be visited (not a ScribNode)
	}
	
	public AssrtArithFormula getFormula()
	{
		return this.expr;
	}
	
	@Override
	public String toString()
	{
		return "@" + this.expr.toString() + ";";  
	}
}

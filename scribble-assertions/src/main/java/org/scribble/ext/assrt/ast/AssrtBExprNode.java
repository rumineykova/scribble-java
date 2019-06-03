package org.scribble.ext.assrt.ast;

import org.antlr.runtime.Token;
import org.scribble.ast.ScribNodeBase;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.del.AssrtDelFactory;

// ScribNode wrapper for AssrtBFormula (that is parsed "directly" to Formula)
// In general, should be an action "annotation" -- but currently only used for boolean assertions
// This is the "actual syntax" node (has source) -- cf. formula, does (and should) not record source (e.g., affects equals/hash)
public class AssrtBExprNode extends ScribNodeBase implements AssrtExprNode
{	
	//protected   // Non public, because non final for reconstruct
	public final AssrtBFormula expr;

	// ScribTreeAdaptor#create constructor
	public AssrtBExprNode(int ttype, Token t, AssrtBFormula bexpr)  // EXTID<AssrtBExprNode>[$EXTID, ...], cf. Scribble.g simple names
	{
		super(t);
		this.expr = bexpr;
	}

	// Tree#dupNode constructor
	protected AssrtBExprNode(AssrtBExprNode node)
	{
		super(node);
		this.expr = node.expr;
	}
	
	@Override
	public AssrtBExprNode dupNode()
	{
		return new AssrtBExprNode(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtAssertion(this);
	}
	
	@Override
	public AssrtBFormula getFormula()
	{
		return this.expr;
	}
	
	@Override
	public String toString()
	{
		return "@\"" + this.expr.toString() + "\"";  
	}
}







/*

	//public AssrtAssertion(CommonTree source, SmtFormula formula)
	public AssrtAssertion(CommonTree source, AssrtBoolFormula formula)
	{
		super(source);
		this.formula = formula; 
	}
	
	/*protected AssrtAssertion reconstruct(AssrtBoolFormula f)
	{
		AssrtAssertion dup = dupNode();
		dup.formula = f;
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public ScribNode visitChildren(AstVisitor nv) throws ScribException
	{
		return reconstruct(this.formula);  // formula cannot be visited (not a ScribNode)
	}*/
//*/

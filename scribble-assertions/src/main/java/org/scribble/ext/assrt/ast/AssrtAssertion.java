package org.scribble.ext.assrt.ast;

import org.antlr.runtime.Token;
import org.scribble.ast.ScribNodeBase;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.del.AssrtDelFactory;

// In general, should be an action "annotation" -- but currently only used for boolean assertions
// This is the "actual syntax" node (has source) -- cf. formula, does (and should) not record source (e.g., affects equals/hash)
public class AssrtAssertion extends ScribNodeBase implements AssrtFormulaNode
{	
	//protected   // Non public, because non final for reconstruct
	public final AssrtBoolFormula bexpr;

	// ScribTreeAdaptor#create constructor
	public AssrtAssertion(Token t, AssrtBoolFormula bexpr)
	{
		super(t);
		this.bexpr = bexpr;
	}

	// Tree#dupNode constructor
	protected AssrtAssertion(AssrtAssertion node)
	{
		super(node);
		this.bexpr = node.bexpr;
	}
	
	@Override
	public AssrtAssertion dupNode()
	{
		return new AssrtAssertion(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtAssertion(this);
	}
	
	@Override
	public AssrtBoolFormula getFormula()
	{
		return this.bexpr;
	}
	
	@Override
	public String toString()
	{
		return "@" + this.bexpr.toString() + ";";  
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

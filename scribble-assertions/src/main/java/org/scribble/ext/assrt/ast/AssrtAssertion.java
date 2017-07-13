package org.scribble.ext.assrt.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ScribNode;
import org.scribble.ast.ScribNodeBase;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.formula.SmtFormula;
import org.scribble.main.ScribbleException;
import org.scribble.visit.AstVisitor;

public class AssrtAssertion extends ScribNodeBase 
{	
	//private final String assertion;  // FIXME: should be String for a more general annotations feature

	private SmtFormula formula;  // Not a ScribNode (no clone/copy/accept/etc -- but is immutable)

	//public AssrtAssertionNode(CommonTree source, String assertion)
	public AssrtAssertion(CommonTree source, SmtFormula formula)
	{
		super(source);
		//this.assertion = assertion; 
		this.formula = formula; 
	}
	
	@Override
	protected AssrtAssertion copy()
	{
		//return new AssrtAssertionNode(this.source, this.assertion);
		return new AssrtAssertion(this.source, this.formula);
	}
	
	@Override
	public AssrtAssertion clone(AstFactory af)
	{
		//return (AssrtAssertionNode) AssrtAstFactoryImpl.FACTORY.AssertionNode(this.source, this.assertion);
		return (AssrtAssertion) ((AssrtAstFactory) af).AssrtAssertion(this.source, this.formula);  // formula is immutable
	}

	/*public String getAssertion()
	{
		return this.assertion; 
	}*/
	
	protected AssrtAssertion reconstruct(SmtFormula f)
	{
		ScribDel del = del();
		AssrtAssertion an = new AssrtAssertion(this.source, f);
		an = (AssrtAssertion) an.del(del);
		return an;
	}

	@Override
	public ScribNode visitChildren(AstVisitor nv) throws ScribbleException
	{
		return reconstruct(this.formula);  // formula cannot be visited (not a ScribNode)
	}
	
	public SmtFormula getFormula()
	{
		/*if (this.formula == null)
		{
			//try
			{
				this.formula = AssrtAssertParser.getInstance().parse((CommonTree) this.source.getChild(0));  // FIXME: should be parsed by parser
			}
			/*catch (AssertionsParseException e)
			{
				System.err.print("Assertion cannot be parsed" + e.getMessage());
			}* /
		}*/
		return this.formula;
	}
	
	@Override
	public String toString()
	{
		//return this.toFormula().toString(); 
		return this.formula.toString(); 
	}
}

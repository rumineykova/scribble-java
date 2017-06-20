package org.scribble.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.assertions.StmFormula;

import parser.AssertionsParseException;
import parser.AssertionsScribParser;

// FIXME: visitChildren/reconstruct
public class AAssertionNode extends ScribNodeBase 
{	
	private final String assertion;  // FIXME: should be earlier parsed
	private StmFormula formula =  null; 

	public AAssertionNode(CommonTree source, String assertion)
	{
		super(source);
		this.assertion = assertion; 
	}
	
	@Override
	protected AAssertionNode copy()
	{
		return new AAssertionNode(this.source, this.assertion);
	}
	
	@Override
	public AAssertionNode clone()
	{
		return (AAssertionNode) AAstFactoryImpl.FACTORY.AssertionNode(this.source, this.assertion);
	}

	public String getAssertion()
	{
		return this.assertion; 
	}
	
	public StmFormula toFormula()
	{
		if (this.formula == null)
		{
			try
			{
				this.formula = AssertionsScribParser.getInstance().parse((CommonTree)this.source.getChild(0));  // FIXME: should be parsed by parser
			}
			catch (AssertionsParseException e)
			{
				System.err.print("Assertion cannot be parsed" + e.getMessage());
			}
		}
		return formula; 
	}
	
	@Override
	public String toString()
	{
		return this.toFormula().toString(); 
	}
}

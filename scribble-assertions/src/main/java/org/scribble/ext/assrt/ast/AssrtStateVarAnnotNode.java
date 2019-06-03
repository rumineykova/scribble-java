package org.scribble.ext.assrt.ast;

import org.antlr.runtime.Token;
import org.scribble.ast.ScribNodeBase;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.del.AssrtDelFactory;

@Deprecated
public class AssrtStateVarAnnotNode extends ScribNodeBase
{	
	// ScribTreeAdaptor#create constructor
	public AssrtStateVarAnnotNode(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	protected AssrtStateVarAnnotNode(AssrtStateVarAnnotNode node)
	{
		super(node);
	}
	
	@Override
	public AssrtStateVarAnnotNode dupNode()
	{
		return new AssrtStateVarAnnotNode(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtStateVarAnnotNode(this);
	}
	
	/*@Override
	public String toString()
	{
		return "@\"" + this.expr.toString() + "\"";  
	}*/
}

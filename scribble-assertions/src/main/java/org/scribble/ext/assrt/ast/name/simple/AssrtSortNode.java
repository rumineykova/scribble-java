package org.scribble.ext.assrt.ast.name.simple;

import org.antlr.runtime.Token;
import org.scribble.ast.name.simple.SimpleNameNode;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.core.type.kind.AssrtSortKind;
import org.scribble.ext.assrt.core.type.name.AssrtSort;

@Deprecated  // Currently unused
public class AssrtSortNode extends SimpleNameNode<AssrtSortKind>
{
	public AssrtSortNode(int ttype, Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	protected AssrtSortNode(AssrtSortNode node)
	{
		super(node);
	}
	
	@Override
	public AssrtSortNode dupNode()
	{
		return new AssrtSortNode(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		//((AssrtDelFactory) df).AssrtSortNode(this);
		throw new RuntimeException("[TODO] : " + this);
	}

	@Override
	public AssrtSort toName()
	{
		return new AssrtSort(getText());
	}
	
	@Override
	public boolean equals(Object o)  // FIXME: is equals/hashCode needed for these Nodes?
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtSortNode))
		{
			return false;
		}
		return super.equals(o);  // Checks canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtSortNode;
	}

	@Override
	public int hashCode()
	{
		int hash = 7639;
		hash = 31 * super.hashCode();
		return hash;
	}
}













/*
	public AssrtSortNode(CommonTree source, String identifier)
	{
		super(source, identifier);
	}
*/
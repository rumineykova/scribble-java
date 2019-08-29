package org.scribble.ext.assrt.ast.name.qualified;

import org.antlr.runtime.Token;
import org.scribble.ast.name.qualified.MemberNameNode;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.core.type.kind.AssrtAssertKind;
import org.scribble.ext.assrt.core.type.name.AssrtAssertName;

// Duplicated From DataNameNode
@Deprecated  // Currently unused
public class AssrtAssertNameNode extends MemberNameNode<AssrtAssertKind>
{
	// ScribTreeAdaptor#create constructor
	public AssrtAssertNameNode(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	protected AssrtAssertNameNode(AssrtAssertNameNode node)
	{
		super(node);
	}
	
	@Override
	public AssrtAssertNameNode dupNode()
	{
		return new AssrtAssertNameNode(this);
	}

	@Override
	public void decorateDel(DelFactory df)
	{
		//((AssrtDelFactory) df).AssrtAssertNameNode(this);
		throw new RuntimeException("[TODO] : " + this);
	}

	@Override
	public AssrtAssertName toName()
	{
		AssrtAssertName membname = new AssrtAssertName(getLastElement());
		return isPrefixed()
				? new AssrtAssertName(getModuleNamePrefix(), membname)
		    : membname;
	}
	
	@Override
	public boolean equals(Object o)  // FIXME: is equals/hashCode needed for these Nodes?
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtAssertNameNode))
		{
			return false;
		}
		return super.equals(o);  // Checks canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtAssertNameNode;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 7621;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
}












/*
	public AssrtAssertNameNode(CommonTree source, String... elems)
	{
		super(source, elems);
	}
*/
package org.scribble.ext.assrt.ast.name.qualified;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.name.qualified.MemberNameNode;
import org.scribble.ext.assrt.type.kind.AssrtAssertKind;
import org.scribble.type.name.MemberName;

public class AssrtAssertNameNode extends MemberNameNode<AssrtAssertKind>  // Duplicated From DataTypeNode
{
	public AssrtAssertNameNode(CommonTree source, String... elems)
	{
		super(source, elems);
	}

	@Override
	protected AssrtAssertNameNode copy()
	{
		return new AssrtAssertNameNode(this.source, this.elems);
	}
	
	@Override
	public AssrtAssertNameNode clone(AstFactory af)
	{
		return (AssrtAssertNameNode) af.QualifiedNameNode(this.source, AssrtAssertKind.KIND, this.elems);
	}

	@Override
	public MemberName<AssrtAssertKind> toName()  // FIXME: asserts aren't directly "typed"
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
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
		return ((AssrtAssertNameNode) o).canEqual(this) && super.equals(o);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtAssertNameNode;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 7621;
		hash = 31 * hash + this.elems.hashCode();
		return hash;
	}
}

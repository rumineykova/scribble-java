package org.scribble.ext.assrt.ast.name.simple;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.name.simple.SimpleNameNode;
import org.scribble.ext.assrt.type.kind.AssrtSortKind;
import org.scribble.ext.assrt.type.name.AssrtSort;

public class AssrtSortNode extends SimpleNameNode<AssrtSortKind> //implements PayloadElemNameNode<AssrtVarNameKind>, AssrtFormulaNode
{
	public AssrtSortNode(CommonTree source, String identifier)
	{
		super(source, identifier);
	}

	@Override
	protected ScribNodeBase copy()
	{
		return new AssrtSortNode(this.source, getIdentifier());
	}

	@Override
	public AssrtSortNode clone(AstFactory af)
	{
		return (AssrtSortNode) af.SimpleNameNode(this.source, AssrtSortKind.KIND, getIdentifier());
	}

	@Override
	public AssrtSort toName()
	{
		String id = getIdentifier();
		return new AssrtSort(id);
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
		return ((AssrtSortNode) o).canEqual(this) && super.equals(o);
	}
	
	@Override
	public boolean canEqual(Object o)
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

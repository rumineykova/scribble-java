package org.scribble.ext.assrt.ast.name.simple;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.name.NameNode;
import org.scribble.ast.name.PayloadElemNameNode;
import org.scribble.ast.name.simple.SimpleNameNode;
import org.scribble.ext.assrt.sesstype.kind.AssrtVarNameKind;
import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.sesstype.Arg;
import org.scribble.sesstype.kind.NonRoleArgKind;

// N.B. used both directly as a PayloadElemNameNode and for the annotation in AssrtAnnotDataTypeElem
public class AssrtVarNameNode extends SimpleNameNode<AssrtVarNameKind> implements PayloadElemNameNode<AssrtVarNameKind>
{
	public AssrtVarNameNode(CommonTree source, String identifier)
	{
		super(source, identifier);
	}

	@Override
	protected ScribNodeBase copy()
	{
		return new AssrtVarNameNode(this.source, getIdentifier());
	}

	@Override
	public NameNode<AssrtVarNameKind> clone(AstFactory af)
	{
		return (AssrtVarNameNode) af.SimpleNameNode(this.source, AssrtVarNameKind.KIND, getIdentifier());
	}

	@Override
	public AssrtDataTypeVar toName() {
		return new AssrtDataTypeVar(getIdentifier());
	}
	
	@Override
	public boolean equals(Object o)  // FIXME: is equals/hashCode needed for these Nodes?
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtVarNameNode))
		{
			return false;
		}
		return ((AssrtVarNameNode) o).canEqual(this) && super.equals(o);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtVarNameNode;
	}

	@Override
	public int hashCode()
	{
		int hash = 967;
		hash = 31 * super.hashCode();
		return hash;
	}

	@Override
	public Arg<? extends NonRoleArgKind> toArg()
	{
		throw new RuntimeException("[scrib-assert] TODO: var name node as do-arg: " + this);  // TODO?
	}

	@Override
	public AssrtDataTypeVar toPayloadType()
	{
		return toName();  // FIXME: Shoudln't this be the type, not the var name?
	}
}

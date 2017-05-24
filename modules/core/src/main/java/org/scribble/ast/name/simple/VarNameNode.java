package org.scribble.ast.name.simple;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.name.NameNode;
import org.scribble.ast.name.PayloadElemNameNode;
import org.scribble.sesstype.Arg;
import org.scribble.sesstype.kind.NonRoleArgKind;
import org.scribble.sesstype.kind.AnnotVarNameKind;
import org.scribble.sesstype.name.AnnotVarName;

// Parser Identifier
public class VarNameNode extends SimpleNameNode<AnnotVarNameKind> implements PayloadElemNameNode<AnnotVarNameKind>
{
	public VarNameNode(CommonTree source, String identifier)
	{
		super(source, identifier);
	}
	
	public String getIdentifier()
	{
		return getLastElement();
	}

	@Override
	public AnnotVarName toName() {
		return new AnnotVarName(getIdentifier());
	}

	@Override
	public NameNode<AnnotVarNameKind> clone() {
		return (VarNameNode) AstFactoryImpl.FACTORY.SimpleNameNode(this.source, AnnotVarNameKind.KIND, getIdentifier());
	}

	@Override
	public boolean canEqual(Object o) {
		return o instanceof VarNameNode;
	}

	@Override
	protected ScribNodeBase copy() {
		return new VarNameNode(this.source, getIdentifier());
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof VarNameNode))
		{
			return false;
		}
		return ((VarNameNode) o).canEqual(this) && super.equals(o);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 349;
		hash = 31 * super.hashCode();
		return hash;
	}

	@Override
	public Arg<? extends NonRoleArgKind> toArg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnnotVarName toPayloadType() {
		return toName();
	}
}

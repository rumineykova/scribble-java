package org.scribble.ext.assrt.ast.name.simple;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.name.PayloadElemNameNode;
import org.scribble.ast.name.simple.SimpleNameNode;
import org.scribble.ext.assrt.ast.AssrtFormulaNode;
import org.scribble.ext.assrt.core.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.core.type.kind.AssrtVarNameKind;
import org.scribble.ext.assrt.core.type.name.AssrtDataTypeVar;
import org.scribble.type.Arg;
import org.scribble.type.kind.NonRoleArgKind;

// N.B. used both directly as a PayloadElemNameNode, and for the annotation in AssrtAnnotDataTypeElem -- also used for statevars
public class AssrtIntVarNameNode extends SimpleNameNode<AssrtVarNameKind>
		implements PayloadElemNameNode<AssrtVarNameKind>, AssrtFormulaNode
{
	public AssrtIntVarNameNode(CommonTree source, String identifier)
	{
		super(source, identifier);
	}

	@Override
	protected ScribNodeBase copy()
	{
		return new AssrtIntVarNameNode(this.source, getIdentifier());
	}

	@Override
	public AssrtIntVarNameNode clone(AstFactory af)
	{
		return (AssrtIntVarNameNode) af.SimpleNameNode(this.source, AssrtVarNameKind.KIND, getIdentifier());
	}
	
	@Override
	public AssrtIntVarFormula getFormula()
	{
		return AssrtFormulaFactory.AssrtIntVar(getIdentifier());
	}

	@Override
	public AssrtDataTypeVar toName()
	{
		//return new AssrtDataTypeVar(getIdentifier());
		return getFormula().toName();
	}
	
	@Override
	public boolean equals(Object o)  // FIXME: is equals/hashCode needed for these Nodes?
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtIntVarNameNode))
		{
			return false;
		}
		return ((AssrtIntVarNameNode) o).canEquals(this) && super.equals(o);
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtIntVarNameNode;
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
		throw new RuntimeException("[assrt] TODO: var name node as do-arg: " + this);  // TODO?
	}

	@Override
	public AssrtDataTypeVar toPayloadType()
	{
		return toName();  
				// FIXME: Shouldn't this be the type (i.e., int), not the var name? -- cf. toName
				// however, toPayloadType is "kinded" the same way as "toName", so have to return AssrtDataTypeVar (not an int DataType)
				// but maybe should be this way, for later toolchain stages, e.g., API generation (for these "dependent" types)
	}
}

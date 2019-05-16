package org.scribble.ext.assrt.ast.name.simple;

import org.antlr.runtime.Token;
import org.scribble.ast.name.PayElemNameNode;
import org.scribble.ast.name.simple.SimpleNameNode;
import org.scribble.core.type.kind.NonRoleArgKind;
import org.scribble.core.type.session.Arg;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtFormulaNode;
import org.scribble.ext.assrt.core.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.core.type.kind.AssrtIntVarNameKind;
import org.scribble.ext.assrt.core.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.del.AssrtDelFactory;

// N.B. used both directly as a PayloadElemNameNode, and for the annotation in AssrtAnnotDataTypeElem -- also used for statevars
public class AssrtIntVarNameNode extends SimpleNameNode<AssrtIntVarNameKind>
		implements PayElemNameNode<AssrtIntVarNameKind>, AssrtFormulaNode
{
	// ScribTreeAdaptor#create constructor
	// Constructor sig for ANTLR "node token" option, generally ttype == t.getType(), where t is a ScribbleParser.ID token type
	public AssrtIntVarNameNode(int ttype, Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	protected AssrtIntVarNameNode(AssrtIntVarNameNode node)
	{
		super(node);
	}
	
	@Override
	public AssrtIntVarNameNode dupNode()
	{
		return new AssrtIntVarNameNode(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtIntVarNameNode(this);
	}
	
	@Override
	public AssrtIntVarFormula getFormula()
	{
		return AssrtFormulaFactory.AssrtIntVar(getText());
	}

	@Override
	public AssrtDataTypeVar toName()
	{
		return getFormula().toName();
	}

	@Override
	public Arg<? extends NonRoleArgKind> toArg()
	{
		throw new RuntimeException(
				"[assrt] TODO: var name node as do-arg: " + this);
				// CHECKME TODO?
	}

	@Override
	public AssrtDataTypeVar toPayloadType()
	{
		return toName();  
				// CHECKME: Shouldn't this be the type (i.e., int), not the var name? -- cf. toName
				// However, toPayloadType is "kinded" the same way as "toName", so have to return AssrtDataTypeVar (not an int DataType)
				// But maybe should be this way, for later toolchain stages, e.g., API generation (for these "dependent" types)
	}
	
	@Override
	public boolean equals(Object o)  // CHECKME: is equals/hashCode needed for these Nodes?
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtIntVarNameNode))
		{
			return false;
		}
		return super.equals(o);  // Checks canEquals
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
}





/*
	public AssrtIntVarNameNode(CommonTree source, String identifier)
	{
		super(source, identifier);
	}
*/
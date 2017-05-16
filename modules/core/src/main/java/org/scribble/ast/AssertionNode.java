package org.scribble.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.OpNode;
import org.scribble.ast.name.simple.VarNameNode;
import org.scribble.del.ScribDel;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.AnnotPayload;
import org.scribble.sesstype.kind.OpKind;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

// Cf. DoArg, wrapper for a (unary) name node of potentially unknown kind (needs disamb)
// PayloadTypeKind is DataType or Local, but Local has its own special subclass (and protocol params not allowed), so this should implicitly be for DataType only
// AST hierarchy requires unary and delegation (binary pair) payloads to be structurally distinguished
//public class DataTypeElem extends PayloadElem<DataTypeKind>
public class AssertionNode extends ScribNodeBase 
{	
	private final String assertion; 
	public AssertionNode(CommonTree source, String assertion)
	{
		//super(name);
		//this.data = data;
		super(source);
		this.assertion = assertion; 
	}

	@Override
	protected AssertionNode copy() {
		return new AssertionNode(this.source, this.assertion);
	}

	public String getAssertion()
	{
		return this.assertion; 
	}
	
	@Override
	public AssertionNode clone() {
		return (AssertionNode) AstFactoryImpl.FACTORY.AssertionNode(this.source, this.assertion);
	}
}

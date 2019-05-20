package org.scribble.ext.assrt.ast;

import org.antlr.runtime.Token;
import org.scribble.ast.PayElem;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.name.qualified.DataNameNode;
import org.scribble.core.type.kind.DataKind;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataName;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

// A "name pair", perhaps similar to GDelegationElem -- factor out?
// This is an "Elem" -- "Elems" are the elements of PayElemList, while PayElemNameNode (like DataNameNode) are the values (an attribute) of the elems
// FIXME: currently only allowed to be "int" (cf. AssrtIntVarNameNode) -- check this explicitly
public class AssrtAnnotDataElem extends ScribNodeBase
		implements PayElem<DataKind>, AssrtActionVarDeclNode
{
	public static final int VAR_CHILD_INDEX = 0;
	public static final int DATA_CHILD_INDEX = 1;
	
	// ScribTreeAdaptor#create constructor
	public AssrtAnnotDataElem(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	protected AssrtAnnotDataElem(AssrtAnnotDataElem node)
	{
		super(node);
	}
	
	public AssrtIntVarNameNode getVarNameChild()
	{
		return (AssrtIntVarNameNode) getChild(VAR_CHILD_INDEX);
	}
	
	public DataNameNode getDataNameChild()
	{
		return (DataNameNode) getChild(DATA_CHILD_INDEX);
	}

	// "add", not "set"
	public void addScribChildren(AssrtIntVarNameNode var, DataNameNode data)
	{
		// Cf. above getters and Scribble.g children order
		addChild(var);
		addChild(data);
	}
	
	@Override
	public AssrtAnnotDataElem dupNode()
	{
		return new AssrtAnnotDataElem(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtAnnotDataElem(this);
	}
	
	public AssrtAnnotDataElem reconstruct(AssrtIntVarNameNode var,
			DataNameNode data)
	{
		AssrtAnnotDataElem dup = dupNode();
		dup.addScribChildren(var, data);
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override 
	public AssrtAnnotDataElem visitChildren(AstVisitor v) throws ScribException
	{
		AssrtIntVarNameNode var = (AssrtIntVarNameNode) visitChild(
				getVarNameChild(), v);
		DataNameNode data = (DataNameNode) visitChild(getDataNameChild(), v);
		return reconstruct(var, data);
	}

	@Override
	public AssrtAnnotDataName toPayloadType()
	{
		// TODO: make it PayloadType AnnotPayload  // CHECKME: means return just the data type?  but maybe the var is needed
		return new AssrtAnnotDataName(getVarNameChild().toName(),
				getDataNameChild().toPayloadType());
	}

	@Override
	public AssrtIntVarNameNode getAnnotVar()
	{
		return getVarNameChild();
	}
	
	@Override
	public String toString()
	{
		return getVarNameChild().toString() + ": " + getDataNameChild().toString();
	}
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*public final AssrtIntVarNameNode var;  // Using AssrtVarNameNode both as the annotation (as here), and as a PayloadElemNameNode -- like the below DataTypeNode
	public final DataNameNode data;  // FIXME: currently only "int"
	
	public AssrtAnnotDataElem(CommonTree source, AssrtIntVarNameNode var,
			DataNameNode data)
	{
		super(source);
		this.var = var;
		this.data = data; 
	}*/


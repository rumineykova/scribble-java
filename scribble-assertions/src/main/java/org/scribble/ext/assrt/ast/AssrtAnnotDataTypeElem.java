package org.scribble.ext.assrt.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.ext.assrt.sesstype.name.AssrtAnnotDataType;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

// A "name pair", perhaps similar to GDelegationElem -- factor out?
// This is an "Elem" -- "Elems" are the elements of PayloadElemList, while PayloadElemNameNode (like DataTypeNode) are the values (an attribute) of the elems
public class AssrtAnnotDataTypeElem<K extends PayloadTypeKind> extends ScribNodeBase implements PayloadElem<K>
{
	public final AssrtVarNameNode var;  // Using AssrtVarNameNode both as the annotation (as here), and as a PayloadElemNameNode -- like the below DataTypeNode
	public final DataTypeNode data;
	
	public AssrtAnnotDataTypeElem(CommonTree source, AssrtVarNameNode var, DataTypeNode data)
	{
		super(source);
		this.var = var;
		this.data = data; 
	}
	
	@Override
	public AssrtAnnotDataTypeElem<K> project(AstFactory af)
	{
		return this;
	}

	@Override
	protected AssrtAnnotDataTypeElem<K> copy()
	{
		return new AssrtAnnotDataTypeElem<>(this.source, this.var, this.data);
	}
	
	@Override
	public AssrtAnnotDataTypeElem<K> clone(AstFactory af)
	{
		AssrtVarNameNode var = ScribUtil.checkNodeClassEquality(this.var, this.var.clone(af));
		DataTypeNode data = ScribUtil.checkNodeClassEquality(this.data, this.data.clone(af));
		return ((AssrtAstFactory) af).AssrtAnnotDataTypeElem(this.source, var, data);
	}

	public AssrtAnnotDataTypeElem<K> reconstruct(AssrtVarNameNode var, DataTypeNode data)
	{
		ScribDel del = del();
		AssrtAnnotDataTypeElem<K> elem = new AssrtAnnotDataTypeElem<>(this.source, var, data);
		elem = ScribNodeBase.del(elem, del);
		return elem;
	}

	@Override 
	public AssrtAnnotDataTypeElem<K> visitChildren(AstVisitor nv) throws ScribbleException
	{
		AssrtVarNameNode var = (AssrtVarNameNode) visitChild(this.var, nv);  
		DataTypeNode data = (DataTypeNode) visitChild(this.data, nv);
		return reconstruct(var, data);
	}
	
	@Override
	public String toString()
	{
		return this.var.toString() + ": " +  this.data.toString();
	}

	@Override
	public AssrtAnnotDataType toPayloadType()
	{
		// TODO: make it PayloadType AnnotPayload  // FIXME: means return just the data type?  but maybe the var is needed
		return new AssrtAnnotDataType(this.var.toPayloadType(), this.data.toPayloadType());
	}
}

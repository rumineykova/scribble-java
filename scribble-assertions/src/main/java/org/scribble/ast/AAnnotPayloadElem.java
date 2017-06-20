package org.scribble.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.AVarNameNode;
import org.scribble.del.ScribDel;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.AAnnotPayload;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

// A "name pair", perhaps similar to GDelegationElem -- factor out?
public class AAnnotPayloadElem<K extends PayloadTypeKind> extends ScribNodeBase implements PayloadElem<K>
{
	public final AVarNameNode varName;
	public final DataTypeNode dataType;
	
	public AAnnotPayloadElem(CommonTree source, AVarNameNode varname, DataTypeNode dataType)
	{
		super(source);
		this.varName = varname;
		this.dataType = dataType; 
	}
	
	@Override
	public AAnnotPayloadElem<K> project()
	{
		return this;
	}

	@Override
	protected AAnnotPayloadElem<K> copy()
	{
		return new AAnnotPayloadElem<>(this.source, this.varName, this.dataType);
	}
	
	@Override
	public AAnnotPayloadElem<K> clone()
	{
		AVarNameNode varname = ScribUtil.checkNodeClassEquality(this.varName, this.varName.clone());
		DataTypeNode datatype = ScribUtil.checkNodeClassEquality(this.dataType, this.dataType.clone());
		return AAstFactoryImpl.FACTORY.AnnotPayloadElem(this.source, varname, datatype);
	}

	public AAnnotPayloadElem<K> reconstruct(AVarNameNode name, DataTypeNode dataType)
	{
		ScribDel del = del();
		AAnnotPayloadElem<K> elem = new AAnnotPayloadElem<>(this.source, name, dataType);
		elem = ScribNodeBase.del(elem, del);
		return elem;
	}

	@Override 
	public AAnnotPayloadElem<K> visitChildren(AstVisitor nv) throws ScribbleException
	{
		AVarNameNode varName = (AVarNameNode) visitChild(this.varName, nv);  
		DataTypeNode dataType = (DataTypeNode) visitChild(this.dataType, nv);
		return reconstruct(varName, dataType);
	}
	
	@Override
	public String toString()
	{
		return this.varName.toString() + ' ' +  this.dataType.toString();
	}

	@Override
	public AAnnotPayload toPayloadType()
	{
		// TODO: make it PayloadType AnnotPayload  // FIXME: means return the actual payload type?
		return new AAnnotPayload(this.varName.toPayloadType(), this.dataType.toPayloadType());
	}
}

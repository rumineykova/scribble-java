/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.ext.assrt.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.ext.assrt.sesstype.AssrtAnnotDataType;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

// A "name pair", perhaps similar to GDelegationElem -- factor out?
public class AssrtAnnotDataTypeElem<K extends PayloadTypeKind> extends ScribNodeBase implements PayloadElem<K>
{
	// "Elems" are the elements of PayloadElemList, while PayloadElemNameNode (like DataTypeNode) are the values (an attribute) of the elems
	public final AssrtVarNameNode varName;  // Using AssrtVarNameNode both as the annotation (as here), and as a PayloadElemNameNode -- like the below DataTypeNode
	public final DataTypeNode dataType;
	
	public AssrtAnnotDataTypeElem(CommonTree source, AssrtVarNameNode varname, DataTypeNode dataType)
	{
		super(source);
		this.varName = varname;
		this.dataType = dataType; 
	}
	
	@Override
	public AssrtAnnotDataTypeElem<K> project(AstFactory af)
	{
		return this;
	}

	@Override
	protected AssrtAnnotDataTypeElem<K> copy()
	{
		return new AssrtAnnotDataTypeElem<>(this.source, this.varName, this.dataType);
	}
	
	@Override
	public AssrtAnnotDataTypeElem<K> clone(AstFactory af)
	{
		AssrtVarNameNode varname = ScribUtil.checkNodeClassEquality(this.varName, this.varName.clone(af));
		DataTypeNode datatype = ScribUtil.checkNodeClassEquality(this.dataType, this.dataType.clone(af));
		return ((AssrtAstFactory) af).AssrtAnnotPayloadElem(this.source, varname, datatype);
	}

	public AssrtAnnotDataTypeElem<K> reconstruct(AssrtVarNameNode name, DataTypeNode dataType)
	{
		ScribDel del = del();
		AssrtAnnotDataTypeElem<K> elem = new AssrtAnnotDataTypeElem<>(this.source, name, dataType);
		elem = ScribNodeBase.del(elem, del);
		return elem;
	}

	@Override 
	public AssrtAnnotDataTypeElem<K> visitChildren(AstVisitor nv) throws ScribbleException
	{
		AssrtVarNameNode varName = (AssrtVarNameNode) visitChild(this.varName, nv);  
		DataTypeNode dataType = (DataTypeNode) visitChild(this.dataType, nv);
		return reconstruct(varName, dataType);
	}
	
	@Override
	public String toString()
	{
		return this.varName.toString() + ' ' +  this.dataType.toString();
	}

	@Override
	public AssrtAnnotDataType toPayloadType()
	{
		// TODO: make it PayloadType AnnotPayload  // FIXME: means return just the data type?  but maybe the var is needed
		return new AssrtAnnotDataType(this.varName.toPayloadType(), this.dataType.toPayloadType());
	}
}

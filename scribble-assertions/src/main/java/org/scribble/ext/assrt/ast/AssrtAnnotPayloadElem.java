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
import org.scribble.ext.assrt.sesstype.AssrtAnnotPayload;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.util.ScribUtil;
import org.scribble.visit.AstVisitor;

// A "name pair", perhaps similar to GDelegationElem -- factor out?
public class AssrtAnnotPayloadElem<K extends PayloadTypeKind> extends ScribNodeBase implements PayloadElem<K>
{
	public final AssrtVarNameNode varName;
	public final DataTypeNode dataType;
	
	public AssrtAnnotPayloadElem(CommonTree source, AssrtVarNameNode varname, DataTypeNode dataType)
	{
		super(source);
		this.varName = varname;
		this.dataType = dataType; 
	}
	
	@Override
	public AssrtAnnotPayloadElem<K> project()
	{
		return this;
	}

	@Override
	protected AssrtAnnotPayloadElem<K> copy()
	{
		return new AssrtAnnotPayloadElem<>(this.source, this.varName, this.dataType);
	}
	
	@Override
	public AssrtAnnotPayloadElem<K> clone(AstFactory af)
	{
		AssrtVarNameNode varname = ScribUtil.checkNodeClassEquality(this.varName, this.varName.clone(af));
		DataTypeNode datatype = ScribUtil.checkNodeClassEquality(this.dataType, this.dataType.clone(af));
		return AssrtAstFactoryImpl.FACTORY.AnnotPayloadElem(this.source, varname, datatype);
	}

	public AssrtAnnotPayloadElem<K> reconstruct(AssrtVarNameNode name, DataTypeNode dataType)
	{
		ScribDel del = del();
		AssrtAnnotPayloadElem<K> elem = new AssrtAnnotPayloadElem<>(this.source, name, dataType);
		elem = ScribNodeBase.del(elem, del);
		return elem;
	}

	@Override 
	public AssrtAnnotPayloadElem<K> visitChildren(AstVisitor nv) throws ScribbleException
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
	public AssrtAnnotPayload toPayloadType()
	{
		// TODO: make it PayloadType AnnotPayload  // FIXME: means return the actual payload type?
		return new AssrtAnnotPayload(this.varName.toPayloadType(), this.dataType.toPayloadType());
	}
}

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
package org.scribble.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.name.qualified.MemberNameNode;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.NonRoleArgKind;
import org.scribble.type.name.MemberName;
import org.scribble.visit.AstVisitor;

// Rename to something better -- one characteristic is both data and sigs are "typed" using their names
public abstract class DataOrSigDeclNode<K extends NonRoleArgKind> extends NameDeclNode<K> implements ModuleMember
{
	public final String schema;
	public final String extName;
	public final String extSource;

	public DataOrSigDeclNode(CommonTree source, String schema, String extName, String extSource, MemberNameNode<K> name)
	{
		super(source, name);
		this.schema = schema;
		this.extName = extName;
		this.extSource = extSource;
	}
	
	public abstract DataOrSigDeclNode<K> reconstruct(String schema, String extName, String source, MemberNameNode<K> name);

	@Override
	public DataOrSigDeclNode<K> visitChildren(AstVisitor nv) throws ScribbleException
	{
		MemberNameNode<K> name = (MemberNameNode<K>) visitChildWithClassEqualityCheck(this, this.name, nv);
		return reconstruct(this.schema, this.extName, this.extSource, name);
	}
	
	// Maybe should be moved to ModuleMember
	public boolean isDataTypeDecl()
	{
		return false;
	}

	public boolean isMessageSigNameDecl()
	{
		return false;
	}
	
	@Override
	public MemberNameNode<K> getNameNode()
	{
		return (MemberNameNode<K>) super.getNameNode();
	}

	@Override
	public MemberName<K> getDeclName()
	{
		return (MemberName<K>) super.getDeclName();  // Simple name -- not consistent with ModuleDecl
	}
}

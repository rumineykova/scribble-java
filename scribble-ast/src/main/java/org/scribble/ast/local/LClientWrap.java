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
package org.scribble.ast.local;

import org.antlr.runtime.Token;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.DelFactory;
import org.scribble.util.Constants;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

public class LClientWrap extends LWrapAction
		implements LSimpleSessionNode
{
	// ScribTreeAdaptor#create constructor
	public LClientWrap(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public LClientWrap(LClientWrap node)
	{
		super(node);
	}

	// CHECKME: factor out implementations with LSend as default methods 
	@Override
	public RoleNode getSelfChild()
	{
		return getClientChild();
	}

	@Override
	public RoleNode getPeerChild()
	{
		return getServerChild();  // Multi-connect/req not supported
	}
	
	@Override
	public LClientWrap dupNode()
	{
		return new LClientWrap(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		df.LClientWrap(this);
	}

	// TODO: factor out a base
	public LClientWrap reconstruct(RoleNode self, RoleNode peer)
	{
		LClientWrap n = dupNode();
		n.addScribChildren(self, peer);
		n.setDel(del());  // No copy
		return n;
	}

	// TODO: factor out a base
	@Override
	public LClientWrap visitChildren(AstVisitor v) throws ScribException
	{
		RoleNode self = (RoleNode) visitChild(getSelfChild(), v);
		RoleNode peer = (RoleNode) visitChild(getPeerChild(), v);
		return reconstruct(self, peer);
	}

	@Override
	public String toString()
	{
		return Constants.CLIENTWRAP_KW + getPeerChild() + ";";
	}
}

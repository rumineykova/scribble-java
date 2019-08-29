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

// TODO: factor out base Wrap (cf. Disconnect)
public class LServerWrap extends LWrapAction
		implements LSimpleSessionNode
{
	// ScribTreeAdaptor#create constructor
	public LServerWrap(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public LServerWrap(LServerWrap node)
	{
		super(node);
	}

	// CHECKME: factor out implementations with LRecv as default methods 
	@Override
	public RoleNode getSelfChild()
	{
		return getServerChild();  // CHECKME: don't use common src/dst pattern between global/local?
	}

	@Override
	public RoleNode getPeerChild()
	{
		return getClientChild();
	}
	
	@Override
	public LServerWrap dupNode()
	{
		return new LServerWrap(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		df.LServerWrap(this);
	}

	// TODO: factor out a base
	public LServerWrap reconstruct(RoleNode peer, RoleNode self)
	{
		LServerWrap n = dupNode();
		n.addScribChildren(peer, self);
		n.setDel(del());  // No copy
		return n;
	}

	// TODO: factor out a base
	@Override
	public LServerWrap visitChildren(AstVisitor v) throws ScribException
	{
		RoleNode peer = (RoleNode) visitChild(getPeerChild(), v);
		RoleNode self = (RoleNode) visitChild(getSelfChild(), v);
		return reconstruct(peer, self);
	}

	@Override
	public String toString()
	{
		return Constants.SERVERWRAP_KW + " " + getPeerChild() + ";";
	}
}

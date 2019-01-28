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
package org.scribble.ast.name.simple;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.DoArgNode;
import org.scribble.del.ScribDel;
import org.scribble.type.kind.RoleKind;
import org.scribble.type.name.Role;
import org.scribble.visit.Substitutor;

public class RoleNode extends SimpleNameNode<RoleKind> implements DoArgNode //RoleDecl, RoleInstantiation
{
	// ScribTreeAdaptor#create constructor
	public RoleNode(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	protected RoleNode(RoleNode node, String id)
	{
		super(node, id);
	}
	
	@Override
	public RoleNode dupNode()
	{
		return new RoleNode(this, getIdentifier());
	}

	// RoleNode is the only NameNode with a reconstruct (so not factored up)
	protected RoleNode reconstruct(String id)
	{
		RoleNode r = dupNode();
		ScribDel del = del(); // Default delegate assigned in ModelFactoryImpl for all simple names
		r.setDel(del);  // No copy
		return r;
	}
	
	@Override
	public RoleNode clone()
	{
		return (RoleNode) clone();
	}
	
	@Override
	public RoleNode substituteNames(Substitutor subs)
	{
		// Reconstruct: subprotocolvisitor uses a fullycloned body (and getRoleSubstitution also returns a clone) -- here rename the clone but keep the fresh dels/envs
		return reconstruct(subs.getRoleSubstitution(toName()).toName().toString());
	}
	
	@Override
	public Role toName()
	{
		return new Role(getIdentifier());
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof RoleNode))
		{
			return false;
		}
		return ((RoleNode) o).canEqual(this) && super.equals(o);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof RoleNode;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 353;
		hash = 31 * super.hashCode();
		return hash;
	}

	
	
	
	
	
	
	
	
	
	
	
	public RoleNode(CommonTree source, String identifier)
	{
		super(source, identifier);
	}

	/*@Override
	protected RoleNode copy()
	{
		return new RoleNode(this.source, getIdentifier());
	}
	
	@Override
	public RoleNode clone(AstFactory af)
	{
		return (RoleNode) af.SimpleNameNode(this.source, RoleKind.KIND, getIdentifier());
	}*/
}

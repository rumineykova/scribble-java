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

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.name.NameNode;
import org.scribble.ast.name.PayloadElemNameNode;
import org.scribble.sesstype.Arg;
import org.scribble.sesstype.kind.AAnnotVarNameKind;
import org.scribble.sesstype.kind.NonRoleArgKind;
import org.scribble.sesstype.name.AAnnotVarName;

public class AVarNameNode extends SimpleNameNode<AAnnotVarNameKind> implements PayloadElemNameNode<AAnnotVarNameKind>
{
	public AVarNameNode(CommonTree source, String identifier)
	{
		super(source, identifier);
	}

	@Override
	protected ScribNodeBase copy()
	{
		return new AVarNameNode(this.source, getIdentifier());
	}

	@Override
	public NameNode<AAnnotVarNameKind> clone()
	{
		return (AVarNameNode) AstFactoryImpl.FACTORY.SimpleNameNode(this.source, AAnnotVarNameKind.KIND, getIdentifier());
	}

	@Override
	public AAnnotVarName toName() {
		return new AAnnotVarName(getIdentifier());
	}
	
	@Override
	public boolean equals(Object o)  // FIXME: is equals/hashCode needed for these Nodes?
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AVarNameNode))
		{
			return false;
		}
		return ((AVarNameNode) o).canEqual(this) && super.equals(o);
	}
	
	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AVarNameNode;
	}

	@Override
	public int hashCode()
	{
		int hash = 967;
		hash = 31 * super.hashCode();
		return hash;
	}

	@Override
	public Arg<? extends NonRoleArgKind> toArg()
	{
		throw new RuntimeException("[scrib-assert] TODO: var name node as do-arg: " + this);  // TODO?
	}

	@Override
	public AAnnotVarName toPayloadType()
	{
		return toName();  // FIXME: Shoudln't this be the type, not the var name?
	}
}

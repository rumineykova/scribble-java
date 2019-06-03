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

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.Token;
import org.scribble.ast.ParamDeclList;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.core.type.kind.AssrtIntVarKind;
import org.scribble.ext.assrt.core.type.name.AssrtIntVar;
import org.scribble.ext.assrt.del.AssrtDelFactory;


public class AssrtStateVarDeclList extends ParamDeclList<AssrtIntVarKind>
{
	// ScribTreeAdaptor#create constructor
	public AssrtStateVarDeclList(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public AssrtStateVarDeclList(AssrtStateVarDeclList node)
	{
		super(node);
	}
	
	@Override
	public List<AssrtStateVarDecl> getDeclChildren()
	{
		return ((List<?>) getChildren()).stream().map(x -> (AssrtStateVarDecl) x)
				.collect(Collectors.toList());
	}

	public List<AssrtIntVar> getRoles()
	{
		return getDeclChildren().stream().map(x -> x.getDeclName())
				.collect(Collectors.toList());
	}
	
	@Override
	public AssrtStateVarDeclList dupNode()
	{
		return new AssrtStateVarDeclList(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtStateVarDeclList(this);
	}

	@Override
	public String toString()
	{
		return "<" + super.toString() + ">";
	}
}

/*public abstract class AssrtStateVarDeclList extends ScribNodeBase 
{
	// ScribTreeAdaptor#create constructor
	public AssrtStateVarDeclList(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public AssrtStateVarDeclList(AssrtStateVarDeclList node)
	{
		super(node);
	}
	
	public abstract List<? extends AssrtStateVarDecl> getDeclChildren();

	// "add", not "set"
	public void addScribChildren(List<? extends AssrtStateVarDecl> ds)
	{
		// Cf. above getters and Scribble.g children order
		super.addChildren(ds);
	}
	
	@Override
	public abstract AssrtStateVarDeclList dupNode();
	
	public AssrtStateVarDeclList reconstruct(
			List<? extends AssrtStateVarDecl> ds)
	{
		AssrtStateVarDeclList dup = dupNode();
		dup.addScribChildren(ds);
		dup.setDel(del());  // No copy
		return dup;
	}
	
	@Override
	public AssrtStateVarDeclList visitChildren(AstVisitor v)
			throws ScribException
	{
		List<? extends AssrtStateVarDecl> ps = 
				visitChildListWithClassEqualityCheck(this, getDeclChildren(), v);
		return reconstruct(ps);
	}
		
	public final int length()
	{
		return getDeclChildren().size();
	}

	public final boolean isEmpty()
	{
		return length() == 0;
	}

	// Without enclosing braces -- added by subclasses
	@Override
	public String toString()
	{
		return getDeclChildren().stream().map(x -> x.toString())
				.collect(Collectors.joining(", "));
	}
}*/

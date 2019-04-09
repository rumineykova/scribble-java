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

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.scribble.del.ScribDel;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;
import org.scribble.visit.SimpleAstVisitor;

/**
 * This is the generic object from which all Scribble AST objects
 * are derived.
 */
public interface ScribNode extends Tree
{
	@Override
	ScribNode getParent();

	//@Override -- super return isn't generic
	List<ScribNode> getChildren();

	//void setChildren(List<ScribNode> children);  // protected

  // Returns a deep clone but with fresh dels (i.e. dels not copied) -- use the af to build with fresh dels -- XXX
	// i.e. recursively using AstFactory to rebuild the whole subtree
	// Cf. node specific reconstructs, retain (i.e. share) the existing del -- so dels must be immutable (except for Envs)
	//ScribNode clone(AstFactory af);

	ScribNode clone();

	ScribDel del();
	ScribNode del(ScribDel del);
	
	default <T> T visitWith(SimpleAstVisitor<T> v) throws ScribException  // "Top-level" visitor entry method
	{
		return v.visit(this);  // N.B. ScribNode has getParent
	}

	CommonTree getSource();  
			// Previously: for parsed entities, null if not parsed
			// Now: for the original parsed entity for error blaming; should not be null unless a "purely generated" entity

	ScribNode accept(AstVisitor nv) throws ScribException;  // The "top-level" method, e.g., module.accept(v)  (cf. Job::runVisitorOnModule)
	ScribNode visitChildren(AstVisitor nv) throws ScribException;
}

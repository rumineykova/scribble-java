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
package org.scribble.ext.assrt.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.global.GProtocolHeader;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ast.name.qualified.ProtocolNameNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.type.formula.AssrtBinCompFormula;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Global;
import org.scribble.visit.AstVisitor;

public class AssrtGProtocolHeader extends GProtocolHeader
{
	public final AssrtAssertion ass;  // null if not specified -- currently duplicated from AssrtGMessageTransfer
			// FIXME: ass.getFormula() is restricted by AssrtAntlrGProtocolHeader to a top-level "x = expr" (integer) equality expr, to stand for a var decl initialiser-expr
			// FIXME: make a distinct category from interaction assertions -- and fix to int vars?

	public AssrtGProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls)
	{
		this(source, name, roledecls, paramdecls, null);
	}

	public AssrtGProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, AssrtAssertion ass)
	{
		super(source, name, roledecls, paramdecls);
		this.ass = ass;
	}
	
	// FIXME: define restrictions directly in ANTLR grammar, and make a separate AST class for protocol header var init-decl annotations
	public AssrtBinCompFormula getAnnotDataTypeVarInitDecl()  // Cf. AssrtAnnotDataTypeElem (no "initializer")
	{
		return (AssrtBinCompFormula) this.ass.getFormula();
	}

	@Override
	protected ScribNodeBase copy()
	{
		return new AssrtGProtocolHeader(this.source, getNameNode(), this.roledecls, this.paramdecls, this.ass);
	}
	
	@Override
	public AssrtGProtocolHeader clone(AstFactory af)
	{
		GProtocolNameNode name = getNameNode().clone(af);
		RoleDeclList roledecls = this.roledecls.clone(af);
		NonRoleParamDeclList paramdecls = this.paramdecls.clone(af);
		AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);
		return ((AssrtAstFactory) af).AssrtGProtocolHeader(this.source, name, roledecls, paramdecls, ass);
	}

	@Override
	public AssrtGProtocolHeader reconstruct(ProtocolNameNode<Global> name, RoleDeclList rdl, NonRoleParamDeclList pdl)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtGProtocolHeader reconstruct(ProtocolNameNode<Global> name, RoleDeclList rdl, NonRoleParamDeclList pdl, AssrtAssertion ass)
	{
		ScribDel del = del();
		AssrtGProtocolHeader gph = new AssrtGProtocolHeader(this.source, (GProtocolNameNode) name, rdl, pdl, ass);
		gph = (AssrtGProtocolHeader) gph.del(del);
		return gph;
	}
	
	@Override
	public GProtocolHeader visitChildren(AstVisitor nv) throws ScribbleException
	{
		RoleDeclList rdl = (RoleDeclList) visitChild(this.roledecls, nv);
		NonRoleParamDeclList pdl = (NonRoleParamDeclList) visitChild(this.paramdecls, nv);
		AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);
		return reconstruct((GProtocolNameNode) this.name, rdl, pdl, ass);
	}
	
	@Override
	public String toString()
	{
		return super.toString() + " @" + this.ass;
	}
	
	/*// FIXME: shouldn't be needed, but here due to Eclipse bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=436350
	@Override
	public Global getKind()
	{
		return GNode.super.getKind();
	}*/
}

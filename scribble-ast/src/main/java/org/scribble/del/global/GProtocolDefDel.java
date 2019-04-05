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
package org.scribble.del.global;

import java.util.Arrays;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.ProtocolDef;
import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GInteractionSeq;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ast.global.GProtocolDef;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.local.LProtocolDef;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.core.lang.SubprotoSig;
import org.scribble.core.type.kind.ProtocolKind;
import org.scribble.core.type.kind.RecVarKind;
import org.scribble.del.ProtocolDefDel;
import org.scribble.del.ScribDelBase;
import org.scribble.util.ScribException;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.context.Projector;
import org.scribble.visit.context.env.ProjectionEnv;
import org.scribble.visit.env.InlineProtocolEnv;

public class GProtocolDefDel extends ProtocolDefDel
{
	public GProtocolDefDel()
	{

	}

	@Override
	protected GProtocolDefDel copy()
	{
		GProtocolDefDel copy = new GProtocolDefDel();
		copy.inlined = this.inlined;
		return copy;
	}

	@Override
	public ScribNode leaveProtocolInlining(ScribNode parent, ScribNode child,
			ProtocolDefInliner inl, ScribNode visited) throws ScribException
	{
		CommonTree blame = ((GProtocolDecl) parent).getHeaderChild().getSource();
		SubprotoSig subsig = inl.peekStack();
		GProtocolDef def = (GProtocolDef) visited;
		GProtocolBlock block = (GProtocolBlock) ((InlineProtocolEnv) def
				.getBlockChild().del().env()).getTranslation();
		
		System.out.println("ccc1: " + child);
		System.out.println("ccc2: " + visited);
		//...HERE: update AF to ANTLR nodes
		
		RecVarNode recvar = (RecVarNode) inl.lang.config.af.SimpleNameNode(blame,
				RecVarKind.KIND, inl.getSubprotocolRecVar(subsig).toString());
		GRecursion rec = inl.lang.config.af.GRecursion(blame, recvar, block);
		GInteractionSeq gis = inl.lang.config.af.GInteractionSeq(blame, Arrays.asList(rec));
		GProtocolDef inlined = inl.lang.config.af.GProtocolDef(def.getSource(),
				inl.lang.config.af.GProtocolBlock(blame, gis));
		inl.pushEnv(inl.popEnv().setTranslation(inlined));
		GProtocolDefDel copy = setInlinedProtocolDef(inlined);
		return (GProtocolDef) ScribDelBase.popAndSetVisitorEnv(this, inl,
				(GProtocolDef) def.del(copy));
	}

	@Override
	public void enterProjection(ScribNode parent, ScribNode child, Projector proj)
			throws ScribException
	{
		//pushVisitorEnv(parent, child, proj);
		ScribDelBase.pushVisitorEnv(this, proj);
	}

	@Override
	public GProtocolDef leaveProjection(ScribNode parent, ScribNode child,
			Projector proj, ScribNode visited) throws ScribException
	{
		GProtocolDef gpd = (GProtocolDef) visited;
		LProtocolBlock block = (LProtocolBlock) ((ProjectionEnv) gpd.getBlockChild()
				.del().env()).getProjection();
		LProtocolDef projection = 
				gpd.project(proj.lang.config.af, proj.peekSelf(), block);
		proj.pushEnv(proj.popEnv().setProjection(projection));
		return (GProtocolDef) ScribDelBase.popAndSetVisitorEnv(this, proj, gpd);
	}
	
	@Override
	public GProtocolDef getInlinedProtocolDef()
	{
		return (GProtocolDef) super.getInlinedProtocolDef();
	}

	@Override
	public GProtocolDefDel setInlinedProtocolDef(ProtocolDef<? extends ProtocolKind> inlined)
	{
		return (GProtocolDefDel) super.setInlinedProtocolDef(inlined);
	}
}

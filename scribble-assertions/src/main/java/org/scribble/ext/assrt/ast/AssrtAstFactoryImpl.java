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

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GChoice;
import org.scribble.ast.global.GInteractionSeq;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.local.LInteractionSeq;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.local.LSend;
import org.scribble.ast.name.NameNode;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.AmbigNameNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.formula.SmtFormula;
import org.scribble.ext.assrt.ast.global.AssrtGMessageTransfer;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.ext.assrt.del.AssrtAnnotDataTypeElemDel;
import org.scribble.ext.assrt.del.global.AssrtGChoiceDel;
import org.scribble.ext.assrt.del.global.AssrtGMessageTransferDel;
import org.scribble.ext.assrt.del.global.AssrtGProtocolBlockDel;
import org.scribble.ext.assrt.del.global.AssrtGRecursionDel;
import org.scribble.ext.assrt.del.local.AssrtLProtocolBlockDel;
import org.scribble.ext.assrt.del.local.AssrtLSendDel;
import org.scribble.ext.assrt.del.name.AssrtAmbigNameNodeDel;
import org.scribble.ext.assrt.sesstype.kind.AssrtVarNameKind;
import org.scribble.sesstype.kind.Kind;
import org.scribble.sesstype.kind.PayloadTypeKind;


// FIXME: separate modified-del-only from new categories
public class AssrtAstFactoryImpl extends AstFactoryImpl implements AssrtAstFactory
{
	@Override
	public GProtocolBlock GProtocolBlock(CommonTree source, GInteractionSeq seq)
	{
		GProtocolBlock gpb = new GProtocolBlock(source, seq);
		gpb = del(gpb, new AssrtGProtocolBlockDel());
		return gpb;
	}

	// Non-annotated message transfers still created as AssrtGMessageTransfer -- null assertion, but AssrtGMessageTransferDel is still needed
	@Override
	public AssrtGMessageTransfer GMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		AssrtGMessageTransfer gmt = new AssrtGMessageTransfer(source, src, msg, dests);
		gmt = del(gmt, new AssrtGMessageTransferDel());
		return gmt;
	}

	@Override
	public AssrtGMessageTransfer AssrtGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion)
	{
		AssrtGMessageTransfer gmt = new AssrtGMessageTransfer(source, src, msg, dests, assertion);
		gmt = del(gmt, new AssrtGMessageTransferDel());
		return gmt;
	}

	@Override
	public GChoice GChoice(CommonTree source, RoleNode subj, List<GProtocolBlock> blocks)
	{
		GChoice gc = new GChoice(source, subj, blocks);
		gc = del(gc, new AssrtGChoiceDel());
		return gc;
	}

	@Override
	public GRecursion GRecursion(CommonTree source, RecVarNode recvar, GProtocolBlock block)
	{
		GRecursion gr = new GRecursion(source, recvar, block);
		gr = del(gr, new AssrtGRecursionDel());
		return gr;
	}

	@Override
	public AmbigNameNode AmbiguousNameNode(CommonTree source, String identifier)
	{
		AmbigNameNode ann = new AmbigNameNode(source, identifier); 
		ann = (AmbigNameNode) ann.del(new AssrtAmbigNameNodeDel());
		return ann;
	}

	// Cf. GMessageTransfer -- non-annotated sends still created as AssrtLSend -- null assertion, but AssrtLSendDel still needed
	@Override
	public LSend LSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		//LSend ls = new LSend(source, src, msg, dests);
		AssrtLSend ls = new AssrtLSend(source, src, msg, dests);
		ls = del(ls, new AssrtLSendDel());
		return ls;
	}

	@Override
	public AssrtLSend AssrtLSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion)
	{
		AssrtLSend ls = new AssrtLSend(source, src, msg, dests, assertion);
		ls = del(ls, new AssrtLSendDel());
		return ls;
	}
	
	// An "additional" category, does not "replace" an existing one -- cf. AssrtGMessageTransfer
	@Override
	public <K extends PayloadTypeKind> AssrtAnnotDataTypeElem<K> AssrtAnnotPayloadElem(CommonTree source, AssrtVarNameNode varName, DataTypeNode dataType)
	{
		AssrtAnnotDataTypeElem<K> de= new AssrtAnnotDataTypeElem<>(source, varName, dataType);
		de = del(de, new AssrtAnnotDataTypeElemDel());
		return de;
	}

	@Override
	//public AssrtAssertionNode AssertionNode(CommonTree source, String assertion)
	public AssrtAssertion AssrtAssertion(CommonTree source, SmtFormula f)
	{
		//AssrtAssertionNode node = new AssrtAssertionNode(source, assertion); 
		AssrtAssertion node = new AssrtAssertion(source, f); 
		node = del(node, createDefaultDelegate());
		return node; 
	}
	
	@Override
	public <K extends Kind> NameNode<K> SimpleNameNode(CommonTree source, K kind, String identifier)
	{
		if (kind.equals(AssrtVarNameKind.KIND))
		{
			NameNode<? extends Kind> snn = new AssrtVarNameNode(source, identifier);
			snn = del(snn, createDefaultDelegate()); 
			return castNameNode(kind, snn);
		}

		return super.SimpleNameNode(source, kind, identifier);
	}

	@Override
	public LProtocolBlock LProtocolBlock(CommonTree source, LInteractionSeq seq)
	{
		LProtocolBlock lpb = new LProtocolBlock(source, seq);
		lpb = del(lpb, new AssrtLProtocolBlockDel());
		return lpb;
	}

}

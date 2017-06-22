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
import org.scribble.ast.local.LSend;
import org.scribble.ast.name.NameNode;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.AmbigNameNode;
import org.scribble.ast.name.simple.OpNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.name.RecVarNodeDel;
import org.scribble.del.name.RoleNodeDel;
import org.scribble.ext.assrt.ast.global.AssrtGMessageTransfer;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.ext.assrt.del.AssrtAnnotPayloadElemDel;
import org.scribble.ext.assrt.del.global.AssrtGMessageTransferDel;
import org.scribble.ext.assrt.del.local.AssrtLSendDel;
import org.scribble.ext.assrt.del.name.AssrtAmbigNameNodeDel;
import org.scribble.ext.assrt.sesstype.kind.AssrtAnnotVarNameKind;
import org.scribble.sesstype.kind.Kind;
import org.scribble.sesstype.kind.OpKind;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.kind.RecVarKind;
import org.scribble.sesstype.kind.RoleKind;


public class AssrtAstFactoryImpl extends AstFactoryImpl implements AssrtAstFactory
{
	public static final AssrtAstFactory FACTORY = new AssrtAstFactoryImpl();

	@Override
	public AssrtGMessageTransfer GMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertionNode assertion)
	{
		AssrtGMessageTransfer gmt = new AssrtGMessageTransfer(source, src, msg, dests, assertion);
		gmt = del(gmt, new AssrtGMessageTransferDel());
		return gmt;
	}

	@Override
	public AmbigNameNode AmbiguousNameNode(CommonTree source, String identifier)
	{
		AmbigNameNode ann = new AmbigNameNode(source, identifier); 
		ann = (AmbigNameNode) ann.del(new AssrtAmbigNameNodeDel());
		return ann;
	}

	@Override
	public LSend LSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		//LSend ls = new LSend(source, src, msg, dests);
		AssrtLSend ls = new AssrtLSend(source, src, msg, dests);
		ls = del(ls, new AssrtLSendDel());
		return ls;
	}

	@Override
	public AssrtLSend LSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertionNode assertion)
	{
		AssrtLSend ls = new AssrtLSend(source, src, msg, dests, assertion);
		ls = del(ls, new AssrtLSendDel());
		return ls;
	}
	
	@Override
	public <K extends PayloadTypeKind> AssrtAnnotPayloadElem<K> AnnotPayloadElem(CommonTree source, AssrtVarNameNode varName, DataTypeNode dataType)
	{
		AssrtAnnotPayloadElem<K> de= new AssrtAnnotPayloadElem<>(source, varName, dataType);
		de = del(de, new AssrtAnnotPayloadElemDel());
		return de;
	}

	@Override
	public AssrtAssertionNode AssertionNode(CommonTree source, String assertion)
	{
		AssrtAssertionNode node = new AssrtAssertionNode(source, assertion); 
		node = del(node, createDefaultDelegate());
		return node; 
	}
	
	@Override
	public <K extends Kind> NameNode<K> SimpleNameNode(CommonTree source, K kind, String identifier)
	{
		NameNode<? extends Kind> snn = null;
		
		// Without delegates
		if (kind.equals(RecVarKind.KIND))
		{
			snn = new RecVarNode(source, identifier);
			snn = del(snn, new RecVarNodeDel());
		}
		else if (kind.equals(RoleKind.KIND))
		{
			snn = new RoleNode(source, identifier);
			snn = del(snn, new RoleNodeDel());
		}
		else if (kind.equals(AssrtAnnotVarNameKind.KIND))
		{
			snn = new AssrtVarNameNode(source, identifier);
			snn = del(snn, createDefaultDelegate()); 
		}
		if (snn != null)
		{
			return castNameNode(kind, snn);
		}

		// With delegates
		if (kind.equals(OpKind.KIND))
		{
			snn = new OpNode(source, identifier);
		}
		else
		{
			throw new RuntimeException("Shouldn't get in here: " + kind);
		}
		return castNameNode(kind, del(snn, createDefaultDelegate()));
	}
}

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
import org.scribble.ast.global.AGMessageTransfer;
import org.scribble.ast.local.ALSend;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.AVarNameNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.AAnnotPayloadElemDel;
import org.scribble.del.global.AGMessageTransferDel;
import org.scribble.del.local.ALSendDel;
import org.scribble.sesstype.kind.PayloadTypeKind;


public class AAstFactoryImpl extends AstFactoryImpl implements AAstFactory
{
	public static final AAstFactory FACTORY = new AAstFactoryImpl();
	
	@Override
	public AGMessageTransfer GMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		throw new RuntimeException("[scrib-assert] Shouldn't get in here: " + source);
	}

	@Override
	public AGMessageTransfer GMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode assertion)
	{
		AGMessageTransfer gmt = new AGMessageTransfer(source, src, msg, dests, assertion);
		gmt = del(gmt, new AGMessageTransferDel());
		return gmt;
	}

	@Override
	public ALSend LSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		throw new RuntimeException("[scrib-assert] Shouldn't get in here: " + source);
	}

	@Override
	public ALSend LSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AAssertionNode assertion)
	{
		ALSend ls = new ALSend(source, src, msg, dests, assertion);
		ls = del(ls, new ALSendDel());
		return ls;
	}
	
	@Override
	public <K extends PayloadTypeKind> AAnnotPayloadElem<K> AnnotPayloadElem(CommonTree source, AVarNameNode varName, DataTypeNode dataType)
	{
		AAnnotPayloadElem<K> de= new AAnnotPayloadElem<>(source, varName, dataType);
		de = del(de, new AAnnotPayloadElemDel());
		return de;
	}

	@Override
	public AAssertionNode AssertionNode(CommonTree source, String assertion)
	{
		AAssertionNode node = new AAssertionNode(source, assertion); 
		node = del(node, createDefaultDelegate());
		return node; 
	}
}

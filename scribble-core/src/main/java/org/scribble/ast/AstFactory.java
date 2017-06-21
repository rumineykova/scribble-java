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

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.global.GChoice;
import org.scribble.ast.global.GConnect;
import org.scribble.ast.global.GContinue;
import org.scribble.ast.global.GDelegationElem;
import org.scribble.ast.global.GDisconnect;
import org.scribble.ast.global.GDo;
import org.scribble.ast.global.GInteractionNode;
import org.scribble.ast.global.GInteractionSeq;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ast.global.GProtocolDef;
import org.scribble.ast.global.GProtocolHeader;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.global.GWrap;
import org.scribble.ast.local.LAccept;
import org.scribble.ast.local.LChoice;
import org.scribble.ast.local.LConnect;
import org.scribble.ast.local.LContinue;
import org.scribble.ast.local.LDelegationElem;
import org.scribble.ast.local.LDisconnect;
import org.scribble.ast.local.LDo;
import org.scribble.ast.local.LInteractionNode;
import org.scribble.ast.local.LInteractionSeq;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.local.LProtocolDecl;
import org.scribble.ast.local.LProtocolDef;
import org.scribble.ast.local.LProtocolHeader;
import org.scribble.ast.local.LReceive;
import org.scribble.ast.local.LRecursion;
import org.scribble.ast.local.LSend;
import org.scribble.ast.local.LWrapClient;
import org.scribble.ast.local.LWrapServer;
import org.scribble.ast.local.SelfRoleDecl;
import org.scribble.ast.name.NameNode;
import org.scribble.ast.name.PayloadElemNameNode;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.qualified.MessageSigNameNode;
import org.scribble.ast.name.qualified.ModuleNameNode;
import org.scribble.ast.name.qualified.QualifiedNameNode;
import org.scribble.ast.name.simple.AmbigNameNode;
import org.scribble.ast.name.simple.DummyProjectionRoleNode;
import org.scribble.ast.name.simple.NonRoleParamNode;
import org.scribble.ast.name.simple.OpNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.sesstype.kind.Kind;
import org.scribble.sesstype.kind.NonRoleParamKind;
import org.scribble.sesstype.kind.OpKind;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.name.GProtocolName;
import org.scribble.sesstype.name.Op;
import org.scribble.sesstype.name.Role;


public abstract class AstFactory
{
	private static MessageSigNode UNIT_MESSAGE_SIG_NODE;  // A "constant"
	
  // FIXME: inconsistent wrt. this.source -- it is essentially parsed (in the sense of *omitted* syntax), but not recorded
	// FIXME: this pattern is not ideal ("exposed" public constructor arg in GWrap/GDisconnect)
	//     An alternative would be to make subclasses, e.g., UnitMessageSigNode, UnitOp, EmptyPayloadElemList -- but a lot of extra classes
	protected MessageSigNode UnitMessageSigNode()
	{
		if (UNIT_MESSAGE_SIG_NODE == null)
		{
			UNIT_MESSAGE_SIG_NODE = MessageSigNode(null, (OpNode) SimpleNameNode(null, OpKind.KIND, Op.EMPTY_OPERATOR.toString()),
					PayloadElemList(null, Collections.emptyList()));  // Payload.EMPTY_PAYLOAD?
		}
		return UNIT_MESSAGE_SIG_NODE;
	}

	public abstract Module Module(CommonTree source, ModuleDecl moddecl, List<ImportDecl<?>> imports, List<NonProtocolDecl<?>> data, List<ProtocolDecl<?>> protos);
	
	public abstract MessageSigNode MessageSigNode(CommonTree source, OpNode op, PayloadElemList payload);

	//PayloadElemList PayloadElemList(List<PayloadElem<?>> payloadelems);
	public abstract PayloadElemList PayloadElemList(CommonTree source, List<PayloadElem<?>> payloadelems);
	//PayloadElem PayloadElem(PayloadElemNameNode name);
	//UnaryPayloadElem DataTypeElem(PayloadElemNameNode<DataTypeKind> name);
	//UnaryPayloadElem UnaryPayloadElem(PayloadElemNameNode<?> name);
	public abstract <K extends PayloadTypeKind> UnaryPayloadElem<K> UnaryPayloadElem(CommonTree source, PayloadElemNameNode<K> name);
	public abstract GDelegationElem GDelegationElem(CommonTree source, GProtocolNameNode name, RoleNode role);
	public abstract LDelegationElem LDelegationElem(CommonTree source, LProtocolNameNode name);

	public abstract ModuleDecl ModuleDecl(CommonTree source, ModuleNameNode fullmodname);
	public abstract ImportModule ImportModule(CommonTree source, ModuleNameNode modname, ModuleNameNode alias);
	
	public abstract MessageSigNameDecl MessageSigNameDecl(CommonTree source, String schema, String extName, String extSource, MessageSigNameNode name);
	public abstract DataTypeDecl DataTypeDecl(CommonTree source, String schema, String extName, String extSource, DataTypeNode name);

	public abstract GProtocolDecl GProtocolDecl(CommonTree source, List<ProtocolDecl.Modifiers> modifiers, GProtocolHeader header, GProtocolDef def);
	public abstract GProtocolHeader GProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls);

	public abstract RoleDeclList RoleDeclList(CommonTree source, List<RoleDecl> rds);
	public abstract RoleDecl RoleDecl(CommonTree source, RoleNode role);
	//ConnectDecl ConnectDecl(CommonTree source, RoleNode src, RoleNode role);
	public abstract NonRoleParamDeclList NonRoleParamDeclList(CommonTree source, List<NonRoleParamDecl<NonRoleParamKind>> pds);
	public abstract <K extends NonRoleParamKind> NonRoleParamDecl<K> NonRoleParamDecl(CommonTree source, K kind, NonRoleParamNode<K> name);
	
	public abstract GProtocolDef GProtocolDef(CommonTree source, GProtocolBlock block);
	public abstract GProtocolBlock GProtocolBlock(CommonTree source, GInteractionSeq gis);
	public abstract GInteractionSeq GInteractionSeq(CommonTree source, List<GInteractionNode> gis);

	public abstract GMessageTransfer GMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests);
	public abstract GConnect GConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest);
	//GConnect GConnect(CommonTree source, RoleNode src, RoleNode dest);
	public abstract GDisconnect GDisconnect(CommonTree source, RoleNode src, RoleNode dest);
	public abstract GWrap GWrap(CommonTree source, RoleNode src, RoleNode dest);
	public abstract GChoice GChoice(CommonTree source, RoleNode subj, List<GProtocolBlock> blocks);
	public abstract GRecursion GRecursion(CommonTree source, RecVarNode recvar, GProtocolBlock block);
	public abstract GContinue GContinue(CommonTree source, RecVarNode recvar);
	public abstract GDo GDo(CommonTree source, RoleArgList roles, NonRoleArgList args, GProtocolNameNode proto);
	
	public abstract RoleArgList RoleArgList(CommonTree source, List<RoleArg> roles);
	public abstract RoleArg RoleArg(CommonTree source, RoleNode role);
	public abstract NonRoleArgList NonRoleArgList(CommonTree source, List<NonRoleArg> args);
	public abstract NonRoleArg NonRoleArg(CommonTree source, NonRoleArgNode arg);

	public abstract <K extends Kind> NameNode<K> SimpleNameNode(CommonTree source, K kind, String identifier);
	public abstract <K extends Kind> QualifiedNameNode<K> QualifiedNameNode(CommonTree source, K kind, String... elems);
	
	public abstract AmbigNameNode AmbiguousNameNode(CommonTree source, String identifier);
	public abstract <K extends NonRoleParamKind> NonRoleParamNode<K> NonRoleParamNode(CommonTree source, K kind, String identifier);
	public abstract DummyProjectionRoleNode DummyProjectionRoleNode();

	public abstract LProtocolDecl LProtocolDecl(CommonTree source, List<ProtocolDecl.Modifiers> modifiers, LProtocolHeader header, LProtocolDef def);
	public abstract LProtocolHeader LProtocolHeader(CommonTree source, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls);
	public abstract SelfRoleDecl SelfRoleDecl(CommonTree source, RoleNode namenode);
	public abstract LProtocolDef LProtocolDef(CommonTree source, LProtocolBlock block);
	public abstract LProtocolBlock LProtocolBlock(CommonTree source, LInteractionSeq seq);
	public abstract LInteractionSeq LInteractionSeq(CommonTree source, List<LInteractionNode> actions);

	public abstract LSend LSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests);
	public abstract LReceive LReceive(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests);
	public abstract LConnect LConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest);
	public abstract LAccept LAccept(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest);
	/*LConnect LConnect(CommonTree source, RoleNode src, RoleNode dest);
	LAccept LAccept(CommonTree source, RoleNode src, RoleNode dest);*/
	public abstract LDisconnect LDisconnect(CommonTree source, RoleNode self, RoleNode peer);
	public abstract LWrapClient LWrapClient(CommonTree source, RoleNode self, RoleNode peer);
	public abstract LWrapServer LWrapServer(CommonTree source, RoleNode self, RoleNode peer);
	public abstract LChoice LChoice(CommonTree source, RoleNode subj, List<LProtocolBlock> blocks);
	public abstract LRecursion LRecursion(CommonTree source, RecVarNode recvar, LProtocolBlock block);
	public abstract LContinue LContinue(CommonTree source, RecVarNode recvar);
	public abstract LDo LDo(CommonTree source, RoleArgList roles, NonRoleArgList args, LProtocolNameNode proto);

	public abstract LProtocolDecl LProjectionDecl(CommonTree source, List<ProtocolDecl.Modifiers> modifiers, GProtocolName fullname, Role self, LProtocolHeader header, LProtocolDef def);  // del extends that of LProtocolDecl 
}

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
package org.scribble.ext.assrt.parser;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.scribble.ast.AuxMod;
import org.scribble.ast.DataDecl;
import org.scribble.ast.DataParamDecl;
import org.scribble.ast.ExplicitMod;
import org.scribble.ast.ImportModule;
import org.scribble.ast.Module;
import org.scribble.ast.ModuleDecl;
import org.scribble.ast.NonRoleArg;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.PayElemList;
import org.scribble.ast.ProtoModList;
import org.scribble.ast.RoleArg;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.RoleDecl;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.ScribNil;
import org.scribble.ast.ScribNode;
import org.scribble.ast.ScribNodeBase;
import org.scribble.ast.SigDecl;
import org.scribble.ast.SigLitNode;
import org.scribble.ast.SigParamDecl;
import org.scribble.ast.UnaryPayElem;
import org.scribble.ast.global.GChoice;
import org.scribble.ast.global.GConnect;
import org.scribble.ast.global.GContinue;
import org.scribble.ast.global.GDelegPayElem;
import org.scribble.ast.global.GDisconnect;
import org.scribble.ast.global.GDo;
import org.scribble.ast.global.GInteractionSeq;
import org.scribble.ast.global.GMsgTransfer;
import org.scribble.ast.global.GProtoBlock;
import org.scribble.ast.global.GProtoDecl;
import org.scribble.ast.global.GProtoDef;
import org.scribble.ast.global.GProtoHeader;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.global.GWrap;
import org.scribble.ast.name.qualified.DataNameNode;
import org.scribble.ast.name.qualified.GProtoNameNode;
import org.scribble.ast.name.qualified.ModuleNameNode;
import org.scribble.ast.name.qualified.SigNameNode;
import org.scribble.ast.name.simple.ExtIdNode;
import org.scribble.ast.name.simple.IdNode;
import org.scribble.ast.name.simple.OpNode;
import org.scribble.del.DelFactory;
import org.scribble.parser.ScribTreeAdaptor;
import org.scribble.parser.antlr.ScribbleParser;

public class AssrtScribTreeAdaptor extends ScribTreeAdaptor
{
	public AssrtScribTreeAdaptor(DelFactory df)
	{
		super(df);
	}

	// Create a Tree (ScribNode) from a Token
	@Override
	public ScribNode create(Token t)
	{
		// Switching on ScribbleParser int type constants -- generated from Scribble.g tokens
		// Previously: String tname = t.getText(); -- by convention of Scribble.g, type constant name given as node text, e.g., module: ... -> ^(MODULE ...)
		ScribNodeBase n;
		switch (t.getType())
		{
			// Simple names "constructed directly" by parser, e.g., t=ID -> ID<...Node>[$t] -- N.B. DelDecorator pass needed for them (CHECKME: also do those here instead? to deprecate DelDecorator)

			// Compound names 

			// Non-name (i.e., general) AST nodes

			// N.B. will "override" base payload parsing in super -- FIXME: hacky
			// AssrtAntlrPayloadElemList used to parse both annotated and non-annotated payload elems -- i.e., original AntlrPayloadElemList is now redundant
			case ScribbleAntlrConstants.PAYLOAD_NODE_TYPE: return AssrtAntlrPayloadElemList.parsePayloadElemList(this, ct, aaf);
			
			// N.B. AssrtScribble.g parses this as a separate syntactic category than GLOBALMESSAGETRANSFER (cf. PAYLOAD)
			case ASSRT_GLOBALPROTOCOLHEADER_NODE_TYPE:  return AssrtAntlrGProtocolHeader.parseAssrtGProtocolHeader(this, ct, aaf);
			case ASSRT_GLOBALMESSAGETRANSFER_NODE_TYPE: return AssrtAntlrGMessageTransfer.parseAssrtGMessageTransfer(this, ct, aaf);
			case ASSRT_GLOBALCONNECT_NODE_TYPE:         return AssrtAntlrGConnect.parseAssrtGConnect(this, ct, aaf);
			case ASSRT_GLOBALDO_NODE_TYPE:              return AssrtAntlrGDo.parseAssrtGDo(this, ct, aaf);
			case ScribbleParser.EMPTY_OP: n = new OpNode(t); break;  // From Scribble.g, token (t) text is OpNode.EMPTY_OP_TOKEN_TEXT*/

			default:
			{
				n = (ScribNodeBase) super.create(t);  // Assigning "n", but direct return should be the same?  ast decoration pattern should be delegating back to the same df as below 
			}
		}
		n.decorateDel(this.df);
		
		return n;
	}
}

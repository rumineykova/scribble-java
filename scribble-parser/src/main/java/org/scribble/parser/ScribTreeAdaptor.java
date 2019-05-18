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
package org.scribble.parser;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;
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
import org.scribble.parser.antlr.ScribbleParser;

// CHECKME: get/setType don't seem to be really used
public class ScribTreeAdaptor extends CommonTreeAdaptor
{
	protected final DelFactory df;  // N.B. not af -- here, create nodes "manually" (with del setting) to preserve original tokens

	public ScribTreeAdaptor(DelFactory df)
	{
		this.df = df;
	}
	
	// A Scribble extension should override getId/ExtIdType
	protected int getIdType()
	{
		return ScribbleParser.ID;
	}

	// A Scribble extension should override getId/ExtIdType
	protected int getExtIdType()
	{
		return ScribbleParser.EXTID;
	}
	
	// Generated parser seems to use nil to create "blank" nodes and then "fill them in"
	@Override
	public Object nil()
	{
		return new ScribNil();
	}

	// Create a Tree (ScribNode) from a Token
	// N.B. not using AstFactory, construction here is pre adding children (and also here directly record parsed Token, not recreate)
	@Override
	public ScribNode create(Token t)
	{
		int type = t.getType();
		if (type == getIdType())  // getText is the "value" of the node (not a "type label")
		{
			IdNode n = new IdNode(t);
			n.decorateDel(this.df);
			return n;
		}
		else if (type == getExtIdType())
		{
			t = new CommonToken(t);
			String text = t.getText();
			t.setText(text.substring(1, text.length()-1));  // N.B. remove surrounding quotes "..."
			ExtIdNode n = new ExtIdNode(t);
			n.decorateDel(this.df);
			return n;
		}

		// Switching on ScribbleParser int type constants -- generated from Scribble.g tokens
		// Previously: String tname = t.getText(); -- by convention of Scribble.g, type constant name given as node text, e.g., module: ... -> ^(MODULE ...)
		ScribNodeBase n;
		switch (t.getText())  // Cf. Scribble.g "imaginary" token types
		{
			// Simple names "constructed directly" by parser, e.g., t=ID -> ID<...Node>[$t] -- N.B. DelDecorator pass needed for them (CHECKME: also do those here instead? to deprecate DelDecorator)

			// Compound names 
			case "GPROTO_NAME": n = new GProtoNameNode(t); break;
			case "MODULE_NAME": n = new ModuleNameNode(t); break;
			case "DATA_NAME": n = new DataNameNode(t); break;
			case "SIG_NAME": n = new SigNameNode(t); break;

			// Non-name (i.e., general) AST nodes
			case "MODULE": n = new Module(t); break;
			case "MODULEDECL": n = new ModuleDecl(t); break;
			case "IMPORTMODULE": n = new ImportModule(t); break;

			case "DATADECL": n = new DataDecl(t); break;
			case "SIGDECL": n = new SigDecl(t); break;
			case "GPROTODECL": n = new GProtoDecl(t); break;
 
			// CHECKME: refactor into header?
			case "PROTOMOD_LIST": n = new ProtoModList(t); break;
			case "aux": n = new AuxMod(t); break;  // FIXME: KW return by parser directly (cf. other tokens are imaginary)
			case "explicit": n = new ExplicitMod(t); break;

			case "GPROTOHEADER": n = new GProtoHeader(t); break;
			case "ROLEDECL_LIST": n = new RoleDeclList(t); break;
			case "ROLEDECL": n = new RoleDecl(t); break;
			case "PARAMDECL_LIST":
				n = new NonRoleParamDeclList(t);
				break;
			case "DATAPARAMDECL": n = new DataParamDecl(t); break;
			case "SIGPARAMDECL": n = new SigParamDecl(t); break;

			case "GPROTODEF": n = new GProtoDef(t); break;
			case "GPROTOBLOCK": n = new GProtoBlock(t); break;
			case "GINTERSEQ": n = new GInteractionSeq(t); break;

			case "SIG_LIT": n = new SigLitNode(t); break;
			case "PAYELEM_LIST": n = new PayElemList(t); break;
			case "UNARY_PAYELEM": n = new UnaryPayElem<>(t); break;
			case "GDELEG_PAYELEM": n = new GDelegPayElem(t); break;

			case "GMSGTRANSFER": n = new GMsgTransfer(t); break;
			case "GCONNECT": n = new GConnect(t); break;
			case "GDCONN": n = new GDisconnect(t); break;
			case "GWRAP": n = new GWrap(t); break;

			case "GCONTINUE": n = new GContinue(t); break;
			case "GDO": n = new GDo(t); break;

			case "ROLEARG_LIST": n = new RoleArgList(t); break;
			case "ROLEARG": n = new RoleArg(t); break;
			case "NONROLEARG_LIST": n = new NonRoleArgList(t); break;
			case "NONROLEARG": n = new NonRoleArg(t); break;

			case "GCHOICE": n = new GChoice(t); break;
			case "GRECURSION": n = new GRecursion(t); break;

			// Special cases
			case "EMPTY_OP": n = new OpNode(ScribbleParser.EMPTY_OP, t); break;  
					// From Scribble.g, token (t) text is OpNode.EMPTY_OP_TOKEN_TEXT*/

			default:
			{
				throw new RuntimeException("[TODO] Unknown token type (cf. ScribbleParser): " + t);
			}
		}
		n.decorateDel(this.df);
		
		return n;
	}
}

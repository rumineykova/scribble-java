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

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.ScribNode;
import org.scribble.parser.AntlrConstants.AntlrNodeType;
import org.scribble.parser.ast.AntlrDataTypeDecl;
import org.scribble.parser.ast.AntlrImportModule;
import org.scribble.parser.ast.AntlrMessageSig;
import org.scribble.parser.ast.AntlrMessageSigDecl;
import org.scribble.parser.ast.AntlrModule;
import org.scribble.parser.ast.AntlrModuleDecl;
import org.scribble.parser.ast.AntlrNonRoleArgList;
import org.scribble.parser.ast.AntlrNonRoleParamDecl;
import org.scribble.parser.ast.AntlrNonRoleParamDeclList;
import org.scribble.parser.ast.AntlrPayloadElemList;
import org.scribble.parser.ast.AntlrRoleArg;
import org.scribble.parser.ast.AntlrRoleArgList;
import org.scribble.parser.ast.AntlrRoleDecl;
import org.scribble.parser.ast.AntlrRoleDeclList;
import org.scribble.parser.ast.global.AAntlrGMessageTransfer;
import org.scribble.parser.ast.global.AntlrGChoice;
import org.scribble.parser.ast.global.AntlrGConnect;
import org.scribble.parser.ast.global.AntlrGContinue;
import org.scribble.parser.ast.global.AntlrGDisconnect;
import org.scribble.parser.ast.global.AntlrGDo;
import org.scribble.parser.ast.global.AntlrGInteractionSequence;
import org.scribble.parser.ast.global.AntlrGInterrupt;
import org.scribble.parser.ast.global.AntlrGInterruptible;
import org.scribble.parser.ast.global.AntlrGMessageTransfer;
import org.scribble.parser.ast.global.AntlrGParallel;
import org.scribble.parser.ast.global.AntlrGProtocolBlock;
import org.scribble.parser.ast.global.AntlrGProtocolDecl;
import org.scribble.parser.ast.global.AntlrGProtocolDefinition;
import org.scribble.parser.ast.global.AntlrGProtocolHeader;
import org.scribble.parser.ast.global.AntlrGRecursion;
import org.scribble.parser.ast.global.AntlrGWrap;
import org.scribble.parser.util.ScribParserUtil;
import org.scribble.util.ScribParserException;

public class AScribParser extends ScribParser
{
	public AScribParser()
	{

	}

	@Override
	public ScribNode parse(CommonTree ct) throws ScribParserException
	{
		ScribParser.checkForAntlrErrors(ct);
		
		AntlrNodeType type = ScribParserUtil.getAntlrNodeType(ct);
		switch (type)
		{
			case MODULE: 
				return AntlrModule.parseModule(this, ct);
			case MODULEDECL:
				return AntlrModuleDecl.parseModuleDecl(this, ct);
			case MESSAGESIGNATUREDECL:
				return AntlrMessageSigDecl.parseMessageSigDecl(this, ct);
			case PAYLOADTYPEDECL:
				return AntlrDataTypeDecl.parseDataTypeDecl(this, ct);
			case IMPORTMODULE:
				return AntlrImportModule.parseImportModule(this, ct);
			case GLOBALPROTOCOLDECL:
				return AntlrGProtocolDecl.parseGPrototocolDecl(this, ct);
			case ROLEDECLLIST:
				return AntlrRoleDeclList.parseRoleDeclList(this, ct);
			case ROLEDECL:
				return AntlrRoleDecl.parseRoleDecl(this, ct);
			/*case CONNECTDECL:
				return AntlrConnectDecl.parseConnectDecl(this, ct);*/
			case PARAMETERDECLLIST:
			//case EMPTY_PARAMETERDECLLST:
				return AntlrNonRoleParamDeclList.parseNonRoleParamDeclList(this, ct);
			case PARAMETERDECL:
				return AntlrNonRoleParamDecl.parseNonRoleParamDecl(this, ct);
			case GLOBALPROTOCOLHEADER:
				return AntlrGProtocolHeader.parseGProtocolHeader(this, ct);
			case GLOBALPROTOCOLDEF:
				return AntlrGProtocolDefinition.parseGProtocolDefinition(this, ct);
			case GLOBALPROTOCOLBLOCK:
				return AntlrGProtocolBlock.parseGProtocolBlock(this, ct);
			case GLOBALINTERACTIONSEQUENCE:
				return AntlrGInteractionSequence.parseGInteractionSequence(this, ct);
			case MESSAGESIGNATURE:
				return AntlrMessageSig.parseMessageSig(this, ct);
			case PAYLOAD:
				return AntlrPayloadElemList.parsePayloadElemList(this, ct);
			case GLOBALCONNECT:
				return AntlrGConnect.parseGConnect(this, ct);
			case GLOBALDISCONNECT:
				return AntlrGDisconnect.parseGDisconnect(this, ct);
			case GLOBALMESSAGETRANSFER:
				return AntlrGMessageTransfer.parseGMessageTransfer(this, ct);
			case ANNOTGLOBALMESSAGETRANSFER: 
				return AAntlrGMessageTransfer.parseAnnotGMessageTransfer(this, ct);
			case GLOBALCHOICE:
				return AntlrGChoice.parseGChoice(this, ct);
			case GLOBALRECURSION:
				return AntlrGRecursion.parseGRecursion(this, ct);
			case GLOBALCONTINUE:
				return AntlrGContinue.parseGContinue(this, ct);
			case GLOBALPARALLEL:
				return AntlrGParallel.parseGParallel(this, ct);
			case GLOBALINTERRUPTIBLE:
				return AntlrGInterruptible.parseGInterruptible(this, ct);
			case GLOBALINTERRUPT:
				return AntlrGInterrupt.parseGInterrupt(this, ct);
			case GLOBALDO:
				return AntlrGDo.parseGDo(this, ct);
			case GLOBALWRAP:
				return AntlrGWrap.parseGWrap(this, ct);
			case ROLEINSTANTIATIONLIST:
				return AntlrRoleArgList.parseRoleArgList(this, ct);
			case ROLEINSTANTIATION:
				return AntlrRoleArg.parseRoleArg(this, ct);
			case ARGUMENTINSTANTIATIONLIST:
				return AntlrNonRoleArgList.parseNonRoleArgList(this, ct);
			default:
				throw new RuntimeException("Unknown ANTLR node type: " + type);
		}
	}
}
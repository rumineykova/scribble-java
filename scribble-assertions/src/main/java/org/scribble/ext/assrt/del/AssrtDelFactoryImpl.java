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
package org.scribble.ext.assrt.del;

import org.scribble.ast.global.GConnect;
import org.scribble.ast.global.GDo;
import org.scribble.ast.global.GMsgTransfer;
import org.scribble.ast.local.LRecv;
import org.scribble.ast.name.simple.AmbigNameNode;
import org.scribble.del.DelFactoryImpl;
import org.scribble.ext.assrt.ast.AssrtAnnotDataElem;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAssertDecl;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtModule;
import org.scribble.ext.assrt.ast.global.AssrtGConnect;
import org.scribble.ext.assrt.ast.global.AssrtGContinue;
import org.scribble.ext.assrt.ast.global.AssrtGDo;
import org.scribble.ext.assrt.ast.global.AssrtGMsgTransfer;
import org.scribble.ext.assrt.ast.global.AssrtGProtoHeader;
import org.scribble.ext.assrt.ast.global.AssrtGRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.ext.assrt.ast.local.AssrtLDo;
import org.scribble.ext.assrt.ast.local.AssrtLProtoHeader;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLReq;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.del.global.AssrtGConnectDel;
import org.scribble.ext.assrt.del.global.AssrtGContinueDel;
import org.scribble.ext.assrt.del.global.AssrtGDoDel;
import org.scribble.ext.assrt.del.global.AssrtGMsgTransferDel;
import org.scribble.ext.assrt.del.global.AssrtGRecursionDel;
import org.scribble.ext.assrt.del.local.AssrtLContinueDel;
import org.scribble.ext.assrt.del.local.AssrtLDoDel;
import org.scribble.ext.assrt.del.local.AssrtLRecursionDel;
import org.scribble.ext.assrt.del.local.AssrtLRecvDel;
import org.scribble.ext.assrt.del.local.AssrtLReqDel;
import org.scribble.ext.assrt.del.local.AssrtLSendDel;
import org.scribble.ext.assrt.del.name.AssrtAmbigNameNodeDel;


public class AssrtDelFactoryImpl extends DelFactoryImpl implements AssrtDelFactory
{
	/**
	 * Existing (base) node classes with new dels
   * (Instantiating existing node classes with new dels)
	 */

	//Names
	@Override
	public void AmbigNameNode(AmbigNameNode n)
	{
		setDel(n, new AssrtAmbigNameNodeDel());
	}
	
	
	// General and globals
	/*@Override
	public void GProtoDecl(GProtoDecl n)
	{
		setDel(n, new AssrtGProtoDeclDel());  // TODO: drop "empty" del's (like this one)
	}
	
	// N.B. no GProtoHeader override, still default del (cf. AssrtGProtoHeader)
	
	@Override
	public void GProtoDef(GProtoDef n)
	{
		setDel(n, new AssrtGProtoDefDel());  // Uses header annot to do AssrtAnnotationChecker Def enter/exit
	}
	
	@Override
	public void GProtoBlock(GProtoBlock n)
	{
		setDel(n, new AssrtGProtoBlockDel());
	}*/

	@Override
	public void GMsgTransfer(GMsgTransfer n)
	{
		setDel(n, new AssrtGMsgTransferDel());
	}

	@Override
	public void GConnect(GConnect n)
	{
		setDel(n, new AssrtGConnectDel());
	}

	/*@Override
	public void GContinue(GContinue n)
	{
		setDel(n, new AssrtGContinueDel());
	}*/

	@Override
	public void GDo(GDo n)
	{
		setDel(n, new AssrtGDoDel());
	}

	/*@Override
	public void GChoice(GChoice n)
	{
		setDel(n, new AssrtGChoiceDel());
	}

	@Override
	public void GRecursion(GRecursion n)
	{
		setDel(n, new AssrtGRecursionDel());
	}*/


	// Locals
	/*@Override
	public void LProjectionDecl(LProjectionDecl n)
	{
		setDel(n, new AssrtLProjectionDeclDel());
	}

	@Override
	public void LProtoDef(LProtoDef n)
	{
		setDel(n, new AssrtLProtoDefDel());
	}

	@Override
	public void LProtoBlock(LProtoBlock n)
	{
		setDel(n, new AssrtLProtoBlockDel());
	}*/

	@Override
	public void LRecv(LRecv n)
	{
		setDel(n, new AssrtLRecvDel());  // CHECKME: AssrtLReceive with assertion?
	}
	
	/**
	 * New (Assrt) node types  
	 */
	
	// Names
	@Override
	public void AssrtIntVarNameNode(AssrtIntVarNameNode n)
	{
		setDel(n, createDefaultDel());
	}


	// General and globals
	@Override
	public void AssrtModule(AssrtModule n)
	{
		setDel(n, new AssrtModuleDel());
	}
	

	/**
	 * New (Assrt) node types
	 * (Explicitly creating new Assrt nodes)
	 */

	// General and globals
	@Override
	public void AssrtAssertion(AssrtAssertion n)
	{
		setDel(n, createDefaultDel());
	}

	@Override
	public void AssrtArithExpr(AssrtArithExpr n)
	{
		setDel(n, createDefaultDel());
	}

	@Override
	public void AssrtAssertDecl(AssrtAssertDecl n)
	{
		setDel(n, createDefaultDel());
	}

	@Override
	public void AssrtGProtoHeader(AssrtGProtoHeader n)
	{
		setDel(n, createDefaultDel());  // Annots handled directly by AssrtAnnotationChecker Def enter/exit
	}

	@Override
	public void AssrtAnnotDataElem(AssrtAnnotDataElem n)
	{
		setDel(n, new AssrtAnnotDataTypeElemDel());
	}

	@Override
	public void AssrtGMsgTransfer(AssrtGMsgTransfer n)
	{
		setDel(n, new AssrtGMsgTransferDel());
	}

	@Override
	public void AssrtGConnect(AssrtGConnect n)
	{
		setDel(n, new AssrtGConnectDel());
	}

	@Override
	public void AssrtGContinue(AssrtGContinue n)
	{
		setDel(n, new AssrtGContinueDel());
	}

	@Override
	public void AssrtGDo(AssrtGDo n)
	{
		setDel(n, new AssrtGDoDel());
	}

	@Override
	public void AssrtGRecursion(AssrtGRecursion n)
	{
		setDel(n, new AssrtGRecursionDel());
	}


	// Locals
	@Override
	public void AssrtLProtoHeader(AssrtLProtoHeader n)
	{
		setDel(n, createDefaultDel());  // Annots handled directly by AssrtAnnotationChecker Def enter/exit
	}

	@Override
	public void AssrtLSend(AssrtLSend n)
	{
		setDel(n, new AssrtLSendDel());
	}

	@Override
	public void AssrtLReq(AssrtLReq n)
	{
		setDel(n, new AssrtLReqDel());
	}

	@Override
	public void AssrtLContinue(AssrtLContinue n)
	{
		setDel(n, new AssrtLContinueDel());
	}

	@Override
	public void AssrtLDo(AssrtLDo n)
	{
		setDel(n, new AssrtLDoDel());
	}

	@Override
	public void AssrtLRecursion(AssrtLRecursion n)
	{
		setDel(n, new AssrtLRecursionDel());
	}
}
	
	

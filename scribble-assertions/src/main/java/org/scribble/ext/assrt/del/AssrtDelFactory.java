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

import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtAExprNode;
import org.scribble.ext.assrt.ast.AssrtAnnotDataElem;
import org.scribble.ext.assrt.ast.AssrtAssertDecl;
import org.scribble.ext.assrt.ast.AssrtBExprNode;
import org.scribble.ext.assrt.ast.AssrtModule;
import org.scribble.ext.assrt.ast.AssrtStateVarAnnotNode;
import org.scribble.ext.assrt.ast.AssrtStateVarDecl;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclList;
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


public interface AssrtDelFactory extends DelFactory
{
	// Names
	void AssrtIntVarNameNode(AssrtIntVarNameNode n);  // Default del


	// General and globals
	void AssrtAssertion(AssrtBExprNode n);  // Bool expr
	void AssrtArithExpr(AssrtAExprNode n);  // Int expr

	void AssrtModule(AssrtModule n);

	void AssrtAssertDecl(AssrtAssertDecl n);

	void AssrtGProtoHeader(AssrtGProtoHeader n);
	void AssrtStateVarAnnotNode(AssrtStateVarAnnotNode n);
	void AssrtStateVarDeclList(AssrtStateVarDeclList n);
	void AssrtStateVarDecl(AssrtStateVarDecl n);

	void AssrtGMsgTransfer(AssrtGMsgTransfer n);
	void AssrtGConnect(AssrtGConnect n);

	void AssrtGContinue(AssrtGContinue n);
	void AssrtGDo(AssrtGDo n);

	void AssrtGRecursion(AssrtGRecursion n);

	void AssrtAnnotDataElem(AssrtAnnotDataElem n);


	// Locals
	void AssrtLProtoHeader(AssrtLProtoHeader n);

	void AssrtLSend(AssrtLSend n);
	void AssrtLReq(AssrtLReq n);
	void AssrtLRecursion(AssrtLRecursion n);
	void AssrtLContinue(AssrtLContinue n);
	void AssrtLDo(AssrtLDo n);
}
	
	

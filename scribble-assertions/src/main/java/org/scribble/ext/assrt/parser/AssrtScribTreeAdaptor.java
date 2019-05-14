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

import org.antlr.runtime.Token;
import org.scribble.ast.ScribNode;
import org.scribble.ast.ScribNodeBase;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtAnnotDataElem;
import org.scribble.ext.assrt.ast.global.AssrtGConnect;
import org.scribble.ext.assrt.ast.global.AssrtGDo;
import org.scribble.ext.assrt.ast.global.AssrtGMsgTransfer;
import org.scribble.ext.assrt.ast.global.AssrtGProtocolHeader;
import org.scribble.parser.ScribTreeAdaptor;
import org.scribble.parser.antlr.AssrtScribbleParser;

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

			case AssrtScribbleParser.ASSRT_ANNOTPAYLOADELEM: n = new AssrtAnnotDataElem(t); break;
			
			case AssrtScribbleParser.ASSRT_GLOBALPROTOCOLHEADER: return new AssrtGProtocolHeader(t);
			case AssrtScribbleParser.ASSRT_GLOBALMESSAGETRANSFER: return new AssrtGMsgTransfer(t);
			case AssrtScribbleParser.ASSRT_GLOBALCONNECT: return new AssrtGConnect(t);
			case AssrtScribbleParser.ASSRT_GLOBALDO: return new AssrtGDo(t);

			default:
			{
				n = (ScribNodeBase) super.create(t);  // Assigning "n", but direct return should be the same?  ast decoration pattern should be delegating back to the same df as below 
			}
		}
		n.decorateDel(this.df);
		
		return n;
	}
}

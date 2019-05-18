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
import org.scribble.ext.assrt.ast.AssrtModule;
import org.scribble.ext.assrt.ast.global.AssrtGConnect;
import org.scribble.ext.assrt.ast.global.AssrtGDo;
import org.scribble.ext.assrt.ast.global.AssrtGMsgTransfer;
import org.scribble.ext.assrt.ast.global.AssrtGProtoHeader;
import org.scribble.parser.ScribAntlrTokens;
import org.scribble.parser.ScribTreeAdaptor;

public class AssrtScribTreeAdaptor extends ScribTreeAdaptor
{
	public AssrtScribTreeAdaptor(ScribAntlrTokens tokens, DelFactory df)
	{
		super(tokens, df);
	}

	// Create a Tree (ScribNode) from a Token
	// N.B. not using AstFactory, construction here is pre adding children (and also here directly record parsed Token, not recreate)
	@Override
	public ScribNode create(Token t)
	{
		// Switching on ScribbleParser "imaginary" token names -- generated from Scribble.g tokens
		// Previously, switched on t.getType(), but arbitrary int constant generation breaks extensibility (e.g., super.create(t))
		ScribNodeBase n;
		switch (t.getText())
		{
			/**
			 *  Create ext node type in place of base
			 *  Parser returns a base token type, we create an ext node type but keep the base token
			 */

			// TODO: integrate with ASSRT variants below?  maybe by un-deprecating reconstructs to make base children configs valid

			case "MODULE": n = new AssrtModule(t); break;

			case "GPROTOHEADER": n = new AssrtGProtoHeader(t); break;  

			case "GMSGTRANSFER": n = new AssrtGMsgTransfer(t); break;
			case "GCONNECT": n = new AssrtGConnect(t); break;

			//case AssrtScribbleParser.GCONTINUE: n = new AssrtGContinue(t); break;
			case "GDO": n = new AssrtGDo(t); break;

			//case AssrtScribbleParser.GRECURSION: n = new AssrtGRecursion(t); break;
			

			/**
			 *  Creating explicitly new ext (Assrt) node types
			 *  Parser returns an ext token type, we create the corresponding ext node type
			 */
			
			// Simple names "constructed directly" by parser, cf. assrt_varname: t=ID -> ID<AssrtIntVarNameNode>[$t] ;

			// Compound names 

			// Non-name (i.e., general) AST nodes
			case "ASSERT_KW": throw new RuntimeException("[TODO] : " + t);

			case "ASSRT_GLOBALPROTOCOLHEADER": n = new AssrtGProtoHeader(t); break;

			case "ASSRT_ANNOTPAYLOADELEM": n = new AssrtAnnotDataElem(t); break;

			case "ASSRT_GLOBALMESSAGETRANSFER": n = new AssrtGMsgTransfer(t); break;
			case "ASSRT_GLOBALCONNECT": n = new AssrtGConnect(t); break;
			
			case "ASSRT_GLOBALDO": n = new AssrtGDo(t); break;

			default:
			{
				n = (ScribNodeBase) super.create(t);  // Assigning "n", but direct return should be the same?  ast decoration pattern should be delegating back to the same df as below 
			}
		}
		n.decorateDel(this.df);
		
		return n;
	}
}

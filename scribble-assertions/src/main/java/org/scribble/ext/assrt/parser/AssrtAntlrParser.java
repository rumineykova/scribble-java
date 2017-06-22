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

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Parser;
import org.scribble.parser.AntlrParser;
import org.scribble.parser.antlr.AssrtScribbleLexer;
import org.scribble.parser.antlr.AssrtScribbleParser;

public class AssrtAntlrParser extends AntlrParser
{
	
	@Override
	protected Lexer newScribbleLexer(ANTLRStringStream ass)
	{
		return new AssrtScribbleLexer(ass);
	}
	
	@Override
	protected Parser newScribbleParser(CommonTokenStream cts)  // FIXME: need to "cast" Parser to 
	{
		return new AssrtScribbleParser(cts);
	}
	
	/*@Override
	public CommonTree parseAntlrTree(Resource res)
	{
		try
		{
			String input = readInput(res);
			AssrtScribbleLexer lex = new AssrtScribbleLexer(new ANTLRStringStream(input));
			AssrtScribbleParser parser = new AssrtScribbleParser(new CommonTokenStream(lex));
			return (CommonTree) parser.module().getTree();
		}
		catch (RecognitionException e)
		{
			throw new RuntimeException(e);
		}
	}*/
}

package org.scribble.ext.assrt.parser.scribble;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.scribble.parser.antlr.AssrtScribbleLexer;
import org.scribble.parser.antlr.AssrtScribbleParser;
import org.scribble.parser.scribble.AntlrParser;

public class AssrtAntlrParser extends AntlrParser
{
	
	@Override
	protected Lexer getScribbleLexer(ANTLRStringStream ass)
	{
		return new AssrtScribbleLexer(ass);
	}
	
	@Override
	//protected Parser newScribbleParser(CommonTokenStream cts)
	protected CommonTree runScribbleParser(CommonTokenStream cts) throws RecognitionException
	{
		return (CommonTree) new AssrtScribbleParser(cts).module().getTree();
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

package org.scribble.ext.assrt.parser;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Parser;
import org.scribble.del.DelFactory;
import org.scribble.parser.ScribAntlrTokens;
import org.scribble.parser.ScribAntlrWrapper;
import org.scribble.parser.antlr.AssrtScribbleLexer;
import org.scribble.parser.antlr.AssrtScribbleParser;

public class AssrtScribAntlrWrapper extends ScribAntlrWrapper
{
	public AssrtScribAntlrWrapper(DelFactory df)
	{
		super(df);
	}

	@Override
	protected String[] getTokenNames()
	{
		return AssrtScribbleParser.tokenNames;
	}
	
	@Override
	public Lexer newScribbleLexer(ANTLRStringStream ss)
	{
		return new AssrtScribbleLexer(ss);
	}
	
	@Override
	public Parser newScribbleParser(CommonTokenStream ts)
	{
		return new AssrtScribbleParser(ts);
	}

	@Override
	protected AssrtScribTreeAdaptor newAdaptor(ScribAntlrTokens tokens,
			DelFactory df)
	{
		return new AssrtScribTreeAdaptor(tokens, df);
	}
}

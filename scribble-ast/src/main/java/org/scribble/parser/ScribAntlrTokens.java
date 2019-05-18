package org.scribble.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.Parser;

public class ScribAntlrTokens
{
	private final Map<Integer, String> typeToText;
	private final Map<String, Integer> textToType;
	
	public ScribAntlrTokens(Parser p, String[] tokenNames)
	{
		try
		{
			Class<? extends Parser> parserC = p.getClass();
			Map<Integer, String> typeToText = new HashMap<>();
			Map<String, Integer> textToType = new HashMap<>();
			for (String t : tokenNames)
			{
				char c = t.charAt(0);
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
				{
					int i;
					i = parserC.getField(t).getInt(p);
					typeToText.put(i, t);
					textToType.put(t, i);
				}
			}
			this.typeToText = Collections.unmodifiableMap(typeToText);
			this.textToType = Collections.unmodifiableMap(textToType);
		}
		catch (IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public int getType(String text)
	{
		return this.textToType.get(text);
	}

	public String getText(int type)
	{
		return this.typeToText.get(type);
	}
}

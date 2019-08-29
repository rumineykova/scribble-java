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

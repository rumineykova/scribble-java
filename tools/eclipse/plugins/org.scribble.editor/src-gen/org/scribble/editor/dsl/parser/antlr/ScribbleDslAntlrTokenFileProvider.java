/*
* generated by Xtext
*/
package org.scribble.editor.dsl.parser.antlr;

import java.io.InputStream;
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider;

public class ScribbleDslAntlrTokenFileProvider implements IAntlrTokenFileProvider {
	
	public InputStream getAntlrTokenFile() {
		ClassLoader classLoader = getClass().getClassLoader();
    	return classLoader.getResourceAsStream("org/scribble/editor/dsl/parser/antlr/internal/InternalScribbleDsl.tokens");
	}
}

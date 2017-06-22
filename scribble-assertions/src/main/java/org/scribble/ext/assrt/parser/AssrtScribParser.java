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

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ScribNode;
import org.scribble.ext.assrt.parser.ast.AssrtAntlrPayloadElemList;
import org.scribble.ext.assrt.parser.ast.global.AssrtAntlrGMessageTransfer;
import org.scribble.parser.AntlrConstants.AntlrNodeType;
import org.scribble.parser.ScribParser;
import org.scribble.parser.util.ScribParserUtil;
import org.scribble.util.ScribParserException;

public class AssrtScribParser extends ScribParser
{
	public AssrtScribParser()
	{

	}

	@Override
	public ScribNode parse(CommonTree ct, AstFactory af) throws ScribParserException
	{
		ScribParser.checkForAntlrErrors(ct);
		
		AntlrNodeType type = ScribParserUtil.getAntlrNodeType(ct);
		switch (type)
		{
			case PAYLOAD:  
				// N.B. will "override" base payload parsing in super
				// Using AssrtAntlrPayloadElemList to parse both annotated and non-annotated payload elems -- i.e., original AntlrPayloadElemList is now redundant
				return AssrtAntlrPayloadElemList.parsePayloadElemList(this, ct, af);
			case ANNOTGLOBALMESSAGETRANSFER: 
				// N.B. AssrtScribble.g parses this as a separate syntactic category than GLOBALMESSAGETRANSFER (cf. PAYLOAD)
				return AssrtAntlrGMessageTransfer.parseAnnotGMessageTransfer(this, ct, af);
			default:
				return super.parse(ct, af);  // Does checkForAntlrErrors again, but fine
		}
	}
}

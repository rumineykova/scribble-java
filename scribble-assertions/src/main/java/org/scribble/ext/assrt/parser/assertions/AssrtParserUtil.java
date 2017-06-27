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
package org.scribble.ext.assrt.parser.assertions;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrConstants.AssrtAntlrNodeType;

public class AssrtParserUtil  // Cf. ScribParserUtil
{
	public static AssrtAntlrNodeType getAntlrNodeType(CommonTree ct)
	{
		String type = ct.getToken().getText();
		switch (type)
		{
			case AssrtAntlrConstants.BEXPR_NODE_TYPE: return AssrtAntlrNodeType.BEXPR;
			case AssrtAntlrConstants.CEXPR_NODE_TYPE: return AssrtAntlrNodeType.CEXPR;
			case AssrtAntlrConstants.AEXPR_NODE_TYPE: return AssrtAntlrNodeType.AEXPR;
			case AssrtAntlrConstants.VAR_NODE_TYPE:   return AssrtAntlrNodeType.VAR;
			case AssrtAntlrConstants.VALUE_NODE_TYPE: return AssrtAntlrNodeType.VALUE;

			// Nodes without a "node type", e.g. parameter names, fall in here
			default: throw new RuntimeException("Unknown ANTLR node type label for assertion of type: " + type);
		}
	}
}

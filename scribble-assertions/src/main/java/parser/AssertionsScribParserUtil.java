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
package parser;

import org.antlr.runtime.tree.CommonTree;

import parser.AssertionsAntlrConstants.AssertionNodeType;

public class AssertionsScribParserUtil {

	public static AssertionNodeType getAntlrNodeType(CommonTree n)
	{
		String type = n.getToken().getText();
		switch (type)
		{
			//case AntlrConstants.EMPTY_PARAMETERDECLLST_NODE_TYPE: return AntlrNodeType.EMPTY_PARAMETERDECLLST;
			case AssertionsAntlrConstants.BEXPR_NODE_TYPE: return AssertionNodeType.BEXPR;
			case AssertionsAntlrConstants.CEXPR_NODE_TYPE: return AssertionNodeType.CEXPR;
			case AssertionsAntlrConstants.AEXPR_NODE_TYPE: return AssertionNodeType.AEXPR;
			case AssertionsAntlrConstants.VAR_NODE_TYPE: return AssertionNodeType.VAR;
			case AssertionsAntlrConstants.VALUE_NODE_TYPE: return AssertionNodeType.VALUE;
			default:
			{
				// Nodes without a "node type", e.g. parameter names, fall in here
				throw new RuntimeException("Unknown ANTLR node type label for assertion of type: " + type);
			} 
		}
	}
}

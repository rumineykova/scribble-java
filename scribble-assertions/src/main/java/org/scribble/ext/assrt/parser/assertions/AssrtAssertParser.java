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
import org.scribble.ext.assrt.ast.formula.SmtFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrConstants.AssrtAntlrNodeType;
import org.scribble.ext.assrt.parser.assertions.ast.formula.ArithFormulaNode;
import org.scribble.ext.assrt.parser.assertions.ast.formula.BoolFormulaNode;
import org.scribble.ext.assrt.parser.assertions.ast.formula.CompFormulaNode;
import org.scribble.ext.assrt.parser.assertions.ast.formula.ValueNode;
import org.scribble.ext.assrt.parser.assertions.ast.formula.VarNode;
import org.scribble.parser.scribble.ScribParser;

public class AssrtAssertParser  // Cf. ScribParser
{
	private static AssrtAssertParser instance = null;

	protected AssrtAssertParser()
	{
		
	}

	public static AssrtAssertParser getInstance()
	{
		if (AssrtAssertParser.instance == null)
		{
			AssrtAssertParser.instance = new AssrtAssertParser();
		}
		return AssrtAssertParser.instance;
	}

	// ct should be the first child of the root node of the assertion subtree (i.e., AssrtAntlrGMessageTransfer.getAssertionChild.getChild(0))
	public SmtFormula parse(CommonTree ct) //throws AssertionsParseException
	{
		ScribParser.checkForAntlrErrors(ct);
		
		AssrtAntlrNodeType type = AssrtParserUtil.getAntlrNodeType(ct);
		switch (type)
		{
			case BEXPR: return BoolFormulaNode.parseBoolFormula(this, ct);
			case CEXPR: return CompFormulaNode.parseCompFormula(this, ct);
			case AEXPR: return ArithFormulaNode.parseArithFormula(this, ct);
			case VAR:   return VarNode.parseVarFormula(this, ct);
			case VALUE: return ValueNode.parseValueFormula(this, ct);
			default:    throw new RuntimeException("Unknown ANTLR node type: " + type);
		}
	}
}

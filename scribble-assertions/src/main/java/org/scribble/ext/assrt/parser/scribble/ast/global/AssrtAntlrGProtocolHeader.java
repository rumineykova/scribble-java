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
package org.scribble.ext.assrt.parser.scribble.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.global.GProtocolHeader;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.parser.scribble.AssrtAntlrToScribParser;
import org.scribble.ext.assrt.type.formula.AssrtBinCompFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;
import org.scribble.parser.scribble.AntlrToScribParser;
import org.scribble.parser.scribble.ast.global.AntlrGProtocolHeader;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

public class AssrtAntlrGProtocolHeader
{
	// Original element indices unchanged

	public static final int ASSERTION_CHILD_INDEX = 3;

	public static GProtocolHeader parseAssrtGProtocolHeader(AntlrToScribParser parser, CommonTree root, AssrtAstFactory af) throws ScribParserException
	{
		GProtocolNameNode name = AntlrSimpleName.toGProtocolNameNode(AntlrGProtocolHeader.getNameChild(root), af);
		RoleDeclList rdl = (RoleDeclList) parser.parse(AntlrGProtocolHeader.getRoleDeclListChild(root), af);
		NonRoleParamDeclList pdl = (NonRoleParamDeclList) parser.parse(AntlrGProtocolHeader.getParamDeclListChild(root), af);
		
		// FIXME: factor out of AssrtAntlrGMessageTransfer
		CommonTree assTree = AssrtAntlrGProtocolHeader.getAssertionChild(root);
		AssrtAssertion ass = AssrtAntlrGProtocolHeader.parseIntVarInitDeclAnnot(((AssrtAntlrToScribParser) parser).ap, assTree, af);

		return af.AssrtGProtocolHeader(root, name, rdl, pdl, ass);
	}

	// FIXME: factor out restrictions explicitly into the ANTLR grammar
	// FIXME: make a different AST class for GProtocolHeader, distinct from AssrtAssertion -- not really an assertion, it's just a initialised-declaration expr
	private static AssrtAssertion parseIntVarInitDeclAnnot(AssrtAntlrToFormulaParser fp, CommonTree assTree, AssrtAstFactory af) throws ScribParserException
	{
		AssrtAssertion ass = AssrtAntlrGMessageTransfer.parseAssertion(fp, assTree, af);

		AssrtBoolFormula f = ass.getFormula();
		if (!(f instanceof AssrtBinCompFormula))
		{
			throw new ScribParserException("[assrt] Protocol header annotation must be an int variable initialised-declaration expression, not: " + f);
		}
		AssrtBinCompFormula bcf = (AssrtBinCompFormula) f;
		if (((AssrtBinCompFormula) f).op != AssrtBinCompFormula.Op.Eq)
		{
			throw new ScribParserException("[assrt] Protocol header annotation must be an int variable initialised-declaration expression, not: " + f);
		}
		else if (!(bcf.left instanceof AssrtIntVarFormula))
		{
			throw new ScribParserException("[assrt] Protocol header annotation LHS must be an int variable, not: " + bcf.left);
		}
		
		return ass;
	}

	public static CommonTree getAssertionChild(CommonTree root)
	{
		return (CommonTree) root.getChild(AssrtAntlrGProtocolHeader.ASSERTION_CHILD_INDEX);
	}
}

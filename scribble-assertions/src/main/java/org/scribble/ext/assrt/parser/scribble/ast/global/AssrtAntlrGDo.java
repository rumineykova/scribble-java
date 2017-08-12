package org.scribble.ext.assrt.parser.scribble.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.global.AssrtGDo;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.parser.scribble.AssrtAntlrToScribParser;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.parser.scribble.AntlrToScribParser;
import org.scribble.parser.scribble.ast.global.AntlrGDo;
import org.scribble.parser.scribble.ast.name.AntlrQualifiedName;
import org.scribble.util.ScribParserException;

public class AssrtAntlrGDo
{
	// Original element indices unchanged

	public static final int ASSERTION_CHILD_INDEX = 3;

	public static AssrtGDo parseAssrtGDo(AntlrToScribParser parser, CommonTree root, AstFactory af) throws ScribParserException
	{
		RoleArgList ril = (RoleArgList) parser.parse(AntlrGDo.getRoleArgListChild(root), af);
		NonRoleArgList al = (NonRoleArgList) parser.parse(AntlrGDo.getNonRoleArgListChild(root), af);
		GProtocolNameNode pnn = AntlrQualifiedName.toGProtocolNameNode(AntlrGDo.getProtocolNameChild(root), af);
		
		CommonTree annotTree = AssrtAntlrGProtocolHeader.getAssertionChild(root);
		AssrtArithExpr annot = AssrtAntlrGDo.parseArithAnnotation(((AssrtAntlrToScribParser) parser).ap, annotTree, (AssrtAstFactory) af);
		
		return ((AssrtAstFactory) af).AssrtGDo(root, ril, al, pnn, annot);
	}

	public static AssrtArithExpr parseArithAnnotation(AssrtAntlrToFormulaParser ap, CommonTree annotTree, AssrtAstFactory af)
	{
		AntlrToScribParser.checkForAntlrErrors(annotTree);  // Check ct root

		CommonTree tmp = //(CommonTree) annotTree.getChild(0);  // Formula node to parse  // FIXME: factor out?
				annotTree;
		AssrtArithFormula f = (AssrtArithFormula) ap.parse(tmp);  // By AssrtAssertions.g
		return af.AssrtArithAnnotation(annotTree, f);
	}

	public static CommonTree getAssertionChild(CommonTree root)
	{
		return (CommonTree) root.getChild(AssrtAntlrGProtocolHeader.ASSERTION_CHILD_INDEX);
	}
}

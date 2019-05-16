package org.scribble.ext.assrt.parser.scribble.ast.global;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.global.GProtocolHeader;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.core.type.kind.AssrtIntVarNameKind;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.parser.scribble.AssrtAntlrToScribParser;
import org.scribble.parser.scribble.AntlrToScribParser;
import org.scribble.parser.scribble.ast.global.AntlrGProtocolHeader;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

public class AssrtAntlrGProtocolHeader
{
	// Original element indices unchanged

	public static final int ASSRT_STATEVARDECLLIST_CHILD_INDEX = 3;

	public static GProtocolHeader parseAssrtGProtocolHeader(AntlrToScribParser parser, CommonTree root, AssrtAstFactory af) throws ScribParserException
	{
		GProtocolNameNode name = AntlrSimpleName.toGProtocolNameNode(AntlrGProtocolHeader.getNameChild(root), af);
		RoleDeclList rdl = (RoleDeclList) parser.parse(AntlrGProtocolHeader.getRoleDeclListChild(root), af);
		NonRoleParamDeclList pdl = (NonRoleParamDeclList) parser.parse(AntlrGProtocolHeader.getParamDeclListChild(root), af);
		
		AssrtAntlrToFormulaParser ap = ((AssrtAntlrToScribParser) parser).ap;
	
		CommonTree assTree = AssrtAntlrGProtocolHeader.getAssrtStateVarDeclListChild(root);  // ASSRT_STATEVARDECLLIST
		List<AssrtIntVarNameNode> annotvars = (assTree.getChildCount() > 1)
				? parseAssrtStateVarDeclListVars(assTree, af)
				: Collections.emptyList();
		List<AssrtArithExpr> annotexprs = (assTree.getChildCount() > 1)
				? parseAssrtStateVarDeclListExprs(ap, assTree, af)
				: Collections.emptyList();
		AssrtAssertion ass = parseAssrtStateVarDeclListAssertionChild(ap, assTree, af);
		return af.AssrtGProtocolHeader(root, name, rdl, pdl, //ass);
				annotvars, annotexprs,
				ass);
	}
	
	public static final int ASSRT_STATEVARDECLLISTASSERTION_CHILD_INDEX = 0;
	public static final int ASSRT_STATEVARDECL_CHILDREN_START_INDEX = 1;
	
	private static AssrtAssertion parseAssrtStateVarDeclListAssertionChild(AssrtAntlrToFormulaParser ap, CommonTree assTree, AssrtAstFactory af)
	{
		CommonTree ass = (CommonTree) assTree.getChild(ASSRT_STATEVARDECLLISTASSERTION_CHILD_INDEX);
		return (ass.getChildCount() == 0)
				? null
				: AssrtAntlrGMessageTransfer.parseAssertion(ap, ass, af);
	}

	private static List<AssrtIntVarNameNode> parseAssrtStateVarDeclListVars(CommonTree assTree, AssrtAstFactory af)
	{
		List<?> children = assTree.getChildren();
		List<CommonTree> cs = 
				children.subList(ASSRT_STATEVARDECL_CHILDREN_START_INDEX, children.size()).stream()  // Stream of ASSRT_STATEVARDECL
					.map(c -> (CommonTree) ((CommonTree) c).getChild(0)).collect(Collectors.toList());  // List of INTVAR
		return cs.stream().map(c -> 
					(AssrtIntVarNameNode) af.SimpleNameNode(c, AssrtIntVarNameKind.KIND, c.getChild(0).getText()))  // Cf. AssrtAntlrIntVarFormula::parseIntVarFormula
				.collect(Collectors.toList());
	}
	
	private static List<AssrtArithExpr> parseAssrtStateVarDeclListExprs(AssrtAntlrToFormulaParser ap, CommonTree assTree, AssrtAstFactory af)
	{
		List<?> children = assTree.getChildren();
		List<CommonTree> cs = 
				children.subList(ASSRT_STATEVARDECL_CHILDREN_START_INDEX, children.size()).stream()  // Stream of ASSRT_STATEVARDECL
					.map(c -> (CommonTree) ((CommonTree) c).getChild(1)).collect(Collectors.toList());  // List of arith-exprs
		return cs.stream().map(c -> 
					AssrtAntlrGDo.parseArithAnnotation(ap, c, af))
				.collect(Collectors.toList());
	}

	public static CommonTree getAssrtStateVarDeclListChild(CommonTree root)
	{
		return (CommonTree) root.getChild(AssrtAntlrGProtocolHeader.ASSRT_STATEVARDECLLIST_CHILD_INDEX);
	}
}

package org.scribble.ext.assrt.parser.scribble.ast.global;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.global.GProtocolHeader;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrToFormulaParser;
import org.scribble.ext.assrt.parser.scribble.AssrtAntlrToScribParser;
import org.scribble.ext.assrt.type.kind.AssrtVarNameKind;
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
		
		CommonTree assTree = AssrtAntlrGProtocolHeader.getAssrtStateVarDeclListChild(root);  // ASSRT_STATEVARDECLLIST
		List<AssrtIntVarNameNode> annotvars = parseAssrtStateVarDeclListVars(assTree, af);
		List<AssrtArithExpr> annotexprs = parseAssrtStateVarDeclListExprs(((AssrtAntlrToScribParser) parser).ap, assTree, af);
		return af.AssrtGProtocolHeader(root, name, rdl, pdl, //ass);
				annotvars, annotexprs,
				null);  // FIXME
	}
	
	private static List<AssrtIntVarNameNode> parseAssrtStateVarDeclListVars(CommonTree assTree, AssrtAstFactory af)
	{
		List<CommonTree> cs = 
				((Stream<?>) assTree.getChildren().stream())  // Stream of ASSRT_STATEVARDECL
					.map(c -> (CommonTree) ((CommonTree) c).getChild(0)).collect(Collectors.toList());  // List of INTVAR
		return cs.stream().map(c -> 
					(AssrtIntVarNameNode) af.SimpleNameNode(c, AssrtVarNameKind.KIND, c.getChild(0).getText()))  // Cf. AssrtAntlrIntVarFormula::parseIntVarFormula
				.collect(Collectors.toList());
	}
	
	private static List<AssrtArithExpr> parseAssrtStateVarDeclListExprs(AssrtAntlrToFormulaParser ap, CommonTree assTree, AssrtAstFactory af)
	{
		List<CommonTree> cs = 
				((Stream<?>) assTree.getChildren().stream())  // Stream of ASSRT_STATEVARDECL
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

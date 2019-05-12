package org.scribble.ext.assrt.ast;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ImportDecl;
import org.scribble.ast.MessageNode;
import org.scribble.ast.ModuleDecl;
import org.scribble.ast.DataOrSigDeclNode;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.global.AssrtGConnect;
import org.scribble.ext.assrt.ast.global.AssrtGContinue;
import org.scribble.ext.assrt.ast.global.AssrtGDo;
import org.scribble.ext.assrt.ast.global.AssrtGMsgTransfer;
import org.scribble.ext.assrt.ast.global.AssrtGProtocolHeader;
import org.scribble.ext.assrt.ast.global.AssrtGRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.ext.assrt.ast.local.AssrtLDo;
import org.scribble.ext.assrt.ast.local.AssrtLProtocolHeader;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLRequest;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.qualified.AssrtAssertNameNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtSortNode;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;


public interface AssrtAstFactory extends AstFactory
{
	// FIXME: should not be an AssrtAssertion -- should be just an (integer) var decl expr (which is not a bool expr)
	AssrtGProtocolHeader AssrtGProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass);
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass);

	AssrtGMsgTransfer AssrtGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion);
	AssrtGConnect AssrtGConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion assertion);
	AssrtGRecursion AssrtGRecursion(CommonTree source, RecVarNode recvar, GProtocolBlock block, //AssrtAssertion ass);
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass);
	AssrtGContinue AssrtGContinue(CommonTree source, RecVarNode recvar, //AssrtArithExpr annot);
			List<AssrtArithExpr> annotexprs);
	AssrtGDo AssrtGDo(CommonTree source, RoleArgList roleinstans, NonRoleArgList arginstans, GProtocolNameNode proto, //AssrtArithExpr annot);
			List<AssrtArithExpr> annotexprs);

	AssrtAnnotDataTypeElem AssrtAnnotDataTypeElem(CommonTree source, AssrtIntVarNameNode varName, DataTypeNode dataType);

	AssrtLProtocolHeader AssrtLProtocolHeader(CommonTree source, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass);
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass);

	AssrtLSend AssrtLSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion);
	AssrtLRequest AssrtLConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion assertion);
	AssrtLRecursion AssrtLRecursion(CommonTree source, RecVarNode recvar, LProtocolBlock block, //AssrtAssertion ass);
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass);
	AssrtLContinue AssrtLContinue(CommonTree source, RecVarNode recvar, //AssrtArithExpr annot);
			List<AssrtArithExpr> annotexprs);
	AssrtLDo AssrtLDo(CommonTree source, RoleArgList roleinstans, NonRoleArgList arginstans, LProtocolNameNode proto, //AssrtArithExpr annot);
			List<AssrtArithExpr> annotexprs);

	AssrtAssertion AssrtAssertion(CommonTree source, AssrtBoolFormula f);
	AssrtArithExpr AssrtArithAnnotation(CommonTree source, AssrtArithFormula expr);

	AssrtModule AssrtModule(CommonTree source, ModuleDecl moddecl, List<ImportDecl<?>> imports, List<DataOrSigDeclNode<?>> data,
			List<ProtocolDecl<?>> protos, List<AssrtAssertDecl> asserts);
	AssrtAssertDecl AssrtAssertDecl(CommonTree source, AssrtAssertNameNode name, List<AssrtSortNode> params, AssrtSortNode ret, AssrtSmtFormula<?> expr);
}

package org.scribble.ext.assrt.ast;

import java.util.List;

import org.antlr.runtime.Token;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ImportDecl;
import org.scribble.ast.ModuleDecl;
import org.scribble.ast.MsgNode;
import org.scribble.ast.NonProtoDecl;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.ProtoDecl;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.global.GProtoBlock;
import org.scribble.ast.local.LProtoBlock;
import org.scribble.ast.name.qualified.DataNameNode;
import org.scribble.ast.name.qualified.GProtoNameNode;
import org.scribble.ast.name.qualified.LProtoNameNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.global.AssrtGConnect;
import org.scribble.ext.assrt.ast.global.AssrtGContinue;
import org.scribble.ext.assrt.ast.global.AssrtGDo;
import org.scribble.ext.assrt.ast.global.AssrtGMsgTransfer;
import org.scribble.ext.assrt.ast.global.AssrtGProtoHeader;
import org.scribble.ext.assrt.ast.global.AssrtGRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.ext.assrt.ast.local.AssrtLDo;
import org.scribble.ext.assrt.ast.local.AssrtLProtoHeader;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLReq;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.qualified.AssrtAssertNameNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtSortNode;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;


public interface AssrtAstFactory extends AstFactory
{
	// Names
	AssrtIntVarNameNode AssrtIntVarNameNode(Token t, String text);


	// General and globals
	AssrtAssertion AssrtAssertion(Token t, AssrtBoolFormula f);
	AssrtArithExpr AssrtArithAnnotation(Token t, AssrtArithFormula expr);

	AssrtModule AssrtModule(Token t, ModuleDecl mdecl,
			List<? extends ImportDecl<?>> imports,
			List<? extends NonProtoDecl<?>> nonprotos,
			List<? extends ProtoDecl<?>> protos, List<AssrtAssertDecl> asserts);

	// FIXME: should not be an AssrtAssertion -- should be just an (integer) var decl expr (which is not a bool expr)
	AssrtGProtoHeader AssrtGProtoHeader(Token t, GProtoNameNode name,
			RoleDeclList roledecls, NonRoleParamDeclList paramdecls, // AssrtAssertion ass);
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass);

	AssrtAssertDecl AssrtAssertDecl(Token t, AssrtAssertNameNode name,
			List<AssrtSortNode> params, AssrtSortNode ret, AssrtSmtFormula<?> expr);

	AssrtGMsgTransfer AssrtGMsgTransfer(Token t, RoleNode src, MsgNode msg,
			List<RoleNode> dests, AssrtAssertion assertion);
	AssrtGConnect AssrtGConnect(Token t, RoleNode src, MsgNode msg, RoleNode dest,
			AssrtAssertion assertion);

	AssrtGContinue AssrtGContinue(Token t, RecVarNode recvar, //AssrtArithExpr annot);
			List<AssrtArithExpr> annotexprs);
	AssrtGDo AssrtGDo(Token t, RoleArgList roleinstans, NonRoleArgList arginstans, GProtoNameNode proto, //AssrtArithExpr annot);
			List<AssrtArithExpr> annotexprs);

	AssrtGRecursion AssrtGRecursion(Token t, RecVarNode recvar, GProtoBlock block, //AssrtAssertion ass);
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass);

	AssrtAnnotDataElem AssrtAnnotDataTypeElem(Token t,
			AssrtIntVarNameNode varName, DataNameNode data);

	// Locals
	AssrtLProtoHeader AssrtLProtoHeader(Token t, LProtoNameNode name,
			RoleDeclList roledecls, NonRoleParamDeclList paramdecls, //AssrtAssertion ass);
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass);

	AssrtLSend AssrtLSend(Token t, RoleNode src, MsgNode msg,
			List<RoleNode> dests, AssrtAssertion assertion);
	AssrtLReq AssrtLConnect(Token t, RoleNode src, MsgNode msg, RoleNode dest,
			AssrtAssertion assertion);

	AssrtLContinue AssrtLContinue(Token t, RecVarNode recvar, //AssrtArithExpr annot);
			List<AssrtArithExpr> annotexprs);
	AssrtLDo AssrtLDo(Token t, RoleArgList roleinstans, NonRoleArgList arginstans, LProtoNameNode proto, //AssrtArithExpr annot);
			List<AssrtArithExpr> annotexprs);

	AssrtLRecursion AssrtLRecursion(Token t, RecVarNode recvar, LProtoBlock block, //AssrtAssertion ass);
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass);
}

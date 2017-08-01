package org.scribble.ext.assrt.ast;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.MessageNode;
import org.scribble.ast.NonRoleParamDeclList;
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
import org.scribble.ext.assrt.ast.global.AssrtGMessageTransfer;
import org.scribble.ext.assrt.ast.global.AssrtGProtocolHeader;
import org.scribble.ext.assrt.ast.global.AssrtGRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLConnect;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.ext.assrt.ast.local.AssrtLProtocolHeader;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;


public interface AssrtAstFactory extends AstFactory
{
	// FIXME: should not be an AssrtAssertion -- should be just an (integer) var decl expr (which is not a bool expr)
	AssrtGProtocolHeader AssrtGProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, AssrtAssertion ass);

	AssrtGMessageTransfer AssrtGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion);
	AssrtGConnect AssrtGConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion assertion);
	AssrtGRecursion AssrtGRecursion(CommonTree source, RecVarNode recvar, GProtocolBlock block, AssrtAssertion ass);
	AssrtGContinue AssrtGContinue(CommonTree source, RecVarNode recvar, AssrtAssertion ass);

	AssrtAnnotDataTypeElem AssrtAnnotDataTypeElem(CommonTree source, AssrtVarNameNode varName, DataTypeNode dataType);

	AssrtLProtocolHeader AssrtLProtocolHeader(CommonTree source, LProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, AssrtAssertion ass);

	AssrtLSend AssrtLSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion);
	AssrtLConnect AssrtLConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, AssrtAssertion assertion);
	AssrtLRecursion AssrtLRecursion(CommonTree source, RecVarNode recvar, LProtocolBlock block, AssrtAssertion ass);
	AssrtLContinue AssrtLContinue(CommonTree source, RecVarNode recvar, AssrtAssertion ass);

	AssrtAssertion AssrtAssertion(CommonTree source, AssrtBoolFormula f);
}

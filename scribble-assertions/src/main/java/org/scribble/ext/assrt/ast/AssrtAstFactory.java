package org.scribble.ext.assrt.ast;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.MessageNode;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.ast.global.AssrtGMessageTransfer;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;


public interface AssrtAstFactory extends AstFactory
{
	AssrtGMessageTransfer AssrtGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion);
	AssrtLSend AssrtLSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion);

	AssrtAnnotDataTypeElem AssrtAnnotDataTypeElem(CommonTree source, AssrtVarNameNode varName, DataTypeNode dataType);

	//AssrtAssertion AssrtAssertion(CommonTree source, SmtFormula f);
	AssrtAssertion AssrtAssertion(CommonTree source, AssrtBoolFormula f);
}

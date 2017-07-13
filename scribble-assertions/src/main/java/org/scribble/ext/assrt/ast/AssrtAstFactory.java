package org.scribble.ext.assrt.ast;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.MessageNode;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.assrt.ast.formula.SmtFormula;
import org.scribble.ext.assrt.ast.global.AssrtGMessageTransfer;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.ast.name.simple.AssrtVarNameNode;
import org.scribble.sesstype.kind.PayloadTypeKind;


public interface AssrtAstFactory extends AstFactory
{
	AssrtGMessageTransfer AssrtGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion);
	AssrtLSend AssrtLSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, AssrtAssertion assertion);

	<K extends PayloadTypeKind> AssrtAnnotDataTypeElem<K> AssrtAnnotPayloadElem(CommonTree source, AssrtVarNameNode varName, DataTypeNode dataType);

	AssrtAssertion AssrtAssertion(CommonTree source, SmtFormula f);
}

package org.scribble.ext.assrt.ast;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;

public interface AssrtStateVarDeclAnnotNode
{
	AssrtAssertion getAssertionChild();
	List<AssrtIntVarNameNode> getAnnotVarChildren();
	List<AssrtArithExpr> getAnnotExprChildren();

	default String annotToString()
	{
		Iterator<AssrtArithExpr> exprs = getAnnotExprChildren().iterator();
		AssrtAssertion ass = getAssertionChild();
		return " @(\""
				+ getAnnotVarChildren().stream().map(v -> v + " := " + exprs.next())
						.collect(Collectors.joining(", "))
				+ ")\" " + ((ass == null) ? "" : ass);
	}
}

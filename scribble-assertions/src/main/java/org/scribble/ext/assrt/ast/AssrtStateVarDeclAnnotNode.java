package org.scribble.ext.assrt.ast;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;

public interface AssrtStateVarDeclAnnotNode
{
	List<AssrtIntVarNameNode> getAnnotVars();
	List<AssrtArithExpr> getAnnotExprs();
	AssrtAssertion getAssertion();

	default String annotToString()
	{
		Iterator<AssrtArithExpr> exprs = getAnnotExprs().iterator();
		AssrtAssertion ass = getAssertion();
		return " @(\"" + getAnnotVars().stream().map(v -> v + " := " + exprs.next()).collect(Collectors.joining(", ")) +  ")\" "
				+ ((ass == null) ? "" : ass);
	}
}

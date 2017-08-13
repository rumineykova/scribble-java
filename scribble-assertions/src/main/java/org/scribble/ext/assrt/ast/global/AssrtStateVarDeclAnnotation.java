package org.scribble.ext.assrt.ast.global;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;

public interface AssrtStateVarDeclAnnotation
{
	List<AssrtIntVarNameNode> getAnnotVars();
	List<AssrtArithExpr> getAnnotExprs();

	default String annotToString()
	{
		Iterator<AssrtArithExpr> exprs = getAnnotExprs().iterator();
		return " @" + getAnnotVars().stream().map(v -> v + " := " + exprs.next()).collect(Collectors.joining(", ")) +  ";";
	}
}

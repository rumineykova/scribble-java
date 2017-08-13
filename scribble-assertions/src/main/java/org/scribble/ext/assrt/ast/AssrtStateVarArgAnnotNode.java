package org.scribble.ext.assrt.ast;

import java.util.List;
import java.util.stream.Collectors;

public interface AssrtStateVarArgAnnotNode
{
	List<AssrtArithExpr> getAnnotExprs();

	default String annotToString()
	{
		return " @" + getAnnotExprs().stream().map(Object::toString).collect(Collectors.joining(", ")) +  ";";
	}
}

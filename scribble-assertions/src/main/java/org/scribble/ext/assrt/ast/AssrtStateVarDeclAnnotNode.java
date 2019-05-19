package org.scribble.ext.assrt.ast;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;

// ProtoHeader or Recursion
public interface AssrtStateVarDeclAnnotNode
{
	CommonTree getExtChild();
	AssrtAssertion getAssertionChild();
	List<AssrtIntVarNameNode> getAnnotVarChildren();
	List<AssrtArithExpr> getAnnotExprChildren();

	default String annotToString()
	{
		CommonTree ext = getExtChild();
		if (ext == null)
		{
			return "";
		}
		Iterator<AssrtArithExpr> exprs = getAnnotExprChildren().iterator();
		AssrtAssertion ass = getAssertionChild();
		return " @(\""
				+ getAnnotVarChildren().stream().map(v -> v + " := " + exprs.next())
						.collect(Collectors.joining(", "))
				+ "\") " + ((ass == null) ? "" : ass);
	}
}

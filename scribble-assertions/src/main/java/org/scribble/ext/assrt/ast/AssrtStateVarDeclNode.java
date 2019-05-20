package org.scribble.ext.assrt.ast;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;

// ProtoHeader or Recursion
public interface AssrtStateVarDeclNode
{
	CommonTree getAnnotChild();
	AssrtBExprNode getAnnotAssertChild();
	List<AssrtIntVarNameNode> getAnnotVarChildren();
	List<AssrtAExprNode> getAnnotExprChildren();

	default String annotToString()
	{
		CommonTree ext = getAnnotChild();
		if (ext == null)
		{
			return "";
		}
		Iterator<AssrtAExprNode> exprs = getAnnotExprChildren().iterator();
		AssrtBExprNode ass = getAnnotAssertChild();
		return " @(\""
				+ getAnnotVarChildren().stream().map(v -> v + " := " + exprs.next())
						.collect(Collectors.joining(", "))
				+ "\") " + ((ass == null) ? "" : ass);
	}
}

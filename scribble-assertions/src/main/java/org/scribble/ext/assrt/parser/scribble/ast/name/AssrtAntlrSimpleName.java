package org.scribble.ext.assrt.parser.scribble.ast.name;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.core.type.kind.AssrtIntVarNameKind;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;

public class AssrtAntlrSimpleName  // Cf. AntlrSimpleName
{
	public static AssrtIntVarNameNode toVarNameNode(CommonTree ct, AssrtAstFactory af)
	{
		return (AssrtIntVarNameNode) af.SimpleNameNode(ct, AssrtIntVarNameKind.KIND, AntlrSimpleName.getName(ct));
	}
}

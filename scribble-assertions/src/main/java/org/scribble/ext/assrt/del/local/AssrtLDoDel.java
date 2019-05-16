package org.scribble.ext.assrt.del.local;

import org.scribble.ast.ScribNode;
import org.scribble.ast.name.qualified.ProtoNameNode;
import org.scribble.core.type.kind.Local;
import org.scribble.del.local.LDoDel;
import org.scribble.ext.assrt.ast.local.AssrtLDo;
import org.scribble.util.ScribException;
import org.scribble.visit.NameDisambiguator;

public class AssrtLDoDel extends LDoDel
{
	@Override
	public ScribNode leaveDisambiguation(ScribNode child,
			NameDisambiguator disamb, ScribNode visited) throws ScribException
	{
		AssrtLDo doo = (AssrtLDo) visited;
		ProtoNameNode<Local> proto = disambProtoNameNode(disamb,
				doo.getProtoNameChild());  // Doesn't keep the original namenode del
		return doo.reconstruct(proto, doo.getRoleListChild(),
				doo.getNonRoleListChild(), doo.getAnnotExprChildren());
	}
}

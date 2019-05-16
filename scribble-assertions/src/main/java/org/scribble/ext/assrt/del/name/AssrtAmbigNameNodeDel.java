package org.scribble.ext.assrt.del.name;

import org.scribble.ast.ScribNode;
import org.scribble.ast.name.simple.AmbigNameNode;
import org.scribble.core.type.name.AmbigName;
import org.scribble.del.name.simple.AmbigNameNodeDel;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.visit.wf.AssrtNameDisambiguator;
import org.scribble.util.ScribException;
import org.scribble.visit.NameDisambiguator;

public class AssrtAmbigNameNodeDel extends AmbigNameNodeDel
{
	public AssrtAmbigNameNodeDel()
	{

	}

	// Currently only in "message positions (see Scribble.g ambiguousname)
	@Override
	public ScribNode leaveDisambiguation(ScribNode child,
			NameDisambiguator disamb, ScribNode visited) throws ScribException
	{
		AmbigNameNode ann = (AmbigNameNode) visited;
		AmbigName name = ann.toName();

		return ((AssrtNameDisambiguator) disamb).isVarnameInScope(name.toString())
				? ((AssrtAstFactory) disamb.job.config.af)
						.AssrtIntVarNameNode(ann.token, name.getLastElement())  // CHECKME: token OK?
				: super.leaveDisambiguation(child, disamb, visited);
	}
}

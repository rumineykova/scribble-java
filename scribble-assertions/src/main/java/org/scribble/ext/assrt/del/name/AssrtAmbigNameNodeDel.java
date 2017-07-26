package org.scribble.ext.assrt.del.name;

import org.scribble.ast.ScribNode;
import org.scribble.ast.name.simple.AmbigNameNode;
import org.scribble.del.name.AmbigNameNodeDel;
import org.scribble.ext.assrt.type.kind.AssrtVarNameKind;
import org.scribble.ext.assrt.visit.wf.AssrtNameDisambiguator;
import org.scribble.main.ScribbleException;
import org.scribble.type.name.AmbigName;
import org.scribble.visit.wf.NameDisambiguator;

public class AssrtAmbigNameNodeDel extends AmbigNameNodeDel
{
	public AssrtAmbigNameNodeDel()
	{

	}

	// Currently only in "message positions (see Scribble.g ambiguousname)
	@Override
	public ScribNode leaveDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb, ScribNode visited) throws ScribbleException
	{
		AmbigNameNode ann = (AmbigNameNode) visited;
		AmbigName name = ann.toName();

		return ((AssrtNameDisambiguator) disamb).isVarnameInScope(name.toString())
				? disamb.job.af.SimpleNameNode(ann.getSource(), AssrtVarNameKind.KIND, name.getLastElement())
				: super.leaveDisambiguation(parent, child, disamb, visited);
	}
}

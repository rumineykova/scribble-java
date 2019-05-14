package org.scribble.ext.assrt.del;

import org.scribble.ast.ScribNode;
import org.scribble.del.ScribDelBase;
import org.scribble.ext.assrt.ast.AssrtAnnotDataElem;
import org.scribble.ext.assrt.visit.wf.AssrtNameDisambiguator;
import org.scribble.main.ScribbleException;
import org.scribble.visit.wf.NameDisambiguator;

public class AssrtAnnotDataTypeElemDel extends ScribDelBase
{
	@Override
	public void enterDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb) throws ScribbleException
	{
		AssrtAnnotDataElem payload = (AssrtAnnotDataElem) child;
		((AssrtNameDisambiguator) disamb).addAnnotPaylaod(payload.var.toString());
	}
}

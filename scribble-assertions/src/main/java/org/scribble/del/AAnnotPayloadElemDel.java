package org.scribble.del;

import org.scribble.ast.AAnnotPayloadElem;
import org.scribble.ast.ScribNode;
import org.scribble.main.ScribbleException;
import org.scribble.visit.wf.NameDisambiguator;

public class AAnnotPayloadElemDel extends ScribDelBase
{
	@Override
	public void enterDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb) throws ScribbleException
	{
		AAnnotPayloadElem<?> payload = (AAnnotPayloadElem<?>) child;
		disamb.addAnnotPaylaod(payload.varName.toString());
	}
}

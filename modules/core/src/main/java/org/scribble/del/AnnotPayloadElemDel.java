package org.scribble.del;

import org.scribble.ast.AnnotPayloadElem;
import org.scribble.ast.ScribNode;
import org.scribble.main.ScribbleException;
import org.scribble.visit.wf.NameDisambiguator;

public class AnnotPayloadElemDel extends ScribDelBase
{
	@Override
	public void enterDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb) throws ScribbleException
	{
		AnnotPayloadElem<?> payload = (AnnotPayloadElem<?>) child;
		disamb.addAnnotPaylaod(payload.varName.toString());
	}
}

package org.scribble.ext.assrt.del;

import org.scribble.ast.ScribNode;
import org.scribble.del.ScribDelBase;
import org.scribble.ext.assrt.ast.AssrtAnnotDataElem;
import org.scribble.ext.assrt.visit.AssrtNameDisambiguator;
import org.scribble.util.ScribException;
import org.scribble.visit.NameDisambiguator;

public class AssrtAnnotDataTypeElemDel extends ScribDelBase
{
	@Override
	public void enterDisambiguation(ScribNode child, NameDisambiguator disamb)
			throws ScribException
	{
		AssrtAnnotDataElem elem = (AssrtAnnotDataElem) child;
		((AssrtNameDisambiguator) disamb)
				.addAnnotPaylaod(elem.getVarNameChild().toString());
	}
}

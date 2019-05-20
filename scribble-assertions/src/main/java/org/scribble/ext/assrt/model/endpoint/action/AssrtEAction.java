package org.scribble.ext.assrt.model.endpoint.action;

import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;

public interface AssrtEAction
{
	AssrtBFormula getAssertion();
	
	default String assertionToString()
	{
		AssrtBFormula ass = getAssertion();
		return ass.equals(AssrtTrueFormula.TRUE) ? "" : ("@\"" + ass + "\"");
		//return ass.equals(AssrtTrueFormula.TRUE) ? "" : ("@" + ass + ";");
	}
}

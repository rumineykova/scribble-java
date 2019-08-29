package org.scribble.ext.assrt.model.endpoint.action;

import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;

public interface AssrtEAction
{
	AssrtBFormula getAssertion();
	
	default String assertionToString()
	{
		AssrtBFormula ass = getAssertion();
		//return ass.equals(AssrtTrueFormula.TRUE) ? "" : ("//@\"" + ass + "\"");
		//return "\"" + ass + "\"";
		return ass.toString();
	}
}

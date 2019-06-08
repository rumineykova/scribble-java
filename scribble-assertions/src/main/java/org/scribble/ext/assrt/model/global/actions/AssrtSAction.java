package org.scribble.ext.assrt.model.global.actions;

import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;

public interface AssrtSAction
{
	AssrtBFormula getAssertion();
	
	default String assertionToString()
	{
		AssrtBFormula ass = getAssertion();
		//return ass.equals(AssrtTrueFormula.TRUE) ? "" : ("@\"" + ass + "\"");
		//return "\"" + ass + "\"";
		return ass.toString();
	}
}

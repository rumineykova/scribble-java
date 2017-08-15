package org.scribble.ext.assrt.model.global.actions;

import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;

public interface AssrtSAction
{
	AssrtBoolFormula getAssertion();
	
	default String assertionToString()
	{
		AssrtBoolFormula ass = getAssertion();
		return ass.equals(AssrtTrueFormula.TRUE) ? "" : ("@\"" + ass + "\"");
		//return ass.equals(AssrtTrueFormula.TRUE) ? "" : ("@" + ass + ";");
	}
}

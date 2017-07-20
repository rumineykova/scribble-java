package org.scribble.ext.assrt.sesstype.formula;

import org.sosy_lab.java_smt.api.BooleanFormula;

// Binary boolean -- top-level formula of assertions
// N.B. equals/hashCode is only for "syntactic" comparison
public abstract class AssrtBoolFormula extends AssrtSmtFormula<BooleanFormula>
{
	public AssrtBoolFormula()
	{

	}
}

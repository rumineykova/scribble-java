package org.scribble.ext.assrt.sesstype.formula;

import org.sosy_lab.java_smt.api.BooleanFormula;

// FIXME: equals/hashCode? -- e.g., for AssrtESend/Receive?
// Binary boolean
// Top-level formula of assertions
public abstract class AssrtBoolFormula extends AssrtSmtFormula<BooleanFormula>
{
	public AssrtBoolFormula()
	{

	}
}

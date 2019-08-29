package org.scribble.ext.assrt.core.type.formula;

import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

public abstract class AssrtAFormula extends AssrtSmtFormula<IntegerFormula>
{
	@Override
	public abstract AssrtAFormula squash();

	// Factor out with AssrtBFormula?
	@Override
	public abstract AssrtAFormula subs(AssrtIntVarFormula old, 
			AssrtIntVarFormula neu);
}

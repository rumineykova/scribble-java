package org.scribble.ext.assrt.type.formula;

import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

public abstract class AssrtArithFormula extends AssrtSmtFormula<IntegerFormula>
{
	public abstract AssrtArithFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu);
	
	@Override
	public abstract AssrtArithFormula squash();
}

package org.scribble.ext.assrt.core.type.formula;

import org.sosy_lab.java_smt.api.BooleanFormula;

// Binary boolean -- top-level formula of assertions
// N.B. equals/hashCode is only for "syntactic" comparison
public abstract class AssrtBFormula extends AssrtSmtFormula<BooleanFormula>
{
	public AssrtBFormula()
	{

	}
	
	@Override
	public abstract AssrtBFormula squash();

	@Override
	public abstract AssrtBFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu);
	
	public abstract AssrtBFormula getCnf();
	@Deprecated
	public abstract boolean isNF(AssrtBinBFormula.Op top);
	public abstract boolean hasOp(AssrtBinBFormula.Op op);
}

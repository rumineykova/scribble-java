package org.scribble.ext.assrt.core.type.formula;

import org.sosy_lab.java_smt.api.Formula;

// N.B. F is kind of the children formulae (not the parent, this)
public interface AssrtBinFormula<F extends Formula> 
{
	AssrtSmtFormula<F> getLeft();
	AssrtSmtFormula<F> getRight();
}

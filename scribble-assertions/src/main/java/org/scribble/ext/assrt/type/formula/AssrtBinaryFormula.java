package org.scribble.ext.assrt.type.formula;

import org.sosy_lab.java_smt.api.Formula;

// N.B. F is kind of the children formulae (not the parent, this)
public interface AssrtBinaryFormula<F extends Formula> 
{
	AssrtSmtFormula<F> getLeft();
	AssrtSmtFormula<F> getRight();
}

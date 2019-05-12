package org.scribble.ext.assrt.ast;

import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
import org.sosy_lab.java_smt.api.Formula;

public interface AssrtFormulaNode
{
	AssrtSmtFormula<? extends Formula> getFormula();
}

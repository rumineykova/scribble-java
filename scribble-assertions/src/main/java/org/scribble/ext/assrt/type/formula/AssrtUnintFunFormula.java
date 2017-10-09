package org.scribble.ext.assrt.type.formula;

import java.util.List;

import org.scribble.ext.assrt.type.name.AssrtSort;
import org.sosy_lab.java_smt.api.Formula;

public interface AssrtUnintFunFormula<F extends Formula> 
{
	List<AssrtSort> getParamSorts();  // FIXME: maybe should not be here -- formula should be arg exprs only (sorts given by member decl)
	AssrtSort getReturnSort();
}

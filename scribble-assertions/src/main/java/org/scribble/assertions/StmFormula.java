package org.scribble.assertions;
import java.util.Set;

import org.sosy_lab.java_smt.api.Formula;

public abstract class StmFormula {

	public abstract Formula toFormula() throws AssertionException; 
	public abstract Set<String> getVars();
}

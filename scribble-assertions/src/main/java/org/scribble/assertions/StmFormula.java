package org.scribble.assertions;
import java.util.Set;

import org.sosy_lab.java_smt.api.Formula;

public abstract class StmFormula {

	protected Formula formula; 
	protected abstract Formula toFormula() throws AssertionException; 
	public abstract Set<String> getVars();

	
	public Formula getFormula() throws AssertionException{
		if (this.formula==null) {
			this.formula = this.toFormula();
		}
		return this.formula; 
	}
}

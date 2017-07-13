package org.scribble.ext.assrt.ast.formula;

import java.util.Set;

import org.sosy_lab.java_smt.api.Formula;

public abstract class SmtFormula
{
	protected Formula formula;

	protected abstract Formula toFormula() throws AssertionParseException;

	public abstract Set<String> getVars();

	public Formula getFormula() throws AssertionParseException
	{
		if (this.formula == null)
		{
			this.formula = this.toFormula();
		}
		return this.formula;
	}
}

package org.scribble.ext.assrt.ast.formula;

import java.util.Set;

import org.sosy_lab.java_smt.api.Formula;

public abstract class AssrtSmtFormula<F extends Formula>
{
	protected F formula;

	public abstract Set<String> getVars();

	protected abstract F toJavaSmtFormula(); //throws AssertionParseException;

	public F getJavaSmtFormula() //throws AssertionParseException
	{
		if (this.formula == null)
		{
			this.formula = toJavaSmtFormula();
		}
		return this.formula;
	}
}

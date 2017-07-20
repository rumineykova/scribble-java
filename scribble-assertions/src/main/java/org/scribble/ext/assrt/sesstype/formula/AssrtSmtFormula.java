package org.scribble.ext.assrt.sesstype.formula;

import java.util.Set;

import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.sosy_lab.java_smt.api.Formula;

// FIXME: equals/hashCode? -- e.g., for AssrtESend/Receive?
// Formula is a "top-level" base class, cf. (Abstract)Name 
public abstract class AssrtSmtFormula<F extends Formula>
{
	protected F formula;  // Mostly not use for equals/hashCode -- except for AssrtLogFormula (and has to be used via toString)
	
	public abstract Set<AssrtDataTypeVar> getVars();

	protected abstract F toJavaSmtFormula(); //throws AssertionParseException;

	public F getJavaSmtFormula() //throws AssertionParseException
	{
		if (this.formula == null)
		{
			this.formula = toJavaSmtFormula();
		}
		return this.formula;
	}
	
	@Override
	public String toString()
	{
		return this.formula.toString();
	}
	
	// N.B. "syntactic" comparison -- should use additonal routines to do further, e.g., normal forms
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtSmtFormula<?>))
		{
			return false;
		}
		return ((AssrtSmtFormula<?>) o).canEqual(this);
	}

	protected abstract boolean canEqual(Object o);
	
	// In case subclasses do super
	@Override
	public int hashCode()
	{
		return 5869;
	}
}

package org.scribble.ext.assrt.core.type.formula;

import java.util.Set;

import org.scribble.ext.assrt.core.type.name.AssrtDataVar;
import org.sosy_lab.java_smt.api.Formula;

// FIXME: rename AnnotFormula
// FIXME: equals/hashCode? -- e.g., for AssrtESend/Receive? -- already done?
		// FIXME: still treated as String in some places, e.g., AssrtESend
// Formula is a "top-level" base class, cf. (Abstract)Name 
public abstract class AssrtSmtFormula<F extends Formula>  // FIXME: drop java_smt Formula
{
	protected F formula;   // "Cached" translation to JavaSMT API -- apart from AssrtLogFormula, which is just a wrapper for JavaSMT 
			// Mostly not used for equals/hashCode -- except for AssrtLogFormula (and has to be used via toString)

	// Currently no redundant quantifier elimination
	public abstract AssrtSmtFormula<F> squash();  // Needs to be here (not AssrtBoolFormula) because whole tree needs to be copied -- otherwise this.formula is inconsistent

	public abstract AssrtSmtFormula<F> subs(AssrtIntVarFormula old, AssrtIntVarFormula neu);

	public abstract String toSmt2Formula();  // Cf. toString -- but can be useful to separate, for debugging (and printing)

	public F getJavaSmtFormula() //throws AssertionParseException
	{
		if (this.formula == null)
		{
			this.formula = toJavaSmtFormula();
		}
		return this.formula;
	}

	 // FIXME: JSMT has a problem dealing with subsequent squashed formula, JSMT formula factory seems to cache var/expr translations
	protected abstract F toJavaSmtFormula(); //throws AssertionParseException;
	
	public abstract Set<AssrtDataVar> getIntVars();  // Change return to AssrtIntVarFormula? less confusing -- cf. AssrtEState.statevars
	
	@Override
	public String toString()
	{
		return this.formula.toString();  // Using JavaSMT to print
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

package org.scribble.ext.assrt.type.formula;

import java.util.Set;

import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.sosy_lab.java_smt.api.Formula;

// FIXME: equals/hashCode? -- e.g., for AssrtESend/Receive? -- already done?
		// FIXME: still treated as String in some places, e.g., AssrtESend
// Formula is a "top-level" base class, cf. (Abstract)Name 
public abstract class AssrtSmtFormula<F extends Formula>
{
	protected F formula;   // "Cached" translation to JavaSMT API -- apart from AssrtLogFormula, which is just a wrapper for JavaSMT 
			// Mostly not used for equals/hashCode -- except for AssrtLogFormula (and has to be used via toString)
	
	public abstract Set<AssrtDataTypeVar> getVars();  // FIXME: change return to AssrtIntVarFormula, less confusing

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

package org.scribble.ext.assrt.core.type.formula;

import java.util.Collections;
import java.util.Set;

import org.scribble.ext.assrt.core.type.name.AssrtIntVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;

// FIXME: declare singleton constant
public class AssrtTrueFormula extends AssrtBFormula
{
	public static final AssrtTrueFormula TRUE = new AssrtTrueFormula();
	
	private AssrtTrueFormula()
	{
		
	}

	@Override
	public AssrtBFormula getCnf()
	{
		return this;
	}

	@Override
	public boolean isNF(AssrtBinBFormula.Op op)
	{
		return true;
	}

	@Override
	public boolean hasOp(AssrtBinBFormula.Op op)
	{
		return false;
	}

	@Override
	public AssrtBFormula squash()
	{
		return this;
	}

	@Override
	public AssrtTrueFormula subs(AssrtIntVarFormula old, AssrtIntVarFormula neu)
	{
		return this;
	}
	
	@Override
	public String toSmt2Formula()
	{
		return "true";
	}
	
	@Override
	protected BooleanFormula toJavaSmtFormula() //throws AssertionParseException {
	{
		return JavaSmtWrapper.getInstance().bfm.makeTrue();
	}
	
	@Override
	public Set<AssrtIntVar> getIntVars()
	{
		return Collections.emptySet(); 
	}
	
	@Override
	public String toString()
	{
		return "True"; 
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtTrueFormula))
		{
			return false;
		}
		return super.equals(this);  // Does canEqual
	}
	
	@Override
	protected boolean canEqual(Object o)
	{
		return o instanceof AssrtTrueFormula;
	}

	@Override
	public int hashCode()
	{
		int hash = 5881;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
}

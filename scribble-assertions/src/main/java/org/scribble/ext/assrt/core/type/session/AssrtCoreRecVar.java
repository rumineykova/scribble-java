package org.scribble.ext.assrt.core.type.session;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.type.name.RecVar;


public abstract class AssrtCoreRecVar implements AssrtCoreType
{
	public final RecVar recvar;

	//public final AssrtArithFormula expr;
	public final List<AssrtArithFormula> annotexprs;
	
	//public AssrtCoreRecVar(RecVar var, AssrtArithFormula expr)
	public AssrtCoreRecVar(RecVar var, List<AssrtArithFormula> annotexprs)
	{
		this.recvar = var;
		this.annotexprs = Collections.unmodifiableList(annotexprs);
	}

	@Override 
	public String toString()
	{
		return this.recvar.toString()
				+ "<" + this.annotexprs.stream().map(e -> e.toString()).collect(Collectors.joining(", ")) + ">";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssrtCoreRecVar))
		{
			return false;
		}
		AssrtCoreRecVar them = (AssrtCoreRecVar) obj;
		return them.canEquals(this) && this.recvar.equals(them.recvar) && this.annotexprs.equals(them.annotexprs);
	}
	
	@Override
	public abstract boolean canEquals(Object o);

	@Override
	public int hashCode()
	{
		int hash = 6733;
		hash = 31*hash + this.recvar.hashCode();
		hash = 31*hash + this.annotexprs.hashCode();
		return hash;
	}
}

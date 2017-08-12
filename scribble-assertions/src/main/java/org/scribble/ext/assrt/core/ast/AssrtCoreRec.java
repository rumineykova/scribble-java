package org.scribble.ext.assrt.core.ast;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.type.name.RecVar;

public abstract class AssrtCoreRec<B extends AssrtCoreType> implements AssrtCoreType
{
	public final RecVar recvar;  // FIXME: RecVarNode?  (Cf. AssrtCoreAction.op/pay)
	public final B body;

	public final Map<AssrtDataTypeVar, AssrtArithFormula> annotvars;  // Int  // Non-null
	//public final AssrtArithFormula init;
	
	//public AssrtCoreRec(RecVar recvar, AssrtDataTypeVar annot, AssrtArithFormula init, B body)
	public AssrtCoreRec(RecVar recvar, Map<AssrtDataTypeVar, AssrtArithFormula> annotvars, B body)
	{
		this.recvar = recvar;
		this.annotvars = Collections.unmodifiableMap(annotvars);
		//this.init = init;
		this.body = body;
	}
	
	@Override
	public String toString()
	{
		//return "mu " + this.recvar + "(" + this.annot + " := " + this.init + ")" + "." + this.body;
		return "mu " + this.recvar + "(" + this.annotvars.entrySet().stream()
				.map(e -> e.getKey() + " := " + e.getValue()).collect(Collectors.joining(", "))+ ")" + "." + this.body;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreRec))
		{
			return false;
		}
		AssrtCoreRec<?> them = (AssrtCoreRec<?>) obj;
		return them.canEquals(this) && this.recvar.equals(them.recvar) 
				//&& this.annot.equals(them.annot) && this.init.equals(them.init) 
				&& this.annotvars.equals(them.annotvars)
				&& this.body.equals(them.body); // FIXME: check B kind is equal?
	}
	
	@Override
	public abstract boolean canEquals(Object o);
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.recvar.hashCode();
		result = prime * result + this.annotvars.hashCode();
		//result = prime * result + this.init.hashCode();
		result = prime * result + this.body.hashCode();
		//result = prime * result + ((body == null) ? 0 : body.hashCode());
		return result;
	}
}

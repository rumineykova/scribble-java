package org.scribble.ext.assrt.core.ast;

import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.type.name.RecVar;

public abstract class AssrtCoreRec<B extends AssrtCoreType> implements AssrtCoreType
{
	public final AssrtDataTypeVar annot;  // Int  // Non-null
	public final AssrtArithFormula init;
	
	public final RecVar recvar;  // FIXME: RecVarNode?  (Cf. AssrtCoreAction.op/pay)
	public final B body;
	
	public AssrtCoreRec(RecVar recvar, AssrtDataTypeVar annot, AssrtArithFormula init, B body)
	{
		this.recvar = recvar;
		this.annot = annot;
		this.init = init;
		this.body = body;
	}
	
	@Override
	public String toString()
	{
		return "mu " + this.recvar + "(" + this.annot + " := " + this.init + ")" + "." + this.body;
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
		return them.canEquals(this) && this.recvar.equals(them.recvar) && this.annot.equals(them.annot)
				&& this.init.equals(them.init) && this.body.equals(them.body); // FIXME: check B kind is equal?
	}
	
	@Override
	public abstract boolean canEquals(Object o);
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.recvar.hashCode();
		result = prime * result + this.annot.hashCode();
		result = prime * result + this.init.hashCode();
		result = prime * result + this.body.hashCode();
		//result = prime * result + ((body == null) ? 0 : body.hashCode());
		return result;
	}
}

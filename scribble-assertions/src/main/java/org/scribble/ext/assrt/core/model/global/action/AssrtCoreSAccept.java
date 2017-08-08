package org.scribble.ext.assrt.core.model.global.action;

import org.scribble.ext.assrt.model.global.actions.AssrtSAccept;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public class AssrtCoreSAccept extends AssrtSAccept
{
	// Annot needed -- e.g. mu X(x:=..) . mu Y(y:=..) ... X<123> -- rec var X will be discarded, so edge action needs to record which var is being updated
	public final AssrtDataTypeVar annot;  // Not null (by AssrtCoreGProtocolTranslator)
	public final AssrtArithFormula expr;

	public AssrtCoreSAccept(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf,
			AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		super(subj, obj, mid, payload, bf);
		this.annot = annot;
		this.expr = expr;
	}

	@Override
	public int hashCode()
	{
		int hash = 6947;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.annot.hashCode();
		hash = 31 * hash + this.expr.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreSAccept))
		{
			return false;
		}
		AssrtCoreSAccept as = (AssrtCoreSAccept) o;
		return super.equals(o)  // Does canEqual
				&& this.annot.equals(as.annot) && this.expr.equals(as.expr);
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtCoreSAccept;
	}
	
	@Override
	public String toString()
	{
		return super.toString()
				+ ((this.annot.toString().startsWith("_dum")) ? "" : "<" + this.annot + " := " + this.expr + ">");  // FIXME
	}
}

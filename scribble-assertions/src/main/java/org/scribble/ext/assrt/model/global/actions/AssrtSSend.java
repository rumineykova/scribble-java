package org.scribble.ext.assrt.model.global.actions;

import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.model.global.actions.SSend;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public class AssrtSSend extends SSend
{
	//public final AssrtAssertion assertion;  // Cf., e.g., AGMessageTransfer
	public final AssrtBoolFormula ass;  // Not null (cf. AssrtESend)

	public AssrtSSend(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		super(subj, obj, mid, payload);
		this.ass = bf;
	}

	@Override
	public int hashCode()
	{
		int hash = 5483;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.ass.toString().hashCode();  // FIXME: treating as String (cf. AssrtESend)
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtSSend))
		{
			return false;
		}
		AssrtSSend as = (AssrtSSend) o;
		return super.equals(o)  // Does canEqual
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String (cf. AssrtESend)
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtSSend;
	}
	
	@Override
	public String toString()
	{
		return super.toString()
				+ (this.ass.equals(AssrtTrueFormula.TRUE) ? "" : "@" + this.ass + ";");  // FIXME
	}
}

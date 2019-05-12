package org.scribble.ext.assrt.model.global.actions;

import org.scribble.core.model.global.actions.SSend;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;

public class AssrtSSend extends SSend implements AssrtSAction
{
	//public final AssrtAssertion assertion;  // Cf., e.g., AGMsgTransfer
	public final AssrtBoolFormula ass;  // Not null (cf. AssrtESend)

	public AssrtSSend(Role subj, Role obj, MsgId<?> mid, Payload payload,
			AssrtBoolFormula bf)
	{
		super(subj, obj, mid, payload);
		this.ass = bf;
	}

	@Override
	public AssrtBoolFormula getAssertion()
	{
		return this.ass;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + assertionToString();
				//+ (this.ass.equals(AssrtTrueFormula.TRUE) ? "" : "@" + this.ass + ";");  // FIXME
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
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtSSend;
	}
}

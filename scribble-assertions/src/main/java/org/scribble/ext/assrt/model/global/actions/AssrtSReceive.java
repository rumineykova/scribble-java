package org.scribble.ext.assrt.model.global.actions;

import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.model.global.actions.SReceive;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public class AssrtSReceive extends SReceive implements AssrtSAction
{
	//public final AssrtAssertion assertion;  // Cf., e.g., AGMessageTransfer
	public final AssrtBoolFormula ass;  // Cf., e.g., AGMessageTransfer  // Not null (cf. AssrtEReceive)

	public AssrtSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
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
		int hash = 5869;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.ass.toString().hashCode();  // FIXME: treating as String (cf. AssrtEReceive)
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtSReceive))
		{
			return false;
		}
		AssrtSReceive as = (AssrtSReceive) o;
		return super.equals(o)  // Does canEqual
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String (cf. AssrtEReceive)
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtSReceive;
	}
}

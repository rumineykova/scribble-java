package org.scribble.ext.assrt.model.global.actions;

import org.scribble.core.model.global.actions.SAcc;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;

// Duplicated from SReceive
public class AssrtSAcc extends SAcc implements AssrtSAction
{
	public final AssrtBoolFormula ass;  // Cf., e.g., AGMsgTransfer  // Not null (cf. AssrtEReceive)

	public AssrtSAcc(Role subj, Role obj, MsgId<?> mid, Payload payload,
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
	}

	@Override
	public int hashCode()
	{
		int hash = 6029;
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
		if (!(o instanceof AssrtSAcc))
		{
			return false;
		}
		AssrtSAcc as = (AssrtSAcc) o;
		return super.equals(o)  // Does canEqual
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String (cf. AssrtEReceive)
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtSAcc;
	}
}

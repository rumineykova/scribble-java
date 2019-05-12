package org.scribble.ext.assrt.model.global.actions;

import org.scribble.core.model.global.actions.SRecv;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;

public class AssrtSRecv extends SRecv implements AssrtSAction
{
	//public final AssrtAssertion assertion;  // Cf., e.g., AGMsgTransfer
	public final AssrtBoolFormula ass;  // Cf., e.g., AGMsgTransfer  // Not null (cf. AssrtEReceive)

	public AssrtSRecv(Role subj, Role obj, MsgId<?> mid, Payload payload,
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
		if (!(o instanceof AssrtSRecv))
		{
			return false;
		}
		AssrtSRecv as = (AssrtSRecv) o;
		return super.equals(o)  // Does canEqual
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String (cf. AssrtEReceive)
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtSRecv;
	}
}

package org.scribble.ext.assrt.model.global.actions;

import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.model.global.actions.SReceive;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public class AssrtSReceive extends SReceive
{
	public final AssrtAssertion assertion;  // Cf., e.g., AGMessageTransfer  // Not null (cf. AssrtEReceive)

	public AssrtSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtAssertion assertion)
	{
		super(subj, obj, mid, payload);
		this.assertion = assertion;
	}

	@Override
	public int hashCode()
	{
		int hash = 5869;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.assertion.hashCode();
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
				&& this.assertion.equals(as.assertion);
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtSReceive;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "@" + this.assertion + ";";
	}
}

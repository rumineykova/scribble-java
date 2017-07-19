package org.scribble.ext.assrt.model.global.actions;

import org.scribble.ext.assrt.ast.formula.AssrtBoolFormula;
import org.scribble.model.global.actions.SReceive;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public class AssrtSReceive extends SReceive
{
	//public final AssrtAssertion assertion;  // Cf., e.g., AGMessageTransfer
	public final AssrtBoolFormula bf;  // Cf., e.g., AGMessageTransfer  // Not null (cf. AssrtEReceive)

	public AssrtSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		super(subj, obj, mid, payload);
		this.bf = bf;
	}

	@Override
	public int hashCode()
	{
		int hash = 5869;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.bf.toString().hashCode();  // FIXME: treating as String (cf. AssrtEReceive)
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
				&& this.bf.toString().equals(as.bf.toString());  // FIXME: treating as String (cf. AssrtEReceive)
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtSReceive;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "@" + this.bf + ";";
	}
}

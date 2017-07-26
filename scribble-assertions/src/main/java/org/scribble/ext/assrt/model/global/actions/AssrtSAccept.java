package org.scribble.ext.assrt.model.global.actions;

import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.model.global.actions.SAccept;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

// Duplicated from SReceive
public class AssrtSAccept extends SAccept
{
	public final AssrtBoolFormula bf;  // Cf., e.g., AGMessageTransfer  // Not null (cf. AssrtEReceive)

	public AssrtSAccept(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		super(subj, obj, mid, payload);
		this.bf = bf;
	}

	@Override
	public int hashCode()
	{
		int hash = 6029;
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
		if (!(o instanceof AssrtSAccept))
		{
			return false;
		}
		AssrtSAccept as = (AssrtSAccept) o;
		return super.equals(o)  // Does canEqual
				&& this.bf.toString().equals(as.bf.toString());  // FIXME: treating as String (cf. AssrtEReceive)
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtSAccept;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "@" + this.bf + ";";
	}
}

package org.scribble.ext.assrt.model.global.actions;

import org.scribble.ext.assrt.sesstype.formula.AssrtBoolFormula;
import org.scribble.model.global.actions.SConnect;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public class AssrtSConnect extends SConnect
{
	public final AssrtBoolFormula bf;  // Not null (cf. AssrtESend)

	public AssrtSConnect(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		super(subj, obj, mid, payload);
		this.bf = bf;
	}

	@Override
	public int hashCode()
	{
		int hash = 6029;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.bf.toString().hashCode();  // FIXME: treating as String (cf. AssrtESend)
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtSConnect))
		{
			return false;
		}
		AssrtSConnect as = (AssrtSConnect) o;
		return super.equals(o)  // Does canEqual
				&& this.bf.toString().equals(as.bf.toString());  // FIXME: treating as String (cf. AssrtESend)
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtSConnect;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "@" + this.bf + ";";
	}
}

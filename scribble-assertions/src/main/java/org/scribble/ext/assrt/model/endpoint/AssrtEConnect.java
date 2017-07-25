package org.scribble.ext.assrt.model.endpoint;

import org.scribble.ext.assrt.model.global.actions.AssrtSConnect;
import org.scribble.ext.assrt.sesstype.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.sesstype.formula.AssrtTrueFormula;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.endpoint.actions.EConnect;
import org.scribble.model.global.SModelFactory;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

// Duplicated from AssrtESend
// FIXME: treating assertion as String -- assertion currently has no equals/hashCode itself
public class AssrtEConnect extends EConnect
{
	//public final AssrtAssertion assertion;  // Cf., e.g., ALSend
	public final AssrtBoolFormula ass;  // Not null -- empty set to True by parsing

	public AssrtEConnect(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula assertion)
	{
		super(ef, peer, mid, payload);
		this.ass = assertion;
	}

	// HACK: replace assertion by True
	public AssrtEConnect toEConnectTrue()  // FIXME: for model building, currently need send assertion to match (syntactical equal) receive assertion (which is always True) to be fireable
	{
		return ((AssrtEModelFactory) this.ef).newAssrtEConnect(this.peer, this.mid, this.payload, AssrtTrueFormula.TRUE);
	}

	@Override
	public AssrtEAccept toDual(Role self)
	{
		return ((AssrtEModelFactory) this.ef).newAssrtEAccept(self, this.mid, this.payload, this.ass);
	}

	@Override
	public AssrtSConnect toGlobal(SModelFactory sf, Role self)
	{
		return new AssrtSConnect(self, this.peer, this.mid, this.payload, this.ass);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 6011;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.ass.toString().hashCode();  // FIXME: treating as String
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtEConnect))
		{
			return false;
		}
		AssrtEConnect as = (AssrtEConnect) o;
		return super.equals(o)  // Does canEquals
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtEConnect;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "@" + this.ass + ";";
	}
}

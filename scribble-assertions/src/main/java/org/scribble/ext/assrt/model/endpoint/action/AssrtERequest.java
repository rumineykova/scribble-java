package org.scribble.ext.assrt.model.endpoint.action;

import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.model.global.AssrtSModelFactory;
import org.scribble.ext.assrt.model.global.actions.AssrtSRequest;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.endpoint.actions.ERequest;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

// Duplicated from AssrtESend
// FIXME: treating assertion as String -- assertion currently has no equals/hashCode itself
public class AssrtERequest extends ERequest implements AssrtEAction
{
	//public final AssrtAssertion assertion;  // Cf., e.g., ALSend
	public final AssrtBoolFormula ass;  // Not null -- empty set to True by parsing

	public AssrtERequest(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula ass)
	{
		super(ef, peer, mid, payload);
		this.ass = ass;
	}
	
	@Override
	public AssrtBoolFormula getAssertion()
	{
		return this.ass;
	}

	// HACK: replace assertion by True
	public AssrtERequest toTrueAssertion()  // FIXME: for model building, currently need send assertion to match (syntactical equal) receive assertion (which is always True) to be fireable
	{
		return ((AssrtEModelFactory) this.ef).newAssrtERequest(this.peer, this.mid, this.payload, AssrtTrueFormula.TRUE);
	}

	@Override
	public AssrtEAccept toDual(Role self)
	{
		return ((AssrtEModelFactory) this.ef).newAssrtEAccept(self, this.mid, this.payload, this.ass);
	}

	@Override
	public AssrtSRequest toGlobal(SModelFactory sf, Role self)
	{
		return ((AssrtSModelFactory) sf).newAssrtSRequest(self, this.peer, this.mid, this.payload, this.ass);
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
		if (!(o instanceof AssrtERequest))
		{
			return false;
		}
		AssrtERequest as = (AssrtERequest) o;
		return super.equals(o)  // Does canEquals
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtERequest;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "@" + this.ass + ";";
	}
}

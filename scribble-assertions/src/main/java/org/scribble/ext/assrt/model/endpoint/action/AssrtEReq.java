package org.scribble.ext.assrt.model.endpoint.action;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.actions.EReq;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.model.global.AssrtSModelFactory;
import org.scribble.ext.assrt.model.global.actions.AssrtSReq;

// Duplicated from AssrtESend
// FIXME: treating assertion as String -- assertion currently has no equals/hashCode itself
public class AssrtEReq extends EReq implements AssrtEAction
{
	//public final AssrtAssertion assertion;  // Cf., e.g., ALSend
	public final AssrtBFormula ass;  // Not null -- empty set to True by parsing

	public AssrtEReq(ModelFactory mf, Role peer, MsgId<?> mid, Payload payload,
			AssrtBFormula ass)
	{
		super(mf, peer, mid, payload);
		this.ass = ass;
	}
	
	@Override
	public AssrtBFormula getAssertion()
	{
		return this.ass;
	}

	// HACK: replace assertion by True
	public AssrtEReq toTrueAssertion()  // FIXME: for model building, currently need send assertion to match (syntactical equal) receive assertion (which is always True) to be fireable
	{
		return ((AssrtEModelFactory) this.mf.local).newAssrtERequest(this.peer,
				this.mid, this.payload, AssrtTrueFormula.TRUE);
	}

	@Override
	public AssrtEAcc toDual(Role self)
	{
		return ((AssrtEModelFactory) this.mf.local).newAssrtEAccept(self, this.mid,
				this.payload, this.ass);
	}

	@Override
	public AssrtSReq toGlobal(Role self)
	{
		return ((AssrtSModelFactory) this.mf.global).newAssrtSRequest(self, this.peer,
				this.mid, this.payload, this.ass);
	}
	
	@Override
	public String toString()
	{
		return super.toString() + assertionToString();
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
		if (!(o instanceof AssrtEReq))
		{
			return false;
		}
		AssrtEReq as = (AssrtEReq) o;
		return super.equals(o)  // Does canEquals
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtEReq;
	}
}

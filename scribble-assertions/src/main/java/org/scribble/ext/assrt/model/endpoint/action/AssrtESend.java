package org.scribble.ext.assrt.model.endpoint.action;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.actions.ESend;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.model.global.AssrtSModelFactory;
import org.scribble.ext.assrt.model.global.actions.AssrtSSend;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;

// FIXME: treating assertion as String -- assertion now has equals/hashCode
public class AssrtESend extends ESend implements AssrtEAction
{
	//public final AssrtAssertion assertion;  // Cf., e.g., ALSend
	public final AssrtBoolFormula ass;  // Not null -- empty set to True by parsing

	public AssrtESend(ModelFactory mf, Role peer, MsgId<?> mid, Payload payload,
			AssrtBoolFormula ass)
	{
		super(mf, peer, mid, payload);
		this.ass = ass;
	}
	
	@Override
	public AssrtBoolFormula getAssertion()
	{
		return this.ass;
	}

	// HACK: replace assertion by True
	public AssrtESend toTrueAssertion()  // FIXME: for model building, currently need send assertion to match (syntactical equal) receive assertion (which is always True) to be fireable
	{
		return ((AssrtEModelFactory) this.mf.local).newAssrtESend(this.peer, this.mid,
				this.payload, AssrtTrueFormula.TRUE);
	}

	@Override
	public AssrtERecv toDual(Role self)
	{
		//return super.toDual(self);  // FIXME: assertion? -- currently ignoring assertions for model building -- no: cannot ignore now, need assertions on actions to check history sensitivity
		return ((AssrtEModelFactory) this.mf.local).newAssrtEReceive(self, this.mid,
				this.payload, this.ass);
	}

	@Override
	public AssrtSSend toGlobal(Role self)
	{
		return ((AssrtSModelFactory) this.mf.global).newAssrtSSend(self, this.peer,
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
		int hash = 5501;
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
		if (!(o instanceof AssrtESend))
		{
			return false;
		}
		AssrtESend as = (AssrtESend) o;
		return super.equals(o)  // Does canEquals
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtESend;
	}
}

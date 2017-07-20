package org.scribble.ext.assrt.model.endpoint;

import org.scribble.ext.assrt.model.global.actions.AssrtSSend;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.sesstype.formula.AssrtBoolFormula;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.model.global.SModelFactory;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

// FIXME: treating assertion as String -- assertion currently has no equals/hashCode itself
public class AssrtESend extends ESend
{
	//public final AssrtAssertion assertion;  // Cf., e.g., ALSend
	public final AssrtBoolFormula bf;  // Not null -- empty set to True by parsing

	public AssrtESend(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula assertion)
	{
		super(ef, peer, mid, payload);
		this.bf = assertion;
	}

	// Change assertion to True
	public AssrtESend toESendTrue()  // FIXME HACK: for model building, currently need send assertion to match (syntactical equal) receive assertion (which is always True) to be fireable
	{
		return ((AssrtEModelFactory) this.ef).newAssrtESend(this.peer, this.mid, this.payload, AssrtFormulaFactory.AssrtTrueFormula());
	}

	@Override
	public AssrtEReceive toDual(Role self)
	{
		//return super.toDual(self);  // FIXME: assertion? -- currently ignoring assertions for model building -- no: cannot ignore now, need assertions on actions to check history sensitivity
		return ((AssrtEModelFactory) this.ef).newAssrtEReceive(self, this.mid, this.payload, this.bf);
	}

	@Override
	public AssrtSSend toGlobal(SModelFactory sf, Role self)
	{
		return new AssrtSSend(self, this.peer, this.mid, this.payload, this.bf);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 5501;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.bf.toString().hashCode();  // FIXME: treating as String
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
				&& this.bf.toString().equals(as.bf.toString());  // FIXME: treating as String
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtESend;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "@" + this.bf + ";";
	}
}

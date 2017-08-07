package org.scribble.ext.assrt.model.endpoint.action;

import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.model.global.AssrtSModelFactory;
import org.scribble.ext.assrt.model.global.actions.AssrtSReceive;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public class AssrtEReceive extends EReceive implements AssrtEAction
{
	//public final AssrtAssertion assertion;  // Cf., e.g., ALSend
	public final AssrtBoolFormula ass;  // Not null -- empty set to True by parsing

	public AssrtEReceive(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		super(ef, peer, mid, payload);
		this.ass = bf;
	}
	
	@Override
	public AssrtBoolFormula getAssertion()
	{
		return this.ass;
	}
	
	// FIXME: syntactic equality as "construtive" duality for assertion actions? -- cf. p50 Def D.3 A implies B
	@Override
	public AssrtESend toDual(Role self)
	{
		//throw new RuntimeException("[assrt-core] Shouldn't get here: " + this);
		return ((AssrtEModelFactory) this.ef).newAssrtESend(self, this.mid, this.payload, this.ass);
	}

	@Override
	public AssrtSReceive toGlobal(SModelFactory sf, Role self)
	{
		return ((AssrtSModelFactory) sf).newAssrtSReceive(self, this.peer, this.mid, this.payload, this.ass);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 5851;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.ass.toString().hashCode();  // FIXME: treating as String (cf. AssrtESend)
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtEReceive))
		{
			return false;
		}
		AssrtEReceive as = (AssrtEReceive) o;
		return super.equals(o)  // Does canEquals
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String (cf. AssrtESend)
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtEReceive;
	}
	
	@Override
	public String toString()
	{
		return super.toString()
				+ (this.ass.equals(AssrtTrueFormula.TRUE) ? "" : "@" + this.ass + ";");  // FIXME
	}
}

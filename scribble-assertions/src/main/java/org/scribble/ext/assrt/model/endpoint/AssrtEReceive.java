package org.scribble.ext.assrt.model.endpoint;

import org.scribble.ext.assrt.ast.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.model.global.actions.AssrtSReceive;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.global.SModelFactory;
import org.scribble.model.global.actions.SReceive;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public class AssrtEReceive extends EReceive
{
	//public final AssrtAssertion assertion;  // Cf., e.g., ALSend
	public final AssrtBoolFormula bf;  // Not null -- empty set to True by parsing

	public AssrtEReceive(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		super(ef, peer, mid, payload);
		this.bf = bf;
	}

	@Override
	public SReceive toGlobal(SModelFactory sf, Role self)
	{
		return new AssrtSReceive(self, this.peer, this.mid, this.payload, this.bf);
	}
	
	// FIXME: syntactic equality as "construtive" duality for assertion actions? -- cf. p50 Def D.3 A implies B
	@Override
	public AssrtESend toDual(Role self)
	{
		//throw new RuntimeException("[assrt-core] Shouldn't get here: " + this);
		return ((AssrtEModelFactory) this.ef).newAssrtESend(self, this.mid, this.payload, this.bf);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 5851;
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
		if (!(o instanceof AssrtEReceive))
		{
			return false;
		}
		AssrtEReceive as = (AssrtEReceive) o;
		return super.equals(o)  // Does canEquals
				&& this.bf.toString().equals(as.bf.toString());  // FIXME: treating as String (cf. AssrtESend)
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtEReceive;
	}
	
	/*@Override
	public String toString()
	{
		return super.toString() + "@" + this.assertion + ";";
	}*/
}

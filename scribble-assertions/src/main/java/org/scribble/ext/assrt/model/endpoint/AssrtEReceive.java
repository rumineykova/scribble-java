package org.scribble.ext.assrt.model.endpoint;

import org.scribble.ext.assrt.ast.AssrtAssertion;
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
	public final AssrtAssertion assertion;  // Cf., e.g., ALSend  // Not null -- empty set to True by parsing
			// FIXME: should not be the AST node

	public AssrtEReceive(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtAssertion assertion)
	{
		super(ef, peer, mid, payload);
		this.assertion = assertion;
	}

	@Override
	public SReceive toGlobal(SModelFactory sf, Role self)
	{
		return new AssrtSReceive(self, this.peer, this.mid, this.payload, this.assertion);
	}
	
	// FIXME: syntactic equality as "construtive" duality for assertion actions? -- cf. p50 Def D.3 A implies B
	@Override
	public AssrtESend toDual(Role self)
	{
		//throw new RuntimeException("[assrt-core] Shouldn't get here: " + this);
		return ((AssrtEModelFactory) this.ef).newAssrtESend(self, this.mid, this.payload, this.assertion);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 5851;
		hash = 31 * hash + super.hashCode();
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
				&& this.assertion.equals(as.assertion);
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

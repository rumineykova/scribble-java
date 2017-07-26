package org.scribble.ext.assrt.model.endpoint;

import org.scribble.ext.assrt.model.global.actions.AssrtSAccept;
import org.scribble.ext.assrt.sesstype.formula.AssrtBoolFormula;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.endpoint.actions.EAccept;
import org.scribble.model.global.SModelFactory;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

// Duplicated from AssrtEreceive
public class AssrtEAccept extends EAccept implements AssrtEAction
{
	public final AssrtBoolFormula ass;  // Not null -- empty set to True by parsing

	public AssrtEAccept(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		super(ef, peer, mid, payload);
		this.ass = bf;
	}
	
	@Override
	public AssrtBoolFormula getAssertion()
	{
		return this.ass;
	}

	@Override
	public AssrtSAccept toGlobal(SModelFactory sf, Role self)
	{
		return new AssrtSAccept(self, this.peer, this.mid, this.payload, this.ass);
	}
	
	// FIXME: syntactic equality as "construtive" duality for assertion actions? -- cf. p50 Def D.3 A implies B
	@Override
	public AssrtEConnect toDual(Role self)
	{
		return ((AssrtEModelFactory) this.ef).newAssrtEConnect(self, this.mid, this.payload, this.ass);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 6029;
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
		if (!(o instanceof AssrtEAccept))
		{
			return false;
		}
		AssrtEAccept as = (AssrtEAccept) o;
		return super.equals(o)  // Does canEquals
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String (cf. AssrtESend)
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtEAccept;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "@" + this.ass + ";";
	}
}

package org.scribble.ext.assrt.model.endpoint.action;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.actions.ERecv;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.model.global.AssrtSModelFactory;
import org.scribble.ext.assrt.model.global.actions.AssrtSRecv;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;

public class AssrtERecv extends ERecv implements AssrtEAction
{
	//public final AssrtAssertion assertion;  // Cf., e.g., ALSend
	public final AssrtBoolFormula ass;  // Not null -- empty set to True by parsing

	public AssrtERecv(ModelFactory mf, Role peer, MsgId<?> mid, Payload payload,
			AssrtBoolFormula bf)
	{
		super(mf, peer, mid, payload);
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
		return ((AssrtEModelFactory) this.mf.local).newAssrtESend(self, this.mid,
				this.payload, this.ass);
	}

	@Override
	public AssrtSRecv toGlobal(Role self)
	{
		return ((AssrtSModelFactory) this.mf.global).newAssrtSReceive(self,
				this.peer, this.mid, this.payload, this.ass);
	}
	
	@Override
	public String toString()
	{
		return super.toString() + assertionToString();
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
		if (!(o instanceof AssrtERecv))
		{
			return false;
		}
		AssrtERecv as = (AssrtERecv) o;
		return super.equals(o)  // Does canEquals
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String (cf. AssrtESend)
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtERecv;
	}
}

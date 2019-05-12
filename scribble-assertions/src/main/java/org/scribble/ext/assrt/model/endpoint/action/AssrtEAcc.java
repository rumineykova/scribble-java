package org.scribble.ext.assrt.model.endpoint.action;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.actions.EAcc;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.model.global.AssrtSModelFactory;
import org.scribble.ext.assrt.model.global.actions.AssrtSAcc;

// Duplicated from AssrtEreceive
public class AssrtEAcc extends EAcc implements AssrtEAction
{
	public final AssrtBoolFormula ass;  // Not null -- empty set to True by parsing

	public AssrtEAcc(ModelFactory mf, Role peer, MsgId<?> mid, Payload payload,
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
	
	// CHECKME: syntactic equality as "constructive" duality for assertion actions? -- cf. p50 Def D.3 A implies B
	@Override
	public AssrtEReq toDual(Role self)
	{
		return ((AssrtEModelFactory) this.mf).newAssrtERequest(self, this.mid,
				this.payload, this.ass);
	}

	@Override
	public AssrtSAcc toGlobal(Role self)
	{
		return ((AssrtSModelFactory) this.mf).newAssrtSAccept(self, this.peer,
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
		if (!(o instanceof AssrtEAcc))
		{
			return false;
		}
		AssrtEAcc as = (AssrtEAcc) o;
		return super.equals(o)  // Does canEquals
				&& this.ass.toString().equals(as.ass.toString());  // FIXME: treating as String (cf. AssrtESend)
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtEAcc;
	}
}

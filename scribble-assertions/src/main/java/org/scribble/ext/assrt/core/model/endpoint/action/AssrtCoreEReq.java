package org.scribble.ext.assrt.core.model.endpoint.action;

import java.util.Collections;
import java.util.List;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelFactory;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSReq;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEReq;

public class AssrtCoreEReq extends AssrtEReq implements AssrtCoreEAction
{
	// Annot needed -- e.g. mu X(x:=..) . mu Y(y:=..) ... X<123> -- rec var X will be discarded, so edge action needs to record which var is being updated
	/*public final AssrtDataTypeVar annot;  // Not null (by AssrtCoreGProtocolTranslator)
	public final AssrtArithFormula expr;*/
	public final List<AssrtAFormula> stateexprs;

	public AssrtCoreEReq(ModelFactory mf, Role peer, MsgId<?> mid,
			Payload payload, AssrtBFormula ass, List<AssrtAFormula> stateexprs)
	{
		super(mf, peer, mid, payload, ass);
		this.stateexprs = Collections.unmodifiableList(stateexprs);
	}
	
	// HACK: replace assertion by True
	@Override
	public AssrtCoreEReq toTrueAssertion()  // FIXME: for model building, currently need send assertion to match (syntactical equal) receive assertion (which is always True) to be fireable
	{
		return ((AssrtCoreEModelFactory) this.mf.local).AssrtCoreEReq(this.peer,
				this.mid, this.payload, AssrtTrueFormula.TRUE, this.stateexprs);
	}

	@Override
	public AssrtCoreEAcc toDual(Role self)
	{
		return ((AssrtCoreEModelFactory) this.mf.local).AssrtCoreEAcc(self,
				this.mid, this.payload, this.ass, this.stateexprs);
	}

	@Override
	public AssrtCoreSReq toGlobal(Role self)
	{
		return ((AssrtCoreSModelFactory) this.mf.global).AssrtCoreSReq(self,
				this.peer, this.mid, this.payload, this.ass, this.stateexprs);
	}

	/*@Override
	public AssrtDataTypeVar getAnnotVar()
	{
		return this.annot;
	}*/

	@Override
	//public AssrtArithFormula getArithExpr()
	public List<AssrtAFormula> getStateExprs()
	{
		//return this.expr;
		return this.stateexprs;
	}
	
	@Override
	public String toString()
	{
		//return super.toString() + "@" + this.ass + ";";
		return super.toString() + stateExprsToString();  // "First", assertion must hold; "second" pass sexprs
				//+ ((this.annot.toString().startsWith("_dum")) ? "" : "<" + this.annot + " := " + this.expr + ">");  // FIXME
				//+ (this.stateexprs.isEmpty() ? "" : "<" + this.stateexprs.stream().map(Object::toString).collect(Collectors.joining(", ")) + ">");
		/*return this.obj + getCommSymbol() + this.mid + this.payload
				+ stateExprsToString() + assertionToString();*/
	}
	
	@Override
	public int hashCode()
	{
		int hash = 6907;
		hash = 31 * hash + super.hashCode();
		//hash = 31 * hash + this.annot.hashCode();
		hash = 31 * hash + this.stateexprs.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreEReq))
		{
			return false;
		}
		AssrtCoreEReq as = (AssrtCoreEReq) o;
		return super.equals(o)  // Does canEquals
				//&& this.annot.equals(as.annot)
				&& this.stateexprs.equals(as.stateexprs);
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreEReq;
	}
}

package org.scribble.ext.assrt.core.model.endpoint.action;

import java.util.Collections;
import java.util.List;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelFactory;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.model.endpoint.action.AssrtESend;

public class AssrtCoreESend extends AssrtESend implements AssrtCoreEAction
{
	// Annot needed -- e.g. mu X(x:=..) . mu Y(y:=..) ... X<123> -- rec var X will be discarded, so edge action needs to record which var is being updated -- no: now relying on surface syntax to only allow subprotos with proper var scoping and annotvar arity checks, etc.
	/*public final AssrtDataTypeVar annot;  // Not null (by AssrtCoreGProtocolTranslator)
	public final AssrtArithFormula expr;*/
	public final List<AssrtArithFormula> stateexprs;

	public AssrtCoreESend(ModelFactory mf, Role peer, MsgId<?> mid,
			Payload payload, AssrtBoolFormula ass, List<AssrtArithFormula> stateexprs)
	{
		super(mf, peer, mid, payload, ass);
		//this.annot = annot;
		this.stateexprs = Collections.unmodifiableList(stateexprs);
	}
	
	@Deprecated
	public ModelFactory getModelFactory()
	{
		return this.mf;
	}
	
	// HACK: replace assertion by True
	@Override
	public AssrtCoreESend toTrueAssertion()  // FIXME: for model building, currently need send assertion to match (syntactical equal) receive assertion (which is always True) to be fireable
	{
		return ((AssrtCoreEModelFactory) this.mf.local).newAssrtCoreESend(this.peer,
				this.mid, this.payload, AssrtTrueFormula.TRUE, Collections.emptyList());
	}

	@Override
	public AssrtCoreERecv toDual(Role self)
	{
		return ((AssrtCoreEModelFactory) this.mf.local).newAssrtCoreEReceive(self,
				this.mid, this.payload, this.ass, this.stateexprs);
	}

	@Override
	public AssrtCoreSSend toGlobal(Role self)
	{
		return ((AssrtCoreSModelFactory) this.mf.global).newAssrtCoreSSend(self,
				this.peer, this.mid, this.payload, this.ass, this.stateexprs);
	}

	/*@Override
	public AssrtDataTypeVar getAnnotVar()
	{
		return this.annot;
	}*/

	@Override
	//public AssrtArithFormula getArithExpr()
	public List<AssrtArithFormula> getStateExprs()
	{
		return this.stateexprs;
	}
	
	@Override
	public String toString()
	{
		//return super.toString() + "@" + this.ass + ";";
		return super.toString() + stateExprsToString();
				//+ ((this.annot.toString().startsWith("_dum")) ? "" : "<" + this.annot + " := " + this.annotexprs + ">");  // FIXME
				//+ (this.stateexprs.isEmpty() ? "" : "<" + this.stateexprs.stream().map(Object::toString).collect(Collectors.joining(", ")) + ">");
	}
	
	@Override
	public int hashCode()
	{
		int hash = 6779;
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
		if (!(o instanceof AssrtCoreESend))
		{
			return false;
		}
		AssrtCoreESend as = (AssrtCoreESend) o;
		return super.equals(o)  // Does canEquals
				//&& this.annot.equals(as.annot) 
				&& this.stateexprs.equals(as.stateexprs);
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreESend;
	}
}

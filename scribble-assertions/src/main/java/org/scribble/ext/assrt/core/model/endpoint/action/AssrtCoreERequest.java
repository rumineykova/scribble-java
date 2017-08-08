package org.scribble.ext.assrt.core.model.endpoint.action;

import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelFactory;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSRequest;
import org.scribble.ext.assrt.model.endpoint.action.AssrtERequest;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public class AssrtCoreERequest extends AssrtERequest implements AssrtCoreEAction
{
	// FIXME -- move to AssrtCoreEAction
	public static final AssrtDataTypeVar DUMMY_VAR = new AssrtDataTypeVar("_dum0");  // cf. AssrtCoreGProtocolTranslator::makeFreshDataTypeVar starts from 1
	public static final AssrtArithFormula ZERO = AssrtFormulaFactory.AssrtIntVal(0);

	// Annot needed -- e.g. mu X(x:=..) . mu Y(y:=..) ... X<123> -- rec var X will be discarded, so edge action needs to record which var is being updated
	public final AssrtDataTypeVar annot;  // Not null (by AssrtCoreGProtocolTranslator)
	public final AssrtArithFormula expr;

	public AssrtCoreERequest(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula ass,
			AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		super(ef, peer, mid, payload, ass);
		this.annot = annot;
		this.expr = expr;
	}
	
	// HACK: replace assertion by True
	@Override
	public AssrtCoreERequest toTrueAssertion()  // FIXME: for model building, currently need send assertion to match (syntactical equal) receive assertion (which is always True) to be fireable
	{
		return ((AssrtCoreEModelFactory) this.ef).newAssrtCoreERequest(this.peer, this.mid, this.payload, AssrtTrueFormula.TRUE, 
				DUMMY_VAR, ZERO);  // HACK FIXME
	}

	@Override
	public AssrtCoreEAccept toDual(Role self)
	{
		return ((AssrtCoreEModelFactory) this.ef).newAssrtCoreEAccept(self, this.mid, this.payload, this.ass, this.annot, this.expr);
	}

	@Override
	public AssrtCoreSRequest toGlobal(SModelFactory sf, Role self)
	{
		return ((AssrtCoreSModelFactory) sf).newAssrtCoreSRequest(self, this.peer, this.mid, this.payload, this.ass, this.annot, this.expr);
	}

	@Override
	public AssrtDataTypeVar getAnnotVar()
	{
		return this.annot;
	}

	@Override
	public AssrtArithFormula getArithExpr()
	{
		return this.expr;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 6907;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.annot.hashCode();
		hash = 31 * hash + this.expr.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreERequest))
		{
			return false;
		}
		AssrtCoreERequest as = (AssrtCoreERequest) o;
		return super.equals(o)  // Does canEquals
				&& this.annot.equals(as.annot) && this.expr.equals(as.expr);
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtCoreERequest;
	}
	
	@Override
	public String toString()
	{
		//return super.toString() + "@" + this.ass + ";";
		return super.toString()
				+ ((this.annot.toString().startsWith("_dum")) ? "" : "<" + this.annot + " := " + this.expr + ">");  // FIXME
	}
}

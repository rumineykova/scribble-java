package org.scribble.ext.assrt.core.model.endpoint.action;

import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelFactory;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.model.endpoint.action.AssrtESend;
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

public class AssrtCoreESend extends AssrtESend implements AssrtCoreEAction
{
	// HACK FIXME -- move tp AssrtCoreEAction
	public static final AssrtDataTypeVar DUMMY_VAR = new AssrtDataTypeVar("_dum");
	public static final AssrtArithFormula ZERO = AssrtFormulaFactory.AssrtIntVal(0);

	// Annot needed -- e.g. mu X(x:=..) . mu Y(y:=..) ... X<123> -- rec var X will be discarded, so edge action needs to record which var is being updated
	public final AssrtDataTypeVar annot;  // Not null (by AssrtCoreGProtocolTranslator)
	public final AssrtArithFormula expr;

	public AssrtCoreESend(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula ass,
			AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		super(ef, peer, mid, payload, ass);
		this.annot = annot;
		this.expr = expr;
	}
	
	// HACK: replace assertion by True
	@Override
	public AssrtCoreESend toTrueAssertion()  // FIXME: for model building, currently need send assertion to match (syntactical equal) receive assertion (which is always True) to be fireable
	{
		return ((AssrtCoreEModelFactory) this.ef).newAssrtCoreESend(this.peer, this.mid, this.payload, AssrtTrueFormula.TRUE, 
				DUMMY_VAR, ZERO);  // HACK FIXME
	}

	@Override
	public AssrtCoreEReceive toDual(Role self)
	{
		return ((AssrtCoreEModelFactory) this.ef).newAssrtCoreEReceive(self, this.mid, this.payload, this.ass, this.annot, this.expr);
	}

	@Override
	public AssrtCoreSSend toGlobal(SModelFactory sf, Role self)
	{
		return ((AssrtCoreSModelFactory) sf).newAssrtCoreSSend(self, this.peer, this.mid, this.payload, this.ass, this.annot, this.expr);
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
		int hash = 6779;
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
		if (!(o instanceof AssrtCoreESend))
		{
			return false;
		}
		AssrtCoreESend as = (AssrtCoreESend) o;
		return super.equals(o)  // Does canEquals
				&& this.annot.equals(as.annot) && this.expr.equals(as.expr);
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtCoreESend;
	}
	
	@Override
	public String toString()
	{
		//return super.toString() + "@" + this.ass + ";";
		return super.toString() + "<" + this.annot + " := " + this.expr + ">";  // FIXME
	}
}

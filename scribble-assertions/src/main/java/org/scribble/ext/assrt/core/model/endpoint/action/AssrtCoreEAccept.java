package org.scribble.ext.assrt.core.model.endpoint.action;

import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelFactory;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSAccept;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEAccept;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public class AssrtCoreEAccept extends AssrtEAccept implements AssrtCoreEAction
{
	// Annot needed -- e.g. mu X(x:=..) . mu Y(y:=..) ... X<123> -- rec var X will be discarded, so edge action needs to record which var is being updated
	public final AssrtDataTypeVar annot;  // Not null (by AssrtCoreGProtocolTranslator)
	public final AssrtArithFormula expr;
	
	public AssrtCoreEAccept(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		super(ef, peer, mid, payload, bf);
		this.annot = annot;
		this.expr = expr;
	}
	
	@Override
	public AssrtCoreERequest toDual(Role self)
	{
		return ((AssrtCoreEModelFactory) this.ef).newAssrtCoreERequest(self, this.mid, this.payload, this.ass, this.annot, this.expr);
	}

	@Override
	public AssrtCoreSAccept toGlobal(SModelFactory sf, Role self)
	{
		return ((AssrtCoreSModelFactory) sf).newAssrtCoreSAccept(self, this.peer, this.mid, this.payload, this.ass, this.annot, this.expr);
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
		int hash = 6911;
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
		if (!(o instanceof AssrtCoreEAccept))
		{
			return false;
		}
		AssrtCoreEAccept as = (AssrtCoreEAccept) o;
		return super.equals(o)  // Does canEquals
				&& this.annot.equals(as.annot) && this.expr.equals(as.expr);
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtCoreEAccept;
	}
	
	@Override
	public String toString()
	{
		//return super.toString() + "@" + this.ass + ";";
		return super.toString()
				+ ((this.annot.toString().startsWith("_dum")) ? "" : "<" + this.annot + " := " + this.expr + ">");  // FIXME
	}
}

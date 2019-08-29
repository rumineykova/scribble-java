package org.scribble.ext.assrt.core.model.global.action;

import java.util.List;

import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.model.global.actions.AssrtSRecv;

public class AssrtCoreSRecv extends AssrtSRecv implements AssrtCoreSAction
{
	// Annot needed -- e.g. mu X(x:=..) . mu Y(y:=..) ... X<123> -- rec var X will be discarded, so edge action needs to record which var is being updated
	/*public final AssrtDataTypeVar annot;  // Not null (by AssrtCoreGProtocolTranslator)
	public final AssrtArithFormula expr;*/

	public final List<AssrtAFormula> stateexprs;

	public AssrtCoreSRecv(Role subj, Role obj, MsgId<?> mid, Payload payload,
			AssrtBFormula bf, List<AssrtAFormula> stateexprs)
	{
		super(subj, obj, mid, payload, bf);
		//this.annot = annot;
		this.stateexprs = stateexprs;
	}

	@Override
	public List<AssrtAFormula> getStateExprs()
	{
		return this.stateexprs;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + stateExprsToString();  // "First", assertion must hold; "second" pass sexprs
				//+ ((this.annot.toString().startsWith("_dum")) ? "" : "<" + this.annot + " := " + this.expr + ">");  // FIXME
				//+ (this.stateexprs.isEmpty() ? "" : "<" + this.stateexprs.stream().map(Object::toString).collect(Collectors.joining(", ")) + ">");
		/*return this.subj + getCommSymbol() + this.obj + ":" + this.mid
				+ this.payload + stateExprsToString() + assertionToString();*/
	}

	@Override
	public int hashCode()
	{
		int hash = 6791;
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
		if (!(o instanceof AssrtCoreSRecv))
		{
			return false;
		}
		AssrtCoreSRecv as = (AssrtCoreSRecv) o;
		return super.equals(o)  // Does canEqual
				//&& this.annot.equals(as.annot)
				&& this.stateexprs.equals(as.stateexprs);
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreSRecv;
	}
}

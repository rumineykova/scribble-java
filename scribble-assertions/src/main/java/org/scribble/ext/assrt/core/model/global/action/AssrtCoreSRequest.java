package org.scribble.ext.assrt.core.model.global.action;

import java.util.List;

import org.scribble.ext.assrt.model.global.actions.AssrtSRequest;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public class AssrtCoreSRequest extends AssrtSRequest implements AssrtCoreSAction
{
	// Annot needed -- e.g. mu X(x:=..) . mu Y(y:=..) ... X<123> -- rec var X will be discarded, so edge action needs to record which var is being updated
	/*public final AssrtDataTypeVar annot;  // Not null (by AssrtCoreGProtocolTranslator)
	public final AssrtArithFormula expr;*/

	public final List<AssrtArithFormula> stateexprs;

	public AssrtCoreSRequest(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf,
			//AssrtDataTypeVar annot, AssrtArithFormula expr)
			List<AssrtArithFormula> stateexprs)
	{
		super(subj, obj, mid, payload, bf);
		//this.annot = annot;
		this.stateexprs = stateexprs;
	}

	@Override
	public List<AssrtArithFormula> getStateExprs()
	{
		return this.stateexprs;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + stateExprsToString();
				//+ ((this.annot.toString().startsWith("_dum")) ? "" : "<" + this.annot + " := " + this.expr + ">");  // FIXME
				//+ (this.stateexprs.isEmpty() ? "" : "<" + this.stateexprs.stream().map(Object::toString).collect(Collectors.joining(", ")) + ">");
	}

	@Override
	public int hashCode()
	{
		int hash = 6917;
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
		if (!(o instanceof AssrtCoreSRequest))
		{
			return false;
		}
		AssrtCoreSRequest as = (AssrtCoreSRequest) o;
		return super.equals(o)  // Does canEqual
				//&& this.annot.equals(as.annot)
				&& this.stateexprs.equals(as.stateexprs);
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtCoreSRequest;
	}
}

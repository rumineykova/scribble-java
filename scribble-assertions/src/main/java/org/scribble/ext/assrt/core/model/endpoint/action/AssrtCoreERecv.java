package org.scribble.ext.assrt.core.model.endpoint.action;

import java.util.Collections;
import java.util.List;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelFactory;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSRecv;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.model.endpoint.action.AssrtERecv;

public class AssrtCoreERecv extends AssrtERecv implements AssrtCoreEAction
{
	// Annot needed -- e.g. mu X(x:=..) . mu Y(y:=..) ... X<123> -- rec var X will be discarded, so edge action needs to record which var is being updated
	/*public final AssrtDataTypeVar annot;  // Not null (by AssrtCoreGProtocolTranslator)
	public final AssrtArithFormula expr;*/
	public final List<AssrtAFormula> stateexprs;  // N.B. state args ignored for recv fireable and firing (msgs don't carry state args)
	
	public AssrtCoreERecv(ModelFactory mf, Role peer, MsgId<?> mid,
			Payload payload, AssrtBFormula bf, List<AssrtAFormula> stateexprs)
	{
		super(mf, peer, mid, payload, bf);
		//this.annot = annot;list)
	
		this.stateexprs = Collections.unmodifiableList(stateexprs);
	}
	
	// msg does not carry state args -- recv getFireable and fire follows accordingly
	public AssrtCoreERecv dropStateArgs()
	{
		return ((AssrtCoreEModelFactory) this.mf.local).AssrtCoreERecv(this.peer, this.mid,
				this.payload, this.ass, Collections.emptyList());
	}
	
	@Override
	public AssrtCoreESend toDual(Role self)
	{
		return ((AssrtCoreEModelFactory) this.mf.local).AssrtCoreESend(self, this.mid,
				this.payload, this.ass, this.stateexprs);
	}

	@Override
	public AssrtCoreSRecv toGlobal(Role self)
	{
		return ((AssrtCoreSModelFactory) this.mf.global).AssrtCoreSRecv(self,
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
		int hash = 6763;
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
		if (!(o instanceof AssrtCoreERecv))
		{
			return false;
		}
		AssrtCoreERecv as = (AssrtCoreERecv) o;
		return super.equals(o)  // Does canEquals
				//&& this.annot.equals(as.annot)
				&& this.stateexprs.equals(as.stateexprs);
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreERecv;
	}
}

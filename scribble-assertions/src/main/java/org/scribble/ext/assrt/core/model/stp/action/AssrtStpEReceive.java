package org.scribble.ext.assrt.core.model.stp.action;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreERecv;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSRecv;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;

public class AssrtStpEReceive extends AssrtCoreERecv implements AssrtStpEAction
{
	public final Map<AssrtIntVarFormula, AssrtSmtFormula<?>> sigma;
	public final AssrtBoolFormula A;  // aliases this.ass
	
	public AssrtStpEReceive(ModelFactory mf, Role peer, MsgId<?> mid,
			Payload payload, Map<AssrtIntVarFormula, AssrtSmtFormula<?>> sigma,
			AssrtBoolFormula A)
	{
		super(mf, peer, mid, payload, A, Collections.emptyList());
		this.sigma = Collections.unmodifiableMap(sigma);
		this.A = A;
	}

	@Override
	public Map<AssrtIntVarFormula, AssrtSmtFormula<?>> getSigma()
	{
		return this.sigma;
	}
	
	@Override
	public AssrtStpESend toDual(Role self)
	{
		throw new RuntimeException("Shouldn't get in here: " + this);
	}

	@Override
	public AssrtCoreSRecv toGlobal(Role self)
	{
		throw new RuntimeException("Shouldn't get in here: " + this);
	}

	@Override
	public List<AssrtArithFormula> getStateExprs()
	{
		throw new RuntimeException("Shouldn't get in here: " + this);
	}
	
	@Override
	public String toString()
	{
		return this.obj + getCommSymbol() + this.mid + this.payload + "@\"" + this.A
				+ "\""
				+ (this.sigma.isEmpty() ? ""
						: "{" + this.sigma.entrySet().stream()
								.map(e -> e.getKey() + ":" + e.getValue())
								.collect(Collectors.joining("")) + "}");
	}
	
	@Override
	public int hashCode()
	{
		int hash = 7853;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.sigma.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtStpEReceive))
		{
			return false;
		}
		AssrtStpEReceive as = (AssrtStpEReceive) o;
		return super.equals(o)  // Does canEquals
				&& this.sigma.equals(as.sigma);
				//&& this.A.equals(as.A);  // Done by via this.ass
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtStpEReceive;
	}
}

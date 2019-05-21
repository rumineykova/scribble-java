package org.scribble.ext.assrt.core.type.session;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.ProtoKind;
import org.scribble.core.type.name.RecVar;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataVar;

public abstract class AssrtCoreRec<K extends ProtoKind, 
			B extends AssrtCoreSType<K>>  // Without Seq complication, take kinded Type directly
		extends AssrtCoreSTypeBase<K>
{
	public final RecVar recvar;  // FIXME: RecVarNode?  (Cf. AssrtCoreAction.op/pay)
	public final LinkedHashMap<AssrtDataVar, AssrtAFormula> annotvars;  // Int  // Non-null
	public final B body;
	public final AssrtBFormula ass;
	
	protected AssrtCoreRec(CommonTree source, RecVar recvar,
			LinkedHashMap<AssrtDataVar, AssrtAFormula> annotvars, B body,
			AssrtBFormula ass)
	{
		super(source);
		this.recvar = recvar;
		this.annotvars = new LinkedHashMap<>(annotvars);
		this.body = body;
		this.ass = ass;
	}
	
	@Override
	public String toString()
	{
		return "mu " + this.recvar + "("
				+ this.annotvars.entrySet().stream()
						.map(e -> e.getKey() + " := " + e.getValue()).collect(
								Collectors.joining(", "))
				+ ")" + this.ass + "." + this.body;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreRec))
		{
			return false;
		}
		AssrtCoreRec<?, ?> them = (AssrtCoreRec<?, ?>) o;
		return super.equals(o)  // Checks canEquals -- implicitly checks kind
				&& this.recvar.equals(them.recvar) 
				&& this.annotvars.equals(them.annotvars)
				&& this.body.equals(them.body)
				&& this.ass.equals(them.ass);
	}
	
	@Override
	public abstract boolean canEquals(Object o);
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.recvar.hashCode();
		result = prime * result + this.annotvars.hashCode();
		result = prime * result + this.body.hashCode();
		result = prime * result + this.ass.hashCode();
		return result;
	}
}

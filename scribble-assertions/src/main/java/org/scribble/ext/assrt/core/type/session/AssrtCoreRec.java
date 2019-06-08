package org.scribble.ext.assrt.core.type.session;

import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.ProtoKind;
import org.scribble.core.type.name.RecVar;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtIntVar;

public abstract class AssrtCoreRec<K extends ProtoKind, 
			B extends AssrtCoreSType<K, B>>  // Without Seq complication, take kinded Type directly
		extends AssrtCoreSTypeBase<K, B>
{
	public final RecVar recvar;  // CHECKME: RecVarNode?  (Cf. AssrtCoreAction.op/pay)
	public final B body;
	public final LinkedHashMap<AssrtIntVar, AssrtAFormula> statevars;  // Int  // Non-null  // CHECKME: factor out with AssrtCore(G)Protocol?
	public final AssrtBFormula assertion;
	
	protected AssrtCoreRec(CommonTree source, RecVar recvar,
			B body, LinkedHashMap<AssrtIntVar, AssrtAFormula> svars,
			AssrtBFormula ass)
	{
		super(source);
		this.recvar = recvar;
		this.statevars = new LinkedHashMap<>(svars);
		this.body = body;
		this.assertion = ass;
	}
	
	@Override
	public <T> Stream<T> assrtCoreGather(
			Function<AssrtCoreSType<K, B>, Stream<T>> f)
	{
		return Stream.concat(f.apply(this), this.body.assrtCoreGather(f));
	}
	
	@Override
	public String toString()
	{
		return "mu " + this.recvar + "("
				+ this.statevars.entrySet().stream()
						.map(x -> x.getKey() + " := " + x.getValue()).collect(
								Collectors.joining(", "))
				+ ")" + this.assertion + "." + this.body;
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
				&& this.body.equals(them.body)
				&& this.statevars.equals(them.statevars)
				&& this.assertion.equals(them.assertion);
	}
	
	@Override
	public abstract boolean canEquals(Object o);
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.recvar.hashCode();
		result = prime * result + this.body.hashCode();
		result = prime * result + this.statevars.hashCode();
		result = prime * result + this.assertion.hashCode();
		return result;
	}
}

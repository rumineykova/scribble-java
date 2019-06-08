package org.scribble.ext.assrt.core.type.session;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.ProtoKind;
import org.scribble.core.type.name.RecVar;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;


public abstract class AssrtCoreRecVar<K extends ProtoKind, 
			B extends AssrtCoreSType<K, B>>
		extends AssrtCoreSTypeBase<K, B>
{
	public final RecVar recvar;
	public final List<AssrtAFormula> stateexprs;
	
	protected AssrtCoreRecVar(CommonTree source, RecVar rv,
			List<AssrtAFormula> annotexprs)
	{
		super(source);
		this.recvar = rv;
		this.stateexprs = Collections.unmodifiableList(annotexprs);
	}
	
	@Override
	public <T> Stream<T> assrtCoreGather(
			Function<AssrtCoreSType<K, B>, Stream<T>> f)
	{
		return f.apply(this);
	}

	@Override 
	public String toString()
	{
		return this.recvar.toString()
				+ "<" + this.stateexprs.stream().map(x -> x.toString())  // CHECKME: factor out with AssrtCoreDo?
						.collect(Collectors.joining(", "))
				+ ">";
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof AssrtCoreRecVar))
		{
			return false;
		}
		AssrtCoreRecVar<?, ?> them = (AssrtCoreRecVar<?, ?>) o;
		return super.equals(o) // Checks canEquals -- implicitly checks kind
				&& this.recvar.equals(them.recvar)
				&& this.stateexprs.equals(them.stateexprs);
	}

	@Override
	public int hashCode()
	{
		int hash = 6733;
		hash = 31*hash + this.recvar.hashCode();
		hash = 31*hash + this.stateexprs.hashCode();
		return hash;
	}
}

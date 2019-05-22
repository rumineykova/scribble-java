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
	public final List<AssrtAFormula> aforms;
	
	protected AssrtCoreRecVar(CommonTree source, RecVar var,
			List<AssrtAFormula> annotexprs)
	{
		super(source);
		this.recvar = var;
		this.aforms = Collections.unmodifiableList(annotexprs);
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
		return this.recvar.toString() + "<" + this.aforms.stream()
				.map(e -> e.toString()).collect(Collectors.joining(", ")) + ">";
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
				&& this.aforms.equals(them.aforms);
	}

	@Override
	public int hashCode()
	{
		int hash = 6733;
		hash = 31*hash + this.recvar.hashCode();
		hash = 31*hash + this.aforms.hashCode();
		return hash;
	}
}

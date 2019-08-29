package org.scribble.ext.assrt.core.type.session;

import java.util.function.Function;
import java.util.stream.Stream;

import org.scribble.core.type.kind.ProtoKind;

public abstract class AssrtCoreEnd<K extends ProtoKind, 
			B extends AssrtCoreSType<K, B>>
		extends AssrtCoreSTypeBase<K, B>
{
	public AssrtCoreEnd()
	{
		super(null);
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
		return "end";
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof AssrtCoreEnd))
		{
			return false;
		}
		return super.equals(o);  // Checks canEquals
	}

	@Override
	public int hashCode()
	{
		return 31*2447;
	}
}

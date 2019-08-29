package org.scribble.ext.assrt.core.type.session;

import java.util.function.Function;
import java.util.stream.Stream;

import org.scribble.core.type.kind.ProtoKind;
import org.scribble.core.type.session.SType;

public interface AssrtCoreSType<K extends ProtoKind, 
			B extends AssrtCoreSType<K, B>>
		extends SType<K, NoSeq<K>>
{
	<T> Stream<T> assrtCoreGather(Function<AssrtCoreSType<K, B>, Stream<T>> f);
}

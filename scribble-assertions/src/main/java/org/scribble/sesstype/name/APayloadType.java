package org.scribble.sesstype.name;

import org.scribble.sesstype.kind.PayloadTypeKind;


public interface APayloadType<K extends PayloadTypeKind> extends PayloadType<K>
{

	default boolean isAnnotPayloadDecl()
	{
		return false;
	}
	
	default boolean isAnnotPayloadInScope()
	{
		return false;
	}
}

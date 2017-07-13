package org.scribble.ext.assrt.sesstype.name;

import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.name.PayloadType;


public interface AssrtPayloadType<K extends PayloadTypeKind> extends PayloadType<K>
{

	default boolean isAnnotVarDecl()
	{
		return false;
	}
	
	default boolean isAnnotVarName()
	{
		return false;
	}
}

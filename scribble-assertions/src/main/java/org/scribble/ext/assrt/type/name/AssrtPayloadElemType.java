package org.scribble.ext.assrt.type.name;

import org.scribble.type.kind.PayloadTypeKind;
import org.scribble.type.name.PayloadElemType;


public interface AssrtPayloadElemType<K extends PayloadTypeKind> extends PayloadElemType<K>
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

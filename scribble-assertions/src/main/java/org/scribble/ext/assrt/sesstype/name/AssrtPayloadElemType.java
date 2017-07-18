package org.scribble.ext.assrt.sesstype.name;

import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.name.PayloadElemType;


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

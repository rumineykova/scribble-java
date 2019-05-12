package org.scribble.ext.assrt.core.type.name;

import org.scribble.core.type.kind.PayElemKind;
import org.scribble.core.type.name.PayElemType;


public interface AssrtPayloadElemType<K extends PayElemKind>
		extends PayElemType<K>
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

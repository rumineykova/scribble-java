package org.scribble.ext.assrt.core.ast;


// ast here means "core syntax" of session types -- it does not link the actual Scribble source (cf. base ast classes)
public interface AssrtCoreType
{
	boolean canEquals(Object o);
}

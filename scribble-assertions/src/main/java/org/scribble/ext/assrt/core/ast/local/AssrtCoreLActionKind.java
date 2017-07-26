package org.scribble.ext.assrt.core.ast.local;

import org.scribble.ext.assrt.core.ast.AssrtCoreActionKind;
import org.scribble.type.kind.Local;

public enum AssrtCoreLActionKind implements AssrtCoreActionKind<Local>
{
	SEND,
	RECEIVE,
	REQUEST,
	ACCEPT;
	
	@Override
	public String toString()
	{
		switch (this)
		{
			case SEND:    return "!";
			case RECEIVE: return "?";
			case REQUEST: return "!!";
			case ACCEPT:  return "??";
			default:      throw new RuntimeException("Won't get here: " + this);
		}
	}
}

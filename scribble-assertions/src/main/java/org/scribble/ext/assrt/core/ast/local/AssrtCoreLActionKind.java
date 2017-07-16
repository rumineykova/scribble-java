package org.scribble.ext.assrt.core.ast.local;

import org.scribble.ext.assrt.core.ast.AssrtCoreActionKind;
import org.scribble.sesstype.kind.Local;

public enum AssrtCoreLActionKind implements AssrtCoreActionKind<Local>
{
	SEND,
	RECEIVE;
	//REQUEST,
	//ACCEPT;
	
	@Override
	public String toString()
	{
		switch (this)
		{
			case SEND: return "!";
			case RECEIVE: return "?";
			default: throw new RuntimeException("Won't get here: " + this);
		}
	}
}

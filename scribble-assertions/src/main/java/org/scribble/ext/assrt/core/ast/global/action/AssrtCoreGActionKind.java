package org.scribble.ext.assrt.core.ast.global.action;

import org.scribble.ext.assrt.core.ast.AssrtCoreActionKind;
import org.scribble.sesstype.kind.Global;

public enum AssrtCoreGActionKind implements AssrtCoreActionKind<Global>
{
	MESSAGE,
	CONNECT;
	//DISCONNECT
	
	@Override
	public String toString()
	{
		switch (this)
		{
			case MESSAGE: return "->";
			case CONNECT: return "->>";
			default: throw new RuntimeException("Won't get here: " + this);
		}
	}
}

package org.scribble.ext.assrt.core.ast.global;

import org.scribble.ext.assrt.core.ast.AssrtCoreActionKind;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLActionKind;
import org.scribble.sesstype.kind.Global;
import org.scribble.sesstype.name.Role;

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
	
	public AssrtCoreLActionKind project(Role src, Role subj)
	{
		return src.equals(subj) ? AssrtCoreLActionKind.SEND : AssrtCoreLActionKind.RECEIVE;
	}
}

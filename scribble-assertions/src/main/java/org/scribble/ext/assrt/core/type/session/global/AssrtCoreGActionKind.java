package org.scribble.ext.assrt.core.type.session.global;

import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.type.session.AssrtCoreActionKind;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLActionKind;

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
	
	// src is global src, subj is either src or dest
	public AssrtCoreLActionKind project(Role src, Role subj)
	{
		return 
				this == AssrtCoreGActionKind.MESSAGE
				? (src.equals(subj) 
						? AssrtCoreLActionKind.SEND : AssrtCoreLActionKind.RECV)
				: (src.equals(subj) 
						? AssrtCoreLActionKind.REQ : AssrtCoreLActionKind.ACC)
				;
	}
}

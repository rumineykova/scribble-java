package org.scribble.net;

import java.io.Serializable;

import org.scribble.sesstype.name.Op;

public class ScribMessage implements Serializable
{
	private static final long serialVersionUID = 1L;

	// add msg source
	public final Op op;
	public final Object[] payload;

	public ScribMessage(Op op, Object... payload)
	{
		this.op = op;
		this.payload = payload;  // FIXME: make defensive array copy?
	}
	
	/*// Unicast
	public MulticastSignature toMulticastSignature(Role src, Role dest, Scope scope)
	{
		return toMulticastSignature(src, Arrays.asList(dest), scope);
	}
	
	public MulticastSignature toMulticastSignature(Role src, List<Role> dests, Scope scope)
	{
		return new MulticastSignature(src, dests, scope, op, getPayloadTypes());
	}

	protected final List<PayloadType> getPayloadTypes()
	{
		List<PayloadType> types = new LinkedList<>();
		for (Object o : this.payload)
		{
			// FIXME: routine should be plugin based on schema
			types.add(new PayloadType(o.getClass().toString()));
		}
		return types;
	}*/

	@Override
	public int hashCode()
	{
		int hash = 73;
		//hash = 31 * hash + super.hashCode();
		hash = 31 * hash + op.hashCode();
		hash = 31 * hash + this.payload.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof ScribMessage))
		{
			return false;
		}
		ScribMessage body = (ScribMessage) o;
		return this.op.equals(body.op) && this.payload.equals(body.payload);
	}
	
	@Override
	public String toString()
	{
		String s = this.op + "(";
		if (this.payload.length > 0)
		{
			s += this.payload[0];
			for (int i = 1; i < this.payload.length; i++)
			{
				s += ", " + this.payload[i];
			}
		}
		return s + ")";
	}
}

package org.scribble.ext.assrt.model.global;

import org.scribble.core.model.MState;
import org.scribble.core.model.global.SState;
import org.scribble.core.model.global.actions.SAction;

public class AssrtSState extends SState
{
	protected AssrtSState(AssrtSConfig config)
	{
		super(config);
	}
	
	@Override
	public AssrtSStateErrors getErrors()
	{
		return new AssrtSStateErrors(this);
	}

	@Override
	protected String getEdgeLabel(SAction msg)
	{
		return "label=\"" + msg.toString().replaceAll("\\\"", "\\\\\"") + "\"";
	}
	
	@Override
	public int hashCode()
	{
		int hash = 5503;
		hash = 31 * hash + super.hashCode();
		return hash;
	}

	// FIXME? doesn't use this.id, cf. super.equals
	// Not using id, cf. ModelState -- FIXME? use a factory pattern that associates unique states and ids? -- use id for hash, and make a separate "semantic equals"
	// Care is needed if hashing, since mutable (OK to use immutable config -- cf., ModelState.id)
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtSState))
		{
			return false;
		}
		return super.equals(o);  // Does canEquals
	}

	@Override
	protected boolean canEquals(MState<?, ?, ?, ?> s)
	{
		return s instanceof AssrtSState;
	}
}

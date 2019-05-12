package org.scribble.ext.assrt.model.global;

import java.util.Map;

import org.scribble.core.model.endpoint.EState;
import org.scribble.core.model.global.SGraph;
import org.scribble.core.model.global.SStateErrors;
import org.scribble.core.type.name.Role;

public class AssrtSStateErrors extends SStateErrors
{
	public Map<Role, EState> varsNotInScope;  // Assertion Variable that are not in scope
	public Map<Role, EState> unsatAssertions;  // Unsatisfiable assertion constraints

	public AssrtSStateErrors(AssrtSState s)
	{
		super(s);
		AssrtSConfig config = (AssrtSConfig) s.config;
		this.varsNotInScope =  config.getVarsNotInScope();
		this.unsatAssertions = config.getUnsatAssertions();
	}
	
	@Override
	public boolean isEmpty()
	{
		return super.isEmpty() && this.varsNotInScope.isEmpty()
				&& this.unsatAssertions.isEmpty();
	}
	
	@Override
	public String toString()
	{
		return super.toString() + ", varsNotInScope=" + this.varsNotInScope
				+ ", unsatAssertions=" + this.unsatAssertions;
	}

	@Override
	public String toErrorMessage(SGraph graph)
	{
		return super.toErrorMessage(graph) + appendSafetyErrorMessages();
	}

	protected String appendSafetyErrorMessages()
	{
		String msg = "";
		if (!this.varsNotInScope.isEmpty())
		{
			msg += "\n    Assertion variables are not in scope: "
					+ this.varsNotInScope;
		}
		if (!this.unsatAssertions.isEmpty())
		{
			msg += "\n    Unsatisfiable constraints: " + this.unsatAssertions;
		}
		return msg;
	}
}

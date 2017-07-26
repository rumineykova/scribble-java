package org.scribble.ext.assrt.model.global;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.model.global.SStateErrors;
import org.scribble.type.name.Role;

public class AssrtSStateErrors extends SStateErrors
{
	public Map<Role, EState> varsNotInScope;   // Assertion Variable that are not in scope
	public Map<Role, EState> unsatAssertions;   // Unsatisfiable assertion constraints

	public AssrtSStateErrors(Map<Role, EReceive> receptionErrors, Set<Set<Role>> deadlocks, 
			Map<Role, Set<ESend>> orphans, Map<Role, EState> unfinished, 
			Map<Role, EState> varsNotInScope, Map<Role, EState> unsatAssertions)
	{
		super(receptionErrors, deadlocks, orphans, unfinished);
		this.varsNotInScope =  Collections.unmodifiableMap(varsNotInScope);
		this.unsatAssertions = Collections.unmodifiableMap(unsatAssertions);
	}
	
	@Override
	public boolean isEmpty()
	{
		return super.isEmpty() && this.varsNotInScope.isEmpty() && this.unsatAssertions.isEmpty();
	}
	
	@Override
	public String toString()
	{
		return super.toString() + ", varsNotInScope=" + this.varsNotInScope + ", unsatAssertions=" + this.unsatAssertions;
	}
}

package org.scribble.ext.assrt.model.global;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.model.global.SStateErrors;
import org.scribble.sesstype.name.Role;

public class AssrtSStateErrors extends SStateErrors
{
	public Map<Role, EState> unsatAssertions;   // Unsatisfiable assertion constraints
	public Map<Role, EState> varsNotInScope;   // Assertion Variable that are not in scope

	public AssrtSStateErrors(Map<Role, EReceive> receptionErrors, Set<Set<Role>> deadlocks, 
			Map<Role, Set<ESend>> orphans, Map<Role, EState> unfinished, 
			Map<Role, EState> unsatAssertions, Map<Role, EState> varsNotInScope)
	{
		super(receptionErrors, deadlocks, orphans, unfinished);
		this.unsatAssertions = Collections.unmodifiableMap(unsatAssertions);
		this.varsNotInScope =  Collections.unmodifiableMap(varsNotInScope);
	}
	
	@Override
	public boolean isEmpty()
	{
		return super.isEmpty() && this.unsatAssertions.isEmpty() && this.varsNotInScope.isEmpty();
	}
	
	@Override
	public String toString()
	{
		return super.toString() + ", unsatAssertions=" + this.unsatAssertions + ", varsNotInScope=" + this.varsNotInScope;
	}
}

package org.scribble.model.global;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.sesstype.name.Role;

public class SStateErrors
{
	// FIXME: factor out explicit error classes -- for error message formatting
	// FIXME: could also check for roles stuck on unconnected sends here (probably better, than current syntax check)
	public final Map<Role, EReceive> stuck;      // Reception errors
	public final Set<Set<Role>> waitFor;         // Deadlock cycles
	public final Map<Role, Set<ESend>> orphans;  // Orphan messages
	public Map<Role, EState> unfinished;         // Unfinished roles
	public Map<Role, EState> unsatAssertions;   // Unsatisfiable assertion constraints

	public SStateErrors(Map<Role, EReceive> receptionErrors, Set<Set<Role>> deadlocks, 
			Map<Role, Set<ESend>> orphans, Map<Role, EState> unfinished, Map<Role, EState> unsatAssertions)
	{
		this.stuck = Collections.unmodifiableMap(receptionErrors);
		this.waitFor = Collections.unmodifiableSet(deadlocks);
		this.orphans = Collections.unmodifiableMap(orphans);
		this.unfinished = Collections.unmodifiableMap(unfinished);
		this.unsatAssertions = Collections.unmodifiableMap(unsatAssertions);
	}
	
	public boolean isEmpty()
	{
		return this.stuck.isEmpty() && this.waitFor.isEmpty() && 
				this.orphans.isEmpty() && this.unfinished.isEmpty() && this.unsatAssertions.isEmpty();
	}
}

package org.scribble.ext.assrt.core.model.global;

import java.util.Map;
import java.util.Set;

import org.scribble.core.model.endpoint.actions.ESend;
import org.scribble.core.type.name.Role;


// TODO: refactor cf. SState.getProgressErrors, Map<Set<SState>, Pair<Set<Role>, Map<Role, Set<ESend>>>>
public class AssrtCoreProgressErrors
{
	public final Map<Role, Set<Set<AssrtCoreSState>>> roleProgress;
	public final Map<ESend, Set<Set<AssrtCoreSState>>> eventualReception;
	
	public AssrtCoreProgressErrors(
			Map<Role, Set<Set<AssrtCoreSState>>> roleProgress,
			Map<ESend, Set<Set<AssrtCoreSState>>> eventualReception)
	{
		this.roleProgress = roleProgress;
		this.eventualReception = eventualReception;
	}
	
	public boolean satisfiesProgress()
	{
		return this.roleProgress.isEmpty() && this.eventualReception.isEmpty();
				// FIXME: refactor eventual reception as 1-bounded stable property
	}
	
	@Override
	public String toString()
	{
		String m = "";
		if (!this.roleProgress.isEmpty())
		{
			m += "\n[f17] Role progress violation(s):\n  "
			//	+ this.roleProgress.stream().map((ts) -> format(ts)).collect(Collectors.joining("\n  "));
					+ roleProgress.toString();
		}
		if (!this.eventualReception.isEmpty())
		{
			m += "\n[f17] Eventual reception violation(s):\n  "
				//+ this.eventualReception.stream().map((ts) -> format(ts)).collect(Collectors.joining("\n  "));
				+ "\n" + eventualReception.toString();
		}
		if (m.length() != 0)
		{
			m = m.substring(1, m.length());
		}
		return m;
	}
	
	/*private static final String format(Set<F17SState> ts)
	{
		return ts.stream().map((s) -> s.toString()).collect(Collectors.joining(", "));
	}*/
}

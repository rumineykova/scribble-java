package org.scribble.ext.assrt.core.model.global;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.name.Role;


// Duplicated from F17LTSBuilder
public class AssrtCoreSModelBuilder
{
	private final SModelFactory sf;
	
	public AssrtCoreSModelBuilder(SModelFactory sf)
	{
		this.sf = sf;
	}
	
	public AssrtCoreSModel build(Map<Role, EState> E0, boolean isExplicit)
	{
		AssrtCoreSState init = new AssrtCoreSState(E0, isExplicit); 
		
		Set<AssrtCoreSState> todo = new HashSet<>();
		Map<Integer, AssrtCoreSState> seen = new HashMap<>();
		todo.add(init);
		
		while (!todo.isEmpty())
		{
			Iterator<AssrtCoreSState> i = todo.iterator();
			AssrtCoreSState curr = i.next();
			i.remove();
			seen.put(curr.id, curr);

			Map<Role, List<EAction>> fireable = curr.getFireable();
			Set<Entry<Role, List<EAction>>> es = new HashSet<>(fireable.entrySet());
			while (!es.isEmpty())
			{
				Iterator<Entry<Role, List<EAction>>> j = es.iterator();
				Entry<Role, List<EAction>> e = j.next();
				j.remove();
				//boolean removed = es.remove(e);

				Role self = e.getKey();
				List<EAction> as = e.getValue();
				for (EAction a : as)
				{
					// cf. SState.getNextStates
					final AssrtCoreSState tmp;
					if (a.isSend() || a.isReceive() || a.isRequest() || a.isAccept())// || a.isDisconnect())
					{
						tmp = curr.fire(self, a);
					}
					/*else if (a.isConnect() || a.isAccept())
					{
						EAction dual = a.toDual(self);
						tmp = curr.sync(self, a, a.peer, dual);
						for (Entry<Role, List<EAction>> foo : es)
						{
							if (foo.getKey().equals(a.peer))
							{
								es.remove(foo);
								foo.getValue().remove(dual);  // remove side effect causes underlying hashing to become inconsistent, so need to manually remove/re-add
								es.add(foo);
								break;
							}
						}
						if (a.isAccept())
						{
							a = dual;  // HACK: draw connect/accept sync edges as connect (to stand for the sync of both) -- set of actions as edge label probably more consistent
						}
					}*/
					else
					{
						throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);
					}

					AssrtCoreSState next = tmp;  // Base case
					if (seen.values().contains(tmp))
					{
						next = seen.values().stream().filter(s -> s.equals(tmp)).iterator().next();
					}
					else if (todo.contains(tmp))
					{
						next = todo.stream().filter(s -> s.equals(tmp)).iterator().next();
					}
					curr.addEdge(a.toGlobal(this.sf, self), next);
					curr.addSubject(self);
					if (!seen.values().contains(next) && !todo.contains(next))
					{
						todo.add(next);
					}
				}
			}
		}
		
		return new AssrtCoreSModel(E0, init, seen);
	}
}

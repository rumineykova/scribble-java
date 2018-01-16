package org.scribble.ext.assrt.core.model.global;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.cli.AssrtCommandLine;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.name.Role;


// Duplicated from F17LTSBuilder
public class AssrtCoreSModelBuilder  // SModel is a wrapper for SGraph with model validation methods -- here, just build "model" directly (no "graph")
{
	private final SModelFactory sf;
	
	public AssrtCoreSModelBuilder(SModelFactory sf)
	{
		this.sf = sf;
	}
	
	public AssrtCoreSModel build(Map<Role, AssrtEState> E0, boolean isExplicit)
	{
		Map<Role, AssrtEState> assrtE0 = E0.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue()));
		AssrtCoreSState init = new AssrtCoreSState(assrtE0, isExplicit);  // FIXME: make AssrtCoreSModelFactory (also AssrtCoreSModel) -- cf. (Assrt)SModelFactory
		
		Set<AssrtCoreSState> todo = new HashSet<>();
		Map<Integer, AssrtCoreSState> seen = new HashMap<>();
		todo.add(init);
		
		while (!todo.isEmpty())
		//for (int zz = 0; !todo.isEmpty(); zz++)
		{
			//System.err.print(zz + " ");
				
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
		
		AssrtCommandLine.time(null, 93);
		AssrtCoreSModel res = new AssrtCoreSModel(E0, init, seen);
		AssrtCommandLine.time(null, 94);
		
		return res;
	}
}

package org.scribble.ext.assrt.core.model.global;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EGraph;
import org.scribble.core.model.endpoint.actions.EAction;
import org.scribble.core.model.global.SConfig;
import org.scribble.core.model.global.SGraphBuilder;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.Role;


// Duplicated from F17LTSBuilder
// SModel is a wrapper for SGraph with model validation methods -- here, just build "model" directly (no "graph")
public class AssrtCoreSGraphBuilder extends SGraphBuilder 
{
	public AssrtCoreSGraphBuilder(ModelFactory mf)
	{
		super(mf);
	}
	
	@Override
	public AssrtCoreSGraph build(Map<Role, EGraph> egraphs, boolean isExplicit, GProtoName fullname)
	{
		/*Map<Role, AssrtEState> assrtE0 = egraphs.entrySet().stream().collect(
				Collectors.toMap(Entry::getKey, e -> (AssrtEState) e.getValue().init));*/
		AssrtCoreSConfig c0 = ((AssrtCoreSGraphBuilderUtil) this.util)
				.createInitConfig(egraphs, isExplicit);
		AssrtCoreSState init = new AssrtCoreSState(c0);  // FIXME: make AssrtCoreSModelFactory (also AssrtCoreSModel) -- cf. (Assrt)SModelFactory
		
		Set<AssrtCoreSState> todo = new HashSet<>();
		todo.add(init);
		
		while (!todo.isEmpty())
		//for (int zz = 0; !todo.isEmpty(); zz++)
		{
			Iterator<AssrtCoreSState> i = todo.iterator();
			AssrtCoreSState curr = i.next();
			i.remove();
			Map<Role, Set<EAction>> fireable = curr.config.getFireable();
			Set<Entry<Role, Set<EAction>>> es = new HashSet<>(fireable.entrySet());
			while (!es.isEmpty())
			{
				Iterator<Entry<Role, Set<EAction>>> j = es.iterator();
				Entry<Role, Set<EAction>> e = j.next();
				j.remove();
				//boolean removed = es.remove(e);

				Role self = e.getKey();
				Set<EAction> as = e.getValue();
				for (EAction a : as)
				{
					// cf. SState.getNextStates
					final AssrtCoreSState succ;
					if (a.isSend() || a.isReceive() || a.isRequest() || a.isAccept())// || a.isDisconnect())
					{
						Set<SConfig> next = new HashSet<>(curr.config.async(self, a));  // Singleton
						succ = (AssrtCoreSState) this.util.addEdgesAndGetNewSuccs(curr,
								a.toGlobal(self), next).iterator().next();  // Constructs the edges
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
						throw new RuntimeException(
								"[assrt-core] Shouldn't get in here: " + a);
					}

					curr.addSubject(self);
					todo.add(succ);
				}
			}
		}
		
		return (AssrtCoreSGraph) this.mf.global.SGraph(fullname,
				this.util.getStates(), init);  // Cf. super
	}
}

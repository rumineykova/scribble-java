package org.scribble.ext.assrt.core.model.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.main.Job;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.sesstype.name.Role;

// 1-bounded LTS
// Factor out with SGraph/SModel?
public class AssrtCoreSModel
{
	public final Map<Role, EState> E0;
	public final AssrtCoreSState init;
	
	public Map<Integer, AssrtCoreSState> allStates; // State ID -> GMState

	private Map<Integer, Set<Integer>> reach; // State ID -> reachable states (not reflexive)
	private Set<Set<Integer>> termSets;

	protected AssrtCoreSModel(Map<Role, EState> E0, AssrtCoreSState init, Map<Integer, AssrtCoreSState> allStates)
	{
		this.E0 = Collections.unmodifiableMap(E0);
		this.init = init;
		this.allStates = Collections.unmodifiableMap(allStates);

		this.reach = getReachabilityMap();
		this.termSets = findTerminalSets();
	}
	
	public AssrtCoreSafetyErrors getSafetyErrors(Job job)
	{
		Set<AssrtCoreSState> conns = Collections.emptySet(); //this.allStates.values().stream().filter(AssrtCoreSState::isConnectionError).collect(Collectors.toSet());
		Set<AssrtCoreSState> disconns = Collections.emptySet(); //this.allStates.values().stream().filter(AssrtCoreSState::isDisconnectedError).collect(Collectors.toSet());
		Set<AssrtCoreSState> unconns = Collections.emptySet(); //this.allStates.values().stream().filter(AssrtCoreSState::isUnconnectedError).collect(Collectors.toSet());
		Set<AssrtCoreSState> syncs = Collections.emptySet(); //this.allStates.values().stream().filter(AssrtCoreSState::isSynchronisationError).collect(Collectors.toSet());
		Set<AssrtCoreSState> recepts = this.allStates.values().stream().filter(AssrtCoreSState::isReceptionError).collect(Collectors.toSet());
		Set<AssrtCoreSState> unfins = this.allStates.values().stream().filter(s -> s.isUnfinishedRoleError(this.E0)).collect(Collectors.toSet());
		Set<AssrtCoreSState> orphans = this.allStates.values().stream().filter(s -> s.isOrphanError(this.E0)).collect(Collectors.toSet());
		
		/*Set<AssrtCoreSState> portOpens = this.allStates.values().stream().filter(AssrtCoreSState::isPortOpenError).collect(Collectors.toSet());
		Set<AssrtCoreSState> portOwners = this.allStates.values().stream().filter(AssrtCoreSState::isPortOwnershipError).collect(Collectors.toSet());*/
		
		Set<AssrtCoreSState> unknownVars = this.allStates.values().stream().filter(s -> s.isUnknownDataTypeVarError()).collect(Collectors.toSet());
		Set<AssrtCoreSState> unsats = this.allStates.values().stream().filter(s -> s.isUnsatisfiableError(job)).collect(Collectors.toSet());

		return new AssrtCoreSafetyErrors(conns, disconns, unconns, syncs, recepts, unfins, orphans, unknownVars, unsats);
	}
	
	public boolean isActive(AssrtCoreSState s, Role r)
	{
		return AssrtCoreSState.isActive(s.getP().get(r), this.E0.get(r).id);
	}
	
	public AssrtCoreProgressErrors getProgressErrors()
	{
		Map<Role, Set<Set<AssrtCoreSState>>> roleProgress = new HashMap<>();
				/*this.E0.keySet().stream().collect(Collectors.toMap((r) -> r, (r) ->
					this.termSets.stream().map((ts) -> ts.stream().map((i) -> this.allStates.get(i)).collect(Collectors.toSet()))
						.filter((ts) -> ts.stream().allMatch((s) -> !s.getSubjects().contains(r)))
							.collect(Collectors.toSet())));*/
		for (Role r : this.E0.keySet())
		{
			for (Set<Integer> ts : this.termSets)	
			{
				if (ts.stream().allMatch((i) -> isActive(this.allStates.get(i), r)
						&& !this.allStates.get(i).getSubjects().contains(r)))
				{
					Set<Set<AssrtCoreSState>> set = roleProgress.get(r);
					if (set == null)
					{
						set = new HashSet<>();
						roleProgress.put(r, set);
					}
					set.add(ts.stream().map((i) -> this.allStates.get(i)).collect(Collectors.toSet()));
				}	
			}
		}

		Map<ESend, Set<Set<AssrtCoreSState>>> eventualReception = new HashMap<>();
		for (Role r1 : this.E0.keySet())
		{
			for (Role r2 : this.E0.keySet())
			{
				if (!r1.equals(r2))
				{
					for (Set<Integer> ts : this.termSets)	
					{
						AssrtCoreSState s1 = this.allStates.get(ts.iterator().next());
						ESend es = s1.getQ().get(r1).get(r2);

						if (es != null && !(es instanceof AssrtCoreEBot)  // FIXME: hasMessage?
								&& ts.stream().allMatch((i) -> es.equals(this.allStates.get(i).getQ().get(r1).get(r2))))
						{
							Set<Set<AssrtCoreSState>> set = eventualReception.get(es);
							if (set == null)
							{
								set = new HashSet<Set<AssrtCoreSState>>();
								eventualReception.put(es,  set);
							}
							set.add(ts.stream().map((i) -> this.allStates.get(i)).collect(Collectors.toSet()));
						}
					}
				}
			}
		}
		
		return new AssrtCoreProgressErrors(roleProgress, eventualReception);
	}
	
	// Revised "eventual reception" -- 1-bounded stable property with subject role side condition
	// FIXME: refactor as actual eventual reception -- though original one may be better for error feedback
	public Set<AssrtCoreSState> getStableErrors()
	{
		Set<AssrtCoreSState> res = new HashSet<>();
		for (AssrtCoreSState s : this.allStates.values())
		{
			if (!AssrtCoreSModel.isStable(s))
			{
				Set<AssrtCoreSState> seen = new HashSet<>();
				if (!canReachStable(seen, s))  // FIXME: subj role side condition for compatibility
				{
					res.add(s);
				}
			}
		}
		return res;
	}
	
	private static boolean isStable(AssrtCoreSState s)  // FIXME: refactor to F17SState
	{
		return s.getQ().values().stream().flatMap(m -> m.values().stream())
					.filter((v) -> v != null && !(v instanceof AssrtCoreEBot)).count() == 0;
	}
	
	//private boolean canReachStable(Set<F17SState> seen, F17SState s, Role r)
	private boolean canReachStable(Set<AssrtCoreSState> seen, AssrtCoreSState s)  // FIXME: subj role side condition for compatibility -- maybe integrate with this.reach -- currently no point to search this way, should just check stable on all reachable
	{
		if (AssrtCoreSModel.isStable(s))
		{
			return true;
		}
		else if (seen.contains(s))
		{
			return false;
		}
		/*for (F17SState succ : s.getAllSuccessors())
		{
			if (isStable(succ))
			{
				return true;
			}
		}*/
		for (AssrtCoreSState succ : s.getAllSuccessors())
		{
			Set<AssrtCoreSState> tmp = new HashSet<>(seen);
			tmp.add(s);
			if (canReachStable(tmp, succ))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return this.init.toString();
	}
	
	public String toDot()
	{
		return this.init.toDot();
	}
	
	@Override
	public final int hashCode()
	{
		int hash = 2887;
		hash = 31 * hash + this.init.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreSModel))
		{
			return false;
		}
		return this.init.id == ((AssrtCoreSModel) o).init.id;
	}

	
	/**
	 *  Duplicated from SGraph
	 */

	public Set<Set<Integer>> getTerminalSets()
	{
		return this.termSets;
	}

	public Set<Set<Integer>> findTerminalSets()
	{
		Set<Set<Integer>> termSets = new HashSet<>();
		Set<Set<Integer>> checked = new HashSet<>();
		for (Integer i : reach.keySet())
		{
			AssrtCoreSState s = this.allStates.get(i);
			Set<Integer> rs = this.reach.get(s.id);
			if (!checked.contains(rs) && rs.contains(s.id))
			{
				checked.add(rs);
				if (isTerminalSetMember(s))
				{
					termSets.add(rs);
				}
			}
		}
		//this.termSets = Collections.unmodifiableSet(termSets);
		return termSets;
	}

	private boolean isTerminalSetMember(AssrtCoreSState s)
	{
		Set<Integer> rs = this.reach.get(s.id);
		Set<Integer> tmp = new HashSet<>(rs);
		tmp.remove(s.id);
		for (Integer r : tmp)
		{
			if (!this.reach.containsKey(r) || !this.reach.get(r).equals(rs))
			{
				return false;
			}
		}
		return true;
	}

	/*// Pre: reach.get(start).contains(end) // FIXME: will return null if initial
	// state is error
	public List<SAction> getTrace(F17SState start, F17SState end)
	{
		SortedMap<Integer, Set<Integer>> candidates = new TreeMap<>();
		Set<Integer> dis0 = new HashSet<Integer>();
		dis0.add(start.id);
		candidates.put(0, dis0);

		Set<Integer> seen = new HashSet<>();
		seen.add(start.id);

		return getTraceAux(new LinkedList<>(), seen, candidates, end);
	}

	// Djikstra's
	private List<SAction> getTraceAux(List<SAction> trace, Set<Integer> seen,
			SortedMap<Integer, Set<Integer>> candidates, F17SState end)
	{
		Integer dis = candidates.keySet().iterator().next();
		Set<Integer> cs = candidates.get(dis);
		Iterator<Integer> it = cs.iterator();
		Integer currid = it.next();
		it.remove();
		if (cs.isEmpty())
		{
			candidates.remove(dis);
		}

		F17SState curr = this.states.get(currid);
		Iterator<SAction> as = curr.getAllActions().iterator();
		Iterator<F17SState> ss = curr.getAllSuccessors().iterator();
		while (as.hasNext())
		{
			SAction a = as.next();
			F17SState s = ss.next();
			if (s.id == end.id)
			{
				trace.add(a);
				return trace;
			}

			if (!seen.contains(s.id) && this.reach.containsKey(s.id)
					&& this.reach.get(s.id).contains(end.id))
			{
				seen.add(s.id);
				Set<Integer> tmp1 = candidates.get(dis + 1);
				if (tmp1 == null)
				{
					tmp1 = new HashSet<>();
					candidates.put(dis + 1, tmp1);
				}
				tmp1.add(s.id);
				List<SAction> tmp2 = new LinkedList<>(trace);
				tmp2.add(a);
				List<SAction> res = getTraceAux(tmp2, seen, candidates, end);
				if (res != null)
				{
					return res;
				}
			}
		}
		return null;
	}*/

	// Not reflexive
	public Map<Integer, Set<Integer>> getReachabilityMap()
	{
		if (this.reach != null)
		{
			return this.reach;
		}

		Map<Integer, Integer> idToIndex = new HashMap<>(); // state ID -> array
																												// index
		Map<Integer, Integer> indexToId = new HashMap<>(); // array index -> state
																												// ID
		int i = 0;
		for (AssrtCoreSState s : this.allStates.values())
		{
			idToIndex.put(s.id, i);
			indexToId.put(i, s.id);
			i++;
		}
		this.reach = getReachabilityAux(idToIndex, indexToId);

		return this.reach;
	}

	private Map<Integer, Set<Integer>> getReachabilityAux(
			Map<Integer, Integer> idToIndex, Map<Integer, Integer> indexToId)
	{
		int size = idToIndex.keySet().size();
		boolean[][] reach = new boolean[size][size];

		for (Integer s1id : idToIndex.keySet())
		{
			for (AssrtCoreSState s2 : this.allStates.get(s1id).getAllSuccessors())
			{
				reach[idToIndex.get(s1id)][idToIndex.get(s2.id)] = true;
			}
		}

		for (boolean again = true; again;)
		{
			again = false;
			for (int i = 0; i < size; i++)
			{
				for (int j = 0; j < size; j++)
				{
					if (reach[i][j])
					{
						for (int k = 0; k < size; k++)
						{
							if (reach[j][k] && !reach[i][k])
							{
								reach[i][k] = true;
								again = true;
							}
						}
					}
				}
			}
		}

		Map<Integer, Set<Integer>> res = new HashMap<>();
		for (int i = 0; i < size; i++)
		{
			Set<Integer> tmp = res.get(indexToId.get(i));
			for (int j = 0; j < size; j++)
			{
				if (reach[i][j])
				{
					if (tmp == null)
					{
						tmp = new HashSet<>();
						res.put(indexToId.get(i), tmp);
					}
					tmp.add(indexToId.get(j));
				}
			}
		}

		return Collections.unmodifiableMap(res);
	}
}

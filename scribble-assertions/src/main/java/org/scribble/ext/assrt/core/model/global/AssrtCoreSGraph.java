package org.scribble.ext.assrt.core.model.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.core.model.global.SGraph;
import org.scribble.core.model.global.SState;
import org.scribble.core.type.name.GProtoName;

// 1-bounded LTS
// Factor out with SGraph/SModel?
public class AssrtCoreSGraph extends SGraph
{
	// For convenience, shadowing supers -- CHECKME: OK or bad?
	private final AssrtCoreSState init;
	private final Map<Integer, AssrtCoreSState> states; // State ID -> GMState

	private Map<Integer, Set<Integer>> reach; // State ID -> reachable states (not reflexive)
	private Set<Set<Integer>> termSets;

	protected AssrtCoreSGraph(GProtoName fullname,
			Map<Integer, SState> states, SState init)
	{
		super(fullname, states, init);  // FIXME: AssrtCoreSState is not an SState
		this.init = (AssrtCoreSState) init;
		this.states = Collections
				.unmodifiableMap(states.entrySet().stream().collect(Collectors
						.toMap(Entry::getKey, x -> (AssrtCoreSState) x.getValue())));

		this.reach = getReachabilityMap();
		this.termSets = findTerminalSets();
	}
	
	@Override
	public String toDot()
	{
		return this.init.toDot();
	}
	
	@Override
	public String toString()
	{
		return this.init.toString();
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
		if (!(o instanceof AssrtCoreSGraph))
		{
			return false;
		}
		return this.init.id == ((AssrtCoreSGraph) o).init.id;
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
			AssrtCoreSState s = this.states.get(i);
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
		for (AssrtCoreSState s : this.states.values())
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
			for (AssrtCoreSState s2 : this.states.get(s1id).getSuccs())
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

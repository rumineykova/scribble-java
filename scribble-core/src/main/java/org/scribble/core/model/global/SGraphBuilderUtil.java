/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.core.model.global;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.core.model.GraphBuilderUtil;
import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EFsm;
import org.scribble.core.model.endpoint.EGraph;
import org.scribble.core.model.global.actions.SAction;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.Role;

public class SGraphBuilderUtil
		extends GraphBuilderUtil<Void, SAction, SState, Global>
{
	private Map<SConfig, SState> states = new HashMap<>();
	
	protected SGraphBuilderUtil(ModelFactory mf)
	{
		super(mf);
		reset();
	}
	
	@Override
	protected void reset()
	{
		this.states.clear();
	}

	// Here for ext overriding (e.g., Assrt)
	// Do as an initial state rather than config?
	protected SConfig createInitConfig(Map<Role, EGraph> egraphs,
			boolean explicit)
	{
		Map<Role, EFsm> efsms = egraphs.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().toFsm()));
		SingleBuffers b0 = new SingleBuffers(efsms.keySet(), !explicit);
		return this.mf.global.SConfig(efsms, b0);
	}
	
	public SState newState(SConfig c)
	{
		SState s = this.mf.global.SState(c);
		this.states.put(c, s);
		return s;
	}

	// Pre: this.states.containsKey(curr.config)
	public Set<SState> addEdgesAndGetNewSuccs(SState curr, SAction a, //List<SConfig> succs)
			Set<SConfig> succs)
			// SConfig.a/sync currently produces a List, but here collapse identical configs for global model (represent non-det "by edges", not "by model states")
	{
		Set<SState> res = new LinkedHashSet<>();  // Takes care of duplicates (o/w should also do "|| res.containsKey(c)" below) 
		for (SConfig c : succs)
		{
			boolean seen = this.states.containsKey(c);
			SState next = seen
					? this.states.get(c)
					: newState(c);
			curr.addEdge(a, next);  // Add edge to succ, regardless of new or not
			if (!seen)  // Must use "cached" test, newState changes adds the key
			{
				res.add(next);  // Only return new succs
			}
		}
		return res;
	}
	
	// s.id -> s
	public Map<Integer, SState> getStates()
	{
		return this.states.values().stream()
				.collect(Collectors.toMap(x -> x.id, x -> x));
	}
}

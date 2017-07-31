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
package org.scribble.ext.assrt.model.endpoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.scribble.model.MPrettyState;
import org.scribble.model.MState;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.type.name.RecVar;

public class AssrtEState extends EState
{
	protected AssrtEState(Set<RecVar> labs)
	{
		super(labs);
	}
	
	@Override
	protected AssrtEState clone()
	{
		Set<EState> all = new HashSet<>();
		all.add(this);
		all.addAll(MPrettyState.getReachableStates(this));
		Map<Integer, AssrtEState> map = new HashMap<>();
		for (EState s : all)
		{
			map.put(s.id, new AssrtEState(s.getLabels()));  // FIXME: factor out with super -- use EGraphBuilderUtil?
		}
		for (EState s : all)
		{
			Iterator<EAction> as = s.getAllActions().iterator();
			Iterator<EState> ss = s.getAllSuccessors().iterator();
			AssrtEState clone = map.get(s.id);
			while (as.hasNext())
			{
				EAction a = as.next();
				EState succ = ss.next();
				clone.addEdge(a, map.get(succ.id));
			}
		}
		return map.get(this.id);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 6619;
		hash = 31 * hash + super.hashCode();  // N.B. uses state ID only -- following super pattern
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtEState))
		{
			return false;
		}
		return super.equals(o);  // Checks canEquals
	}

	@Override
	protected boolean canEquals(MState<?, ?, ?, ?> s)
	{
		return s instanceof AssrtEState;
	}
}

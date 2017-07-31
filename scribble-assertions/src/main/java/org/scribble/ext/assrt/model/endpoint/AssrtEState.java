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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.model.MPrettyState;
import org.scribble.model.MState;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.type.name.RecVar;

public class AssrtEState extends EState
{
	private final Map<AssrtDataTypeVar, AssrtArithFormula> vars;

	// FIXME: make AssrtIntTypeVar?
	protected AssrtEState(Set<RecVar> labs, Map<AssrtDataTypeVar, AssrtArithFormula> vars)  // FIXME: currently syntactically restricted to one annot var
	{
		super(labs);
		this.vars = Collections.unmodifiableMap(vars);
	}
	
	public Map<AssrtDataTypeVar, AssrtArithFormula> getVars()
	{
		return this.vars;
	}
	
	@Override
	protected AssrtEState clone()
	{
		Set<AssrtEState> all = new HashSet<>();
		all.add(this);
		all.addAll(MPrettyState.getReachableStates(this).stream().map(x -> (AssrtEState) x).collect(Collectors.toSet()));
		Map<Integer, AssrtEState> map = new HashMap<>();
		for (AssrtEState s : all)
		{
			map.put(s.id, new AssrtEState(s.getLabels(), s.getVars()));  // FIXME: factor out with super -- use EGraphBuilderUtil?
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

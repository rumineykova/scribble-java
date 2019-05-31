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

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.scribble.core.model.endpoint.actions.ESend;
import org.scribble.core.type.name.Role;

public class SProgressErrors
{
	public final SModel model;
	
	public final Map<Set<SState>, Set<Role>> starved;  // Role progress errors
	public final Map<Set<SState>, Map<Role, Set<ESend>>> ignored;  // Eventual reception errors
	
	public SProgressErrors(SModel model)
	{
		this.model = model;
		this.starved = Collections.unmodifiableMap(model.getRoleProgErrors());
		Map<Set<SState>, Map<Role, Set<ESend>>> tmp = model.getEventualRecepErrors();
		this.ignored = Collections.unmodifiableMap(tmp.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey,
						x -> x.getValue().entrySet().stream()
								.collect(Collectors.toMap(Entry::getKey,
										y -> Collections.unmodifiableSet(y.getValue()))))));
	}
	
	public boolean isEmpty()
	{
		return this.starved.isEmpty() && this.ignored.isEmpty();
	}

	public String toErrorMessage()
	{
		String msg = "";
		if (!this.starved.isEmpty())  // starved
		{
			for (Set<SState> ts : this.starved.keySet())
			{
				msg += "\nRole progress violation for " + this.starved.get(ts)
						+ " in session state terminal set:\n    "
						+ SGraph.termSetToString(ts);
			}
		}
		if (!this.ignored.isEmpty())  // ignored
		{
			for (Set<SState> ts : this.ignored.keySet())
			{
				msg += "\nEventual reception violation for " + this.ignored.get(ts)
						+ " in session state terminal set:\n    "
						+ SGraph.termSetToString(ts);
			}
		}
		return msg;
	}
	
	@Override
	public String toString()
	{
		return Stream.<Map<?, ?>> of(this.ignored, this.starved)
				.filter(x -> !x.isEmpty()).map(x -> x.toString())
				.collect(Collectors.joining(", "));
	}
}

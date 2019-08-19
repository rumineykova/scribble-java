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
package org.scribble.ext.assrt.core.model.global;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.scribble.core.model.endpoint.EState;
import org.scribble.core.model.global.SGraph;
import org.scribble.core.model.global.SStateErrors;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.job.AssrtCore;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAction;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;

public class AssrtCoreSStateErrors extends SStateErrors
{
	// FIXME: factor out explicit error classes -- for error message formatting
	// FIXME: could also check for roles stuck on unconnected sends here (probably better, than current syntax check)

	public final Map<Role, Set<AssrtCoreEAction>> unknown;
	public final Map<Role, EState> assprog;  // TODO: rename -- state (safety) error, not "progress"
	public final Map<Role, Set<AssrtCoreEAction>> assunsat;
	public final Map<Role, AssrtEState> initrecass;
	public final Map<Role, Set<AssrtCoreEAction>> recass;  // CHECKME: equiv of assprog for rec asserts?

		
	// TODO batching
	// TODO refactor super to take core
	// CHECKME: core and fullname really necessary?
	public AssrtCoreSStateErrors(AssrtCore core, GProtoName fullname,
			AssrtCoreSState init, AssrtCoreSState curr)
	{
		super(curr);
		AssrtCoreSConfig cfg = (AssrtCoreSConfig) curr.config;
		core.verbosePrintln(
				"[assrt-core] Checking for safety errors in session state ("
						+ curr.id + "):");

		this.unknown = Collections
				.unmodifiableMap(cfg.getUnknownDataVarErrors(core, fullname));  // TODO: unmodifiable nested Sets
		this.assprog = Collections
				.unmodifiableMap(cfg.getAssertProgressErrors(core, fullname));  // Not actually a "progress" error
		this.assunsat = Collections
				.unmodifiableMap(cfg.getAssertUnsatErrors(core, fullname));

		if (this.state.id == init.id)
		{
			this.initrecass = cfg.getInitRecAssertErrors(core, fullname);
			this.recass = Collections.emptyMap();
		}
		else
		{
			this.initrecass = Collections.emptyMap();
			this.recass = Collections.
					unmodifiableMap(cfg.getRecAssertErrors(core, fullname));
		}
	}
	
	@Override
	public boolean isEmpty()
	{
		return super.isEmpty() && this.unknown.isEmpty() && this.assprog.isEmpty()
				&& this.assunsat.isEmpty() && this.recass.isEmpty();
	}

	@Override
	public String toErrorMessage(SGraph graph)
	{
		String msg = super.toErrorMessage(graph);  // Return empty when no error?
		if (!isEmpty())
		{
			msg += "\n" + this.state;
		}
		return msg;
	}

	@Override
	protected String appendErrors()
	{
		String res = super.appendErrors();
		if (!this.unknown.isEmpty())
		{
			res += "\n    Unknown data vars: " + this.unknown;
		}
		if (!this.assprog.isEmpty())
		{
			res += "\n    Assertion-progress errors: " + this.assprog;
		}
		if (!this.assunsat.isEmpty())
		{
			res += "\n    Unsatisfiable assertions: " + this.assunsat;
		}
		if (!this.recass.isEmpty())
		{
			res += "\n    Recursion-assertion errors: " + this.recass;
		}
		return res;
	}
	
	@Override
	public String toString()
	{
		String sup = super.toString();
		return (sup.isEmpty() ? "" : sup + ", ") + Stream
				.<Map<?, ?>> of(this.unknown, this.assprog, this.assunsat, this.recass)
				.filter(x -> !x.isEmpty()).map(x -> x.toString())
				.collect(Collectors.joining(", "));
	}
}

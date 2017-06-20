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
package org.scribble.model.global;

import java.util.Map;

import org.scribble.model.MState;
import org.scribble.model.endpoint.EState;
import org.scribble.model.global.SState;
import org.scribble.model.global.SStateErrors;
import org.scribble.sesstype.name.Role;

// FIXME: hashCode/equals
public class ASState extends SState
{
	// Unlike EState, SGraph is not just a "simple wrapper" for an existing graph of nodes -- it is a "semantic structure" that needs to be fully built properly (so no arbitrary "toGraph" method; cf., EState)
	protected ASState(ASConfig config)
	{
		super(config);
	}
	
	public ASStateErrors getErrors()
	{
		SStateErrors errs = super.getErrors();

		Map<Role, EState> unsatAssertion = ((ASConfig) this.config).getUnsatAssertions();   // FIXME:
		Map<Role, EState> varsNotInScope = ((ASConfig) this.config).checkHistorySensitivity();

		return new ASStateErrors(errs.stuck, errs.waitFor, errs.orphans, errs.unfinished, unsatAssertion, varsNotInScope);
	}
	
	// FIXME? doesn't use super.hashCode (cf., equals)
	@Override
	public int hashCode()
	{
		int hash = 79;
		//int hash = super.hashCode();
		hash = 31 * hash + this.config.hashCode();
		return hash;
	}

	// FIXME? doesn't use this.id, cf. super.equals
	// Not using id, cf. ModelState -- FIXME? use a factory pattern that associates unique states and ids? -- use id for hash, and make a separate "semantic equals"
	// Care is needed if hashing, since mutable (OK to use immutable config -- cf., ModelState.id)
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof ASState))
		{
			return false;
		}
		return ((ASState) o).canEquals(this) && this.config.equals(((ASState) o).config);
	}

	@Override
	protected boolean canEquals(MState<?, ?, ?, ?> s)
	{
		return s instanceof ASState;
	}
}

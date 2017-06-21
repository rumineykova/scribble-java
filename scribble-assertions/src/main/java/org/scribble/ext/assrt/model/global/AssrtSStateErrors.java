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
package org.scribble.ext.assrt.model.global;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.model.global.SStateErrors;
import org.scribble.sesstype.name.Role;

public class AssrtSStateErrors extends SStateErrors
{
	public Map<Role, EState> unsatAssertions;   // Unsatisfiable assertion constraints
	public Map<Role, EState> varsNotInScope;   // Assertion Variable that are not in scope

	public AssrtSStateErrors(Map<Role, EReceive> receptionErrors, Set<Set<Role>> deadlocks, 
			Map<Role, Set<ESend>> orphans, Map<Role, EState> unfinished, 
			Map<Role, EState> unsatAssertions, Map<Role, EState> varsNotInScope)
	{
		super(receptionErrors, deadlocks, orphans, unfinished);
		this.unsatAssertions = Collections.unmodifiableMap(unsatAssertions);
		this.varsNotInScope =  Collections.unmodifiableMap(varsNotInScope);
	}
	
	@Override
	public boolean isEmpty()
	{
		return super.isEmpty() && this.unsatAssertions.isEmpty() && this.varsNotInScope.isEmpty();
	}
}

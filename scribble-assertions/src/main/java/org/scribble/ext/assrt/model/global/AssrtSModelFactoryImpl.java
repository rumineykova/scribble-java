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

import java.util.Map;
import java.util.Set;

import org.scribble.ext.assrt.ast.formula.AssertionLogFormula;
import org.scribble.model.endpoint.EFSM;
import org.scribble.model.global.SBuffers;
import org.scribble.model.global.SConfig;
import org.scribble.model.global.SGraph;
import org.scribble.model.global.SModelFactoryImpl;
import org.scribble.model.global.SState;
import org.scribble.sesstype.name.GProtocolName;
import org.scribble.sesstype.name.Role;

public class AssrtSModelFactoryImpl extends SModelFactoryImpl implements AssrtSModelFactory
{

	@Override
	public SConfig newAssrtSConfig(Map<Role, EFSM> state, SBuffers buffs, AssertionLogFormula formula, Map<Role, Set<String>> variablesInScope)
	{
		return new AssrtSConfig(this, state, buffs, formula, variablesInScope);
	}

	@Override
	public SGraph newAssrtSGraph(GProtocolName proto, Map<Integer, SState> states, SState init)
	{
		return new AssrtSGraph(proto, states, init);
	}
}

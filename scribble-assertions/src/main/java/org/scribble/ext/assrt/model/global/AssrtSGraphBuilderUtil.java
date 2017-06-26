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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.model.endpoint.EFSM;
import org.scribble.model.endpoint.EGraph;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.global.SBuffers;
import org.scribble.model.global.SConfig;
import org.scribble.model.global.SGraphBuilderUtil;
import org.scribble.model.global.SModelFactory;
import org.scribble.sesstype.name.Role;

public class AssrtSGraphBuilderUtil extends SGraphBuilderUtil
{
	protected AssrtSGraphBuilderUtil(SModelFactory sf)
	{
		super(sf);
	}
	
	@Override
	protected SConfig createInitialSConfig(EModelFactory ef, Map<Role, EGraph> egraphs, boolean explicit)
	{
		Map<Role, EFSM> efsms = egraphs.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().toFsm()));
		SBuffers b0 = new SBuffers(ef, efsms.keySet(), !explicit);
		return ((AssrtSModelFactory) this.sf).newAssrtSConfig(efsms, b0, null, new HashMap<Role, Set<String>>());
	}
}

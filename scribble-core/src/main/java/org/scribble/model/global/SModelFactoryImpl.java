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

import org.scribble.model.endpoint.EFSM;
import org.scribble.model.global.actions.SConnect;
import org.scribble.model.global.actions.SReceive;
import org.scribble.model.global.actions.SSend;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.GProtocolName;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

// Separate E/SModelFactories fits protected E/SState constructor pattern
public class SModelFactoryImpl implements SModelFactory
{
	@Override
	public SConnect newSConnect(Role subj, Role obj, MessageId<?> mid, Payload payload)
	{
		return new SConnect(subj, obj, mid, payload);
	}

	@Override
	public SSend newSSend(Role subj, Role obj, MessageId<?> mid, Payload payload)
	{
		return new SSend(subj, obj, mid, payload);
	}

	@Override
	public SReceive newSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload)
	{
		return new SReceive(subj, obj, mid, payload);
	}

	@Override
	public SState newSState(SConfig config)
	{
		return new SState(config);
	}

	@Override
	public SGraph newSGraph(GProtocolName proto, Map<Integer, SState> states, SState init)
	{
		return new SGraph(proto, states, init);
	}

	@Override
	public SConfig newSConfig(Map<Role, EFSM> state, SBuffers buffs)
	{
		return new SConfig(state, buffs);
	}
}

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

import java.util.HashMap;
import java.util.Set;

import org.scribble.core.model.endpoint.actions.EAcc;
import org.scribble.core.model.endpoint.actions.ERecv;
import org.scribble.core.model.global.SSingleBuffers;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreERecv;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;

// Immutable -- send/receive/etc return updated copies
public class AssrtCoreSSingleBuffers extends SSingleBuffers
{
	public AssrtCoreSSingleBuffers(Set<Role> roles, boolean implicit)
	{
		super(roles, implicit);
	}
	
	// TODO refactor to modelfactory (cf. super)
	protected AssrtCoreSSingleBuffers()
	{
		
	}
	
	@Override
	protected AssrtCoreSSingleBuffers copy()  // TODO: refactor to modelfactory (cf. super)
	{
		AssrtCoreSSingleBuffers copy = new AssrtCoreSSingleBuffers();
		for (Role r : this.buffs.keySet())
		{
			copy.connected.put(r, new HashMap<>(this.connected.get(r)));
			copy.buffs.put(r, new HashMap<>(this.buffs.get(r)));
		}
		return copy;
	}

  // N.B. state args ignored for recv fireable and firing (msgs don't carry state args)
	public boolean canReceive(Role self, ERecv a)
	{
		AssrtCoreESend msg = (AssrtCoreESend) this.buffs.get(self).get(a.peer);
		return isConnected(self, a.peer)  // Other direction doesn't matter, local can still receive after peer disconnected
				&& msg != null && msg//.toTrueAssertion()
						.toDual(a.peer)
						.equals(((AssrtCoreERecv) a).dropStateArgs());  
						// Ignore state args for firing, msg doesn't carry state args, cf. A->B.A->C.X<123> w.r.t. A/B duality
	}

	// N.B. "sync" action but only considers the self side, i.e., to actually fire, must also explicitly check canRequest
	public boolean canAccept(Role self, EAcc a)
	{
		throw new RuntimeException(
				"[assrt-core] [TODO] canAccept for " + self + ": " + a);
	}

	@Override
	public int hashCode()
	{
		int hash = 31393;
		hash = 31 * hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreSSingleBuffers))
		{
			return false;
		}
		return super.equals(o);  // Checks canEquals
	}
	
	@Override
	protected boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreSSingleBuffers;
	}
}

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

import org.scribble.ext.assrt.ast.AssrtAssertionNode;
import org.scribble.ext.assrt.model.global.actions.AssrtSReceive;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

@Deprecated
public class AssrtEReceive extends AssrtEAction
{
	/*protected static final Set<Receive> RECEIVES = new HashSet<>();
	
	public static Receive get(Role peer, MessageId<?> mid, Payload payload)
	{
		Receive receive = new Receive(peer, mid, payload, true);
		for (Receive r : Receive.RECEIVES)  // FIXME: hashmap
		{
			if (r.equiv(receive))
			{
				return r;
			}
		}
		Receive.RECEIVES.add(receive);
		return receive;
	}

	private Receive(Role peer, MessageId<?> mid, Payload payload, boolean hack)
	{
		super(peer, mid, payload);
	}*/

	public AssrtEReceive(Role peer, MessageId<?> mid, Payload payload)
	{
		super(peer, mid, payload, null);
		//Receive.RECEIVES.add(this);
	}
	
	public AssrtEReceive(Role peer, MessageId<?> mid, Payload payload, AssrtAssertionNode assertion)
	{
		super(peer, mid, payload, assertion);
		//Receive.RECEIVES.add(this);
	}
	
	@Override
	public AssrtEAction toDual(Role self)
	{
		//return new AESend(self, this.mid, this.payload, this.assertion);
		////return Send.get(self, this.mid, this.payload);
		return null;
	}

	@Override
	//public GModelAction toGlobal(Role self)
	public AssrtSReceive toGlobal(Role self)
	{
		//return new GModelAction(this.peer, self, this.mid, this.payload);
		////return GModelAction.get(this.peer, self, this.mid, this.payload);
		return new AssrtSReceive(self, this.peer, this.mid, this.payload, this.assertion);

	}
	
	@Override
	public int hashCode()
	{
		int hash = 947;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
	
	@Override
	public boolean isReceive()
	{
		return true;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtEReceive))
		{
			return false;
		}
		return ((AssrtEReceive) o).canEqual(this) && super.equals(o);
	}

	public boolean canEqual(Object o)
	{
		return o instanceof AssrtEReceive;
	}

	@Override
	protected String getCommSymbol()
	{
		return "?";
	}
}

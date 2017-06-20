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
package org.scribble.model.endpoint;

import org.scribble.ast.AssertionNode;
import org.scribble.model.global.actions.ASReceive;
import org.scribble.model.global.actions.SReceive;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public class AEReceive extends AEAction
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

	public AEReceive(Role peer, MessageId<?> mid, Payload payload)
	{
		super(peer, mid, payload, null);
		//Receive.RECEIVES.add(this);
	}
	
	public AEReceive(Role peer, MessageId<?> mid, Payload payload, AssertionNode assertion)
	{
		super(peer, mid, payload, assertion);
		//Receive.RECEIVES.add(this);
	}
	
	@Override
	public AESend toDual(Role self)
	{
		return new AESend(self, this.mid, this.payload, this.assertion);
		//return Send.get(self, this.mid, this.payload);
	}

	@Override
	//public GModelAction toGlobal(Role self)
	public ASReceive toGlobal(Role self)
	{
		//return new GModelAction(this.peer, self, this.mid, this.payload);
		////return GModelAction.get(this.peer, self, this.mid, this.payload);
		return new ASReceive(self, this.peer, this.mid, this.payload, this.assertion);

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
		if (!(o instanceof AEReceive))
		{
			return false;
		}
		return ((AEReceive) o).canEqual(this) && super.equals(o);
	}

	public boolean canEqual(Object o)
	{
		return o instanceof AEReceive;
	}

	@Override
	protected String getCommSymbol()
	{
		return "?";
	}
}

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

import org.scribble.ast.AAssertionNode;
import org.scribble.model.global.actions.ASSend;
import org.scribble.model.global.actions.SSend;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public class AESend extends AEAction
{
	/*protected static final Set<Send> SENDS = new HashSet<>();
	
	public static Send get(Role peer, MessageId<?> mid, Payload payload)
	{
		Send send = new Send(peer, mid, payload, true);
		for (Send s : Send.SENDS)  // FIXME: hashmap
		{
			if (s.equiv(send))
			{
				return s;
			}
		}
		Send.SENDS.add(send);
		return send;
	}
	
	public Send(Role peer, MessageId<?> mid, Payload payload, boolean hack)
	{
		super(peer, mid, payload);
	}*/

	public AESend(Role peer, MessageId<?> mid, Payload payload, AAssertionNode assertion)
	{
		super(peer, mid, payload, assertion);
		//Send.SENDS.add(this);
	}

	@Override
	public AEReceive toDual(Role self)
	{
		return new AEReceive(self, this.mid, this.payload, this.assertion);
		//return Receive.get(self, this.mid, this.payload);
	}

	@Override
	//public GModelAction toGlobal(Role self)
	public ASSend toGlobal(Role self)
	{
		//return new GModelAction(self, this.peer, this.mid, this.payload);
		////return GModelAction.get(self, this.peer, this.mid, this.payload);
		return new ASSend(self, this.peer, this.mid, this.payload, this.assertion);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 953;
		hash = 31 * hash + super.hashCode();
		return hash;
	}
	
	@Override
	public boolean isSend()
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
		if (!(o instanceof AESend))
		{
			return false;
		}
		return ((AESend) o).canEqual(this) && super.equals(o);
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AESend;
	}

	@Override
	protected String getCommSymbol()
	{
		return "!";
	}
}

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
import org.scribble.model.AMAction;
import org.scribble.model.global.actions.SAction;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.kind.Local;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

// FIXME: extend EAction, not AMAaction (delete the latter)
@Deprecated
public abstract class AEAction extends AMAction<Local>
{
	public final Role peer;
	
	/*public final MessageId<?> mid;
	public final Payload payload;  // Empty for MessageSigNames*/
	
	protected AEAction(Role peer, MessageId<?> mid, Payload payload)
	{
		this(peer, mid, payload, null); 
	}
	
	protected AEAction(Role peer, MessageId<?> mid, Payload payload, AAssertionNode assertion)
	{
		/*this.mid = mid;
		this.payload = payload;*/
		super(peer, mid, payload, assertion);
		this.peer = peer;
	}
	
	public abstract AEAction toDual(Role self);

	//public abstract GModelAction toGlobal(Role self);
	public abstract SAction toGlobal(Role self);

	public boolean isSend()
	{
		return false;
	}
	
	public boolean isReceive()
	{
		return false;
	}

	public boolean isConnect()
	{
		return false;
	}

	public boolean isDisconnect()
	{
		return false;
	}

	public boolean isAccept()
	{
		return false;
	}

	public boolean isWrapClient()
	{
		return false;
	}

	public boolean isWrapServer()
	{
		return false;
	}
}

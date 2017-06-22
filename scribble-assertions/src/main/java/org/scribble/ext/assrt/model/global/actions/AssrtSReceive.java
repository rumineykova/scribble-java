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
package org.scribble.ext.assrt.model.global.actions;

import org.scribble.ext.assrt.ast.AssrtAssertionNode;
import org.scribble.model.global.actions.SReceive;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

@Deprecated
public class AssrtSReceive extends SReceive
{
	public AssrtSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtAssertionNode assertion)
	{
		super(subj, obj, mid, payload);
	}
	
	@Override
	public boolean isReceive()
	{
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 977;
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
		if (!(o instanceof AssrtSReceive))
		{
			return false;
		}
		return ((AssrtSReceive) o).canEqual(this) && super.equals(o);
	}

	public boolean canEqual(Object o)
	{
		return o instanceof AssrtSReceive;
	}

	@Override
	protected String getCommSymbol()
	{
		return "?";
	}
}
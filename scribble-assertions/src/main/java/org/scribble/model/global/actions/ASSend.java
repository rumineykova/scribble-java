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
package org.scribble.model.global.actions;

import org.scribble.ast.AAssertionNode;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public class ASSend extends SSend
{
	public final AAssertionNode assertion;  // Cf., e.g., AGMessageTransfer

	public ASSend(Role subj, Role obj, MessageId<?> mid, Payload payload, AAssertionNode assertion)
	{
		super(subj, obj, mid, payload);
		this.assertion = assertion;
	}

	@Override
	public int hashCode()
	{
		int hash = 5483;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.assertion.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof ASSend))
		{
			return false;
		}
		ASSend as = (ASSend) o;
		return as.canEqual(this) && super.equals(o) && as.assertion.equals(this.assertion);
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof ASSend;
	}
	
	@Override
	public String toString()
	{
		return "[" + this.assertion + "]\n" + super.toString();
	}
}

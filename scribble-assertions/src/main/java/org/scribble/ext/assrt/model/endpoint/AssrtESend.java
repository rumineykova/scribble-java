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

import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.model.global.actions.AssrtSSend;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.model.global.SModelFactory;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public class AssrtESend extends ESend
{
	public final AssrtAssertion assertion;  // Cf., e.g., ALSend  // FIXME: should not be the AST node

	public AssrtESend(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload, AssrtAssertion assertion)
	{
		super(ef, peer, mid, payload);
		this.assertion = assertion;
	}

	@Override
	public AssrtSSend toGlobal(SModelFactory sf, Role self)
	{
		return new AssrtSSend(self, this.peer, this.mid, this.payload, this.assertion);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 5501;
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
		if (!(o instanceof AssrtESend))
		{
			return false;
		}
		AssrtESend as = (AssrtESend) o;
		return as.canEqual(this) && super.equals(o)
				&& ((as.assertion == null && this.assertion == null)
						|| (as.assertion != null && this.assertion != null && as.assertion.equals(this.assertion)));
	}

	@Override
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtESend;
	}
	
	@Override
	public String toString()
	{
		return "[" + this.assertion + "]" + super.toString();
	}
}

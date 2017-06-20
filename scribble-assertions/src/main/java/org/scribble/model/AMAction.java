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
package org.scribble.model;

import org.scribble.ast.AAssertionNode;
import org.scribble.model.MAction;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.kind.ProtocolKind;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

// FIXME: equals/hashCode
public abstract class AMAction<K extends ProtocolKind> extends MAction<K>
{
	public final AAssertionNode assertion; 
	
	protected AMAction(Role obj, MessageId<?> mid, Payload payload)
	{
		this(obj, mid, payload, null); 
	}
	
	protected AMAction(Role obj, MessageId<?> mid, Payload payload, AAssertionNode assertion)
	{
		super(obj, mid, payload);
		this.assertion = assertion; 
	}
	
	@Override
	public String toString()
	{
		String assertionToString = this.assertion==null? "": this.assertion.toString();  
		return this.obj + getCommSymbol() + this.mid + this.payload + assertionToString;
	}

	public String toStringWithMessageIdHack()
	{
		String m = this.mid.isMessageSigName() ? "^" + this.mid : this.mid.toString();  // HACK
		return this.obj + getCommSymbol() + m + this.payload;
	}
	
	protected abstract String getCommSymbol();
	
	@Override
	public int hashCode()
	{
		int hash = 919;
		hash = 31 * hash + this.obj.hashCode();
		hash = 31 * hash + this.mid.hashCode();
		hash = 31 * hash + this.payload.hashCode();
		//hash = 31* hash + this.payload.hashCode(); 
		return hash;
	}

	@Override
	public boolean equals(Object o)  // FIXME: kind
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AMAction))
		{
			return false;
		}
		AMAction<?> a = (AMAction<?>) o;  // Refactor as "compatible"
		return a.canEqual(this) && 
				this.obj.equals(a.obj) && this.mid.equals(a.mid) && this.payload.equals(a.payload);
		//return this.id == ((ModelAction<?>) o).id;
	}
	
	public abstract boolean canEqual(Object o);
}

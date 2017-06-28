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
package org.scribble.ext.assrt.sesstype.name;

import org.scribble.ext.assrt.sesstype.kind.AssrtAnnotPayloadElemKind;
import org.scribble.sesstype.name.DataType;

// In name package like GDelegationType -- FIXME: maybe refactor (both) out of name, and (Assrt)PayloadType
public class AssrtAnnotDataType implements AssrtPayloadType<AssrtAnnotPayloadElemKind>
{
	public final AssrtDataTypeVarName varName;
	public final DataType dataType;  // FIXME: generalise?
	
	public AssrtAnnotDataType(AssrtDataTypeVarName varName, DataType dataType)
	{
		this.varName = varName; 
		this.dataType = dataType; 
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtAnnotDataType))
		{
			return false;
		}
		AssrtAnnotDataType n = (AssrtAnnotDataType) o;
		return n.canEqual(this) && 
			n.varName.equals(this.varName) && n.dataType.equals(this.dataType);
	}
	
	public boolean canEqual(Object o)
	{
		return o instanceof AssrtAnnotDataType;
	}

	@Override
	public String toString()
	{
		return this.varName.toString() + ' '  + this.dataType.getSimpleName().toString();   
	}
	
	@Override
	public int hashCode()
	{
		int hash = 2767;
		hash = hash*31 + this.dataType.hashCode(); 
		hash = hash*31 + this.varName.hashCode();
		return hash;
	}

	@Override
	public AssrtAnnotPayloadElemKind getKind() {
		return AssrtAnnotPayloadElemKind.KIND;
	}
	
	@Override
	public boolean isAnnotVarDecl()
	{
		return true;
	}
}

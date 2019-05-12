package org.scribble.ext.assrt.type.name;

import org.scribble.core.type.name.DataName;
import org.scribble.ext.assrt.type.kind.AssrtAnnotDataTypeKind;

// Cf. GDelegType, in name package -- CHECKME: maybe refactor (both) out of name, and (Assrt)PayloadType
public class AssrtAnnotDataType
		implements AssrtPayloadElemType<AssrtAnnotDataTypeKind>
{
	public final AssrtDataTypeVar var;
	public final DataName data;  // CHECKME: generalise?
	
	public AssrtAnnotDataType(AssrtDataTypeVar varName, DataName data)
	{
		this.var = varName; 
		this.data = data; 
	}

	@Override
	public AssrtAnnotDataTypeKind getKind()
	{
		return AssrtAnnotDataTypeKind.KIND;
	}
	
	@Override
	public boolean isAnnotVarDecl()
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
		if (!(o instanceof AssrtAnnotDataType))
		{
			return false;
		}
		AssrtAnnotDataType them = (AssrtAnnotDataType) o;
		return them.canEquals(this) && them.var.equals(this.var)
				&& them.data.equals(this.data);
	}
	
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtAnnotDataType;
	}

	@Override
	public String toString()
	{
		return this.var + ": "  + this.data.getSimpleName();   
	}
	
	@Override
	public int hashCode()
	{
		int hash = 2767;
		hash = hash*31 + this.data.hashCode(); 
		hash = hash*31 + this.var.hashCode();
		return hash;
	}
}

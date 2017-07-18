package org.scribble.ext.assrt.sesstype.name;

import org.scribble.ext.assrt.sesstype.kind.AssrtAnnotDataTypeKind;
import org.scribble.sesstype.name.DataType;

// In name package like GDelegationType -- FIXME: maybe refactor (both) out of name, and (Assrt)PayloadType
public class AssrtAnnotDataType implements AssrtPayloadElemType<AssrtAnnotDataTypeKind>
{
	public final AssrtDataTypeVar var;
	public final DataType data;  // FIXME: generalise?
	
	public AssrtAnnotDataType(AssrtDataTypeVar varName, DataType dataType)
	{
		this.var = varName; 
		this.data = dataType; 
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
		AssrtAnnotDataType n = (AssrtAnnotDataType) o;
		return n.canEqual(this) && n.var.equals(this.var) && n.data.equals(this.data);
	}
	
	public boolean canEqual(Object o)
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

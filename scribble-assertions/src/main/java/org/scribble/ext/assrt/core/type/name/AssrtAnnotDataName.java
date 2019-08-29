package org.scribble.ext.assrt.core.type.name;

import org.scribble.core.type.name.DataName;
import org.scribble.ext.assrt.core.type.kind.AssrtAnnotDataKind;

// Cf. GDelegType; similarly located in name package -- CHECKME: maybe refactor (both) out of name, and (Assrt)PayloadType
public class AssrtAnnotDataName
		implements AssrtPayElemType<AssrtAnnotDataKind>
{
	// TODO: currently hardcoded to int data
	public final AssrtIntVar var;
	public final DataName data;  // currently only int
			// CHECKME: generalise beyond data?
	
	public AssrtAnnotDataName(AssrtIntVar varName, DataName data)
	{
		this.var = varName; 
		this.data = data; 
	}

	@Override
	public AssrtAnnotDataKind getKind()
	{
		return AssrtAnnotDataKind.KIND;
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
		if (!(o instanceof AssrtAnnotDataName))
		{
			return false;
		}
		AssrtAnnotDataName them = (AssrtAnnotDataName) o;
		return them.canEquals(this) && them.var.equals(this.var)
				&& them.data.equals(this.data);
	}
	
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtAnnotDataName;
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

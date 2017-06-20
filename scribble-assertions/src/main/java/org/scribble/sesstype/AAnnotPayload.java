package org.scribble.sesstype;

import org.scribble.sesstype.kind.AAnnotPayloadElemKind;
import org.scribble.sesstype.name.AAnnotVarName;
import org.scribble.sesstype.name.DataType;
import org.scribble.sesstype.name.APayloadType;

// Cf., org.scribble.sesstype.Payload?
public class AAnnotPayload implements APayloadType<AAnnotPayloadElemKind>
{
	public final AAnnotVarName varName;
	public final DataType dataType;
	
	public AAnnotPayload(AAnnotVarName varName, DataType dataType)
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
		if (!(o instanceof AAnnotPayload))
		{
			return false;
		}
		AAnnotPayload n = (AAnnotPayload) o;
		return n.canEqual(this) && 
			n.varName.equals(this.varName) && n.dataType.equals(this.dataType);
	}
	
	public boolean canEqual(Object o)
	{
		return o instanceof AAnnotPayload;
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
	public AAnnotPayloadElemKind getKind() {
		return AAnnotPayloadElemKind.KIND;
	}
	
	@Override
	public boolean isAnnotPayloadDecl()
	{
		return true;
	}
}

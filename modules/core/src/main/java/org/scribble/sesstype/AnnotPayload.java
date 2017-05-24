package org.scribble.sesstype;

import org.scribble.sesstype.kind.AnnotPayloadElemKind;
import org.scribble.sesstype.name.DataType;
import org.scribble.sesstype.name.PayloadType;
import org.scribble.sesstype.name.AnnotVarName;

public class AnnotPayload implements PayloadType<AnnotPayloadElemKind>
{
	public final AnnotVarName varName;
	public final DataType dataType;
	
	public AnnotPayload(AnnotVarName varName, DataType dataType)
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
		if (!(o instanceof AnnotPayload))
		{
			return false;
		}
		AnnotPayload n = (AnnotPayload) o;
		return n.canEqual(this) && 
			n.varName.equals(this.varName) && n.dataType.equals(this.dataType);
	}
	
	public boolean canEqual(Object o)
	{
		return o instanceof AnnotPayload;
	}

	@Override
	public String toString()
	{
		return this.varName.toString() + ' '  + this.dataType.toString();   
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
	public AnnotPayloadElemKind getKind() {
		return AnnotPayloadElemKind.KIND;
	}
	
	@Override
	public boolean isAnnotPayloadDecl()
	{
		return true;
	}
}

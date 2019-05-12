package org.scribble.ext.assrt.type.name;

import org.scribble.core.type.name.AbstractName;
import org.scribble.ext.assrt.type.kind.AssrtSortKind;

public class AssrtSort extends AbstractName<AssrtSortKind>  // Cf. Op   // integrate with PayloadElemType?  cf., grammar (sorts are same identifiers as types)
{
	private static final long serialVersionUID = 1L;
	
	//public static final AssrtSort EMPTY_OPERATOR = new Op();

	protected AssrtSort()
	{
		super(AssrtSortKind.KIND);
	}

	public AssrtSort(String text)
	{
		super(AssrtSortKind.KIND, text);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtSort))
		{
			return false;
		}
		return super.equals(o);  // Checks canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtSort;
	}

	@Override
	public int hashCode()
	{
		int hash = 7649;
		hash = 31 * super.hashCode();
		return hash;
	}
}

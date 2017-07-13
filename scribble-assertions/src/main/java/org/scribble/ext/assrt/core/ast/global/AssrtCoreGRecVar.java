package org.scribble.ext.assrt.core.ast.global;

import org.scribble.ext.assrt.core.ast.AssrtCoreRecVar;
import org.scribble.sesstype.name.RecVar;

	
// FIXME: hashCode/equals
public class AssrtCoreGRecVar extends AssrtCoreRecVar implements AssrtCoreGType
{
	public AssrtCoreGRecVar(RecVar var)
	{
		super(var);
	}
}

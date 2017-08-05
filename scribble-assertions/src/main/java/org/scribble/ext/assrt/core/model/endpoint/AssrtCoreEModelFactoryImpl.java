package org.scribble.ext.assrt.core.model.endpoint;

import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEReceive;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactoryImpl;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public class AssrtCoreEModelFactoryImpl extends AssrtEModelFactoryImpl implements AssrtCoreEModelFactory
{

	@Override
	public AssrtCoreESend newAssrtCoreESend(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf,
			AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		return new AssrtCoreESend(this, peer, mid, payload, bf, annot, expr);
	}

	@Override
	public AssrtCoreEReceive newAssrtCoreEReceive(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf,
			AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		return new AssrtCoreEReceive(this, peer, mid, payload, bf, annot, expr);
	}

}

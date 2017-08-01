package org.scribble.ext.assrt.core.model.global;

import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSReceive;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.model.global.AssrtSModelFactoryImpl;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public class AssrtCoreSModelFactoryImpl extends AssrtSModelFactoryImpl implements AssrtCoreSModelFactory
{

	@Override
	public AssrtCoreSSend newAssrtCoreSSend(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf,
			AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		return new AssrtCoreSSend(subj, obj, mid, payload, bf, annot, expr);
	}

	@Override
	public AssrtCoreSReceive newAssrtCoreSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf,
			AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		return new AssrtCoreSReceive(subj, obj, mid, payload, bf, annot, expr);
	}
}

package org.scribble.ext.assrt.core.model.endpoint;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAccept;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEReceive;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreERequest;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.core.model.stp.AssrtStpEState;
import org.scribble.ext.assrt.core.model.stp.action.AssrtStpEReceive;
import org.scribble.ext.assrt.core.model.stp.action.AssrtStpESend;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactoryImpl;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.RecVar;
import org.scribble.type.name.Role;

public class AssrtCoreEModelFactoryImpl extends AssrtEModelFactoryImpl implements AssrtCoreEModelFactory
{

	@Override
	public AssrtCoreESend newAssrtCoreESend(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf,
			//AssrtDataTypeVar annot, AssrtArithFormula expr)
			List<AssrtArithFormula> stateexprs)
	{
		return new AssrtCoreESend(this, peer, mid, payload, bf, stateexprs);
	}

	@Override
	public AssrtCoreEReceive newAssrtCoreEReceive(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf,
			//AssrtDataTypeVar annot, AssrtArithFormula expr)
			List<AssrtArithFormula> stateexprs)
	{
		return new AssrtCoreEReceive(this, peer, mid, payload, bf, stateexprs);
	}

	@Override
	public AssrtCoreERequest newAssrtCoreERequest(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf,
			//AssrtDataTypeVar annot, AssrtArithFormula expr)
			List<AssrtArithFormula> stateexprs)
	{
		return new AssrtCoreERequest(this, peer, mid, payload, bf, stateexprs);
	}

	@Override
	public AssrtCoreEAccept newAssrtCoreEAccept(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf,
			//AssrtDataTypeVar annot, AssrtArithFormula expr)
			List<AssrtArithFormula> stateexprs)
	{
		return new AssrtCoreEAccept(this, peer, mid, payload, bf, stateexprs);
	}

	@Override
	public AssrtStpEState newAssertStpEState(Set<RecVar> labs)
	{
		return new AssrtStpEState(labs);
	}

	@Override
	public AssrtStpESend newAssrtStpESend(Role peer, MessageId<?> mid, Payload payload,
			Map<AssrtDataTypeVar, AssrtSmtFormula<?>> sigma, AssrtBoolFormula A)
	{
		return new AssrtStpESend(this, peer, mid, payload, sigma, A);
	}

	@Override
	public AssrtStpEReceive newAssrtStpEReceive(Role peer, MessageId<?> mid, Payload payload,
			Map<AssrtDataTypeVar, AssrtSmtFormula<?>> sigma, AssrtBoolFormula A)
	{
		return new AssrtStpEReceive(this, peer, mid, payload, sigma, A);
	}
}

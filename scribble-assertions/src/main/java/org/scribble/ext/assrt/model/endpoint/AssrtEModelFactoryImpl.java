package org.scribble.ext.assrt.model.endpoint;

import org.scribble.ext.assrt.sesstype.formula.AssrtBoolFormula;
import org.scribble.model.endpoint.EModelFactoryImpl;
import org.scribble.model.endpoint.actions.EAccept;
import org.scribble.model.endpoint.actions.ERequest;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public class AssrtEModelFactoryImpl extends EModelFactoryImpl implements AssrtEModelFactory
{

	@Override
	public ESend newESend(Role peer, MessageId<?> mid, Payload payload)
	{
		throw new RuntimeException("[scrib-assert] Shouldn't get in here: ");
	}

	@Override
	public EReceive newEReceive(Role peer, MessageId<?> mid, Payload payload)
	{
		throw new RuntimeException("[scrib-assert] Shouldn't get in here: ");
	}

	@Override
	public ERequest newERequest(Role peer, MessageId<?> mid, Payload payload)
	{
		throw new RuntimeException("[scrib-assert] Shouldn't get in here: ");
	}

	@Override
	public EAccept newEAccept(Role peer, MessageId<?> mid, Payload payload)
	{
		throw new RuntimeException("[scrib-assert] Shouldn't get in here: ");
	}

	@Override
	//public AssrtESend newAssrtESend(Role peer, MessageId<?> mid, Payload payload, AssrtAssertion assertion)
	public AssrtESend newAssrtESend(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtESend(this, peer, mid, payload, bf);
	}

	@Override
	public AssrtEReceive newAssrtEReceive(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtEReceive(this, peer, mid, payload, bf);
	}

	@Override
	public AssrtERequest newAssrtERequest(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtERequest(this, peer, mid, payload, bf);
	}

	@Override
	public AssrtEAccept newAssrtEAccept(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtEAccept(this, peer, mid, payload, bf);
	}
}

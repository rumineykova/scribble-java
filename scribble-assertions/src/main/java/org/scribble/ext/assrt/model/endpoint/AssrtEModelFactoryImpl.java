package org.scribble.ext.assrt.model.endpoint;

import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.model.endpoint.EModelFactoryImpl;
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
	public ESend newAssrtESend(Role peer, MessageId<?> mid, Payload payload, AssrtAssertion assertion)
	{
		return new AssrtESend(this, peer, mid, payload, assertion);
	}
}

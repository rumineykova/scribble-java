package org.scribble.ext.assrt.model.endpoint;

import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public interface AssrtEModelFactory extends EModelFactory
{
	ESend newAssrtESend(Role peer, MessageId<?> mid, Payload payload, AssrtAssertion assertion);
}

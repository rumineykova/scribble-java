package org.scribble.ext.assrt.model.endpoint;

import org.scribble.ext.assrt.sesstype.formula.AssrtBoolFormula;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;

public interface AssrtEModelFactory extends EModelFactory
{
	// FIXME: should take AssrtBoolFormula ("type"), not AssrtAssertion (syntax)
	//AssrtESend newAssrtESend(Role peer, MessageId<?> mid, Payload payload, AssrtAssertion assertion);
	AssrtESend newAssrtESend(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);
	AssrtEReceive newAssrtEReceive(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);  // FIXME: duality? (assertions currently ignored by toDual)
}

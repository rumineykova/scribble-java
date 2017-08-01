package org.scribble.ext.assrt.model.endpoint;

import java.util.Map;
import java.util.Set;

import org.scribble.ext.assrt.model.endpoint.action.AssrtEAccept;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEReceive;
import org.scribble.ext.assrt.model.endpoint.action.AssrtERequest;
import org.scribble.ext.assrt.model.endpoint.action.AssrtESend;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.RecVar;
import org.scribble.type.name.Role;

public interface AssrtEModelFactory extends EModelFactory
{
	// FIXME: should take AssrtBoolFormula ("type"), not AssrtAssertion (syntax)
	//AssrtESend newAssrtESend(Role peer, MessageId<?> mid, Payload payload, AssrtAssertion assertion);
	AssrtESend newAssrtESend(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);
	AssrtEReceive newAssrtEReceive(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);  // FIXME: duality? (assertions currently ignored by toDual)
	AssrtERequest newAssrtERequest(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);
	AssrtEAccept newAssrtEAccept(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);
	
	AssrtEState newAssrtEState(Set<RecVar> labs, Map<AssrtDataTypeVar, AssrtArithFormula> vars);
}

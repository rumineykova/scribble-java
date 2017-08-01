package org.scribble.ext.assrt.core.model.endpoint;

import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEReceive;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public interface AssrtCoreEModelFactory extends AssrtEModelFactory
{
	AssrtCoreESend newAssrtCoreESend(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);
	AssrtCoreEReceive newAssrtCoreEReceive(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);
	/*AssrtERequest newAssrtCoreERequest(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);
	AssrtEAccept newAssrtCoreEAccept(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);*/
	
	//AssrtEState newAssrtCoreEState(Set<RecVar> labs, Map<AssrtDataTypeVar, AssrtArithFormula> vars);
}

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
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.RecVar;
import org.scribble.type.name.Role;

public interface AssrtCoreEModelFactory extends AssrtEModelFactory
{
	/*AssrtCoreESend newAssrtCoreESend(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);
	AssrtCoreEReceive newAssrtCoreEReceive(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);
	AssrtCoreERequest newAssrtCoreERequest(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);
	AssrtCoreEAccept newAssrtCoreEAccept(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);*/
	
	//AssrtEState newAssrtCoreEState(Set<RecVar> labs, Map<AssrtDataTypeVar, AssrtArithFormula> vars);

	AssrtCoreESend newAssrtCoreESend(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
	AssrtCoreEReceive newAssrtCoreEReceive(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
	AssrtCoreERequest newAssrtCoreERequest(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
	AssrtCoreEAccept newAssrtCoreEAccept(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
	
	AssrtStpEState newAssertStpEState(Set<RecVar> labs);
	AssrtStpESend newAssrtStpESend(Role peer, MessageId<?> mid, Payload payload, 
			Map<AssrtDataTypeVar, AssrtSmtFormula<?>> sigma, AssrtBoolFormula A);
	AssrtStpEReceive newAssrtStpEReceive(Role peer, MessageId<?> mid, Payload payload,
			Map<AssrtDataTypeVar, AssrtSmtFormula<?>> sigma, AssrtBoolFormula A);
}

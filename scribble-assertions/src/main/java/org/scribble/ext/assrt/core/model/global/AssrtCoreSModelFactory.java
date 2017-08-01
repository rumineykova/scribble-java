package org.scribble.ext.assrt.core.model.global;

import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSReceive;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public interface AssrtCoreSModelFactory extends SModelFactory
{
	AssrtCoreSSend newAssrtCoreSSend(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);
	AssrtCoreSReceive newAssrtCoreSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);
	/*AssrtSRequest newAssrtSRequest(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);
	AssrtSAccept newAssrtSAccept(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);*/

	//SConfig newAssrtSConfig(Map<Role, EFSM> state, SBuffers buffs, AssrtLogFormula formula, Map<Role, Set<String>> variablesInScope);
}

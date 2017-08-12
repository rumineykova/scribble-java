package org.scribble.ext.assrt.core.model.global;

import java.util.List;

import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSAccept;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSReceive;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSRequest;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public interface AssrtCoreSModelFactory extends SModelFactory
{
	/*AssrtCoreSSend newAssrtCoreSSend(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);
	AssrtCoreSReceive newAssrtCoreSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);
	AssrtCoreSRequest newAssrtCoreSRequest(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);
	AssrtCoreSAccept newAssrtCoreSAccept(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, AssrtDataTypeVar annot, AssrtArithFormula expr);*/

	//SConfig newAssrtSConfig(Map<Role, EFSM> state, SBuffers buffs, AssrtLogFormula formula, Map<Role, Set<String>> variablesInScope);

	//AssrtCoreSState newAssrtCoreSState(Map<Role, EFSM> state, SBuffers buffs, AssrtLogFormula formula, Map<Role, Set<String>> variablesInScope);

	AssrtCoreSSend newAssrtCoreSSend(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
	AssrtCoreSReceive newAssrtCoreSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
	AssrtCoreSRequest newAssrtCoreSRequest(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
	AssrtCoreSAccept newAssrtCoreSAccept(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
}

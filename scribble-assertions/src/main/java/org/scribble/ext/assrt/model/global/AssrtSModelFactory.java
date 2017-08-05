package org.scribble.ext.assrt.model.global;

import java.util.Map;
import java.util.Set;

import org.scribble.ext.assrt.model.global.actions.AssrtSAccept;
import org.scribble.ext.assrt.model.global.actions.AssrtSReceive;
import org.scribble.ext.assrt.model.global.actions.AssrtSRequest;
import org.scribble.ext.assrt.model.global.actions.AssrtSSend;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtLogFormula;
import org.scribble.model.endpoint.EFSM;
import org.scribble.model.global.SBuffers;
import org.scribble.model.global.SConfig;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public interface AssrtSModelFactory extends SModelFactory
{
	AssrtSSend newAssrtSSend(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);
	AssrtSReceive newAssrtSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);
	AssrtSRequest newAssrtSRequest(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);
	AssrtSAccept newAssrtSAccept(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf);

	SConfig newAssrtSConfig(Map<Role, EFSM> state, SBuffers buffs, AssrtLogFormula formula, Map<Role, Set<String>> variablesInScope);
}

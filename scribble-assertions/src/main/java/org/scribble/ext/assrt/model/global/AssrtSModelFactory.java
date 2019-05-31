package org.scribble.ext.assrt.model.global;

import java.util.Map;
import java.util.Set;

import org.scribble.core.model.endpoint.EFsm;
import org.scribble.core.model.global.SModelFactory;
import org.scribble.core.model.global.SSingleBuffers;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtLogFormula;
import org.scribble.ext.assrt.model.global.actions.AssrtSAcc;
import org.scribble.ext.assrt.model.global.actions.AssrtSRecv;
import org.scribble.ext.assrt.model.global.actions.AssrtSReq;
import org.scribble.ext.assrt.model.global.actions.AssrtSSend;

@Deprecated
public interface AssrtSModelFactory extends SModelFactory
{

	AssrtSConfig newAssrtSConfig(Map<Role, EFsm> state, SSingleBuffers buffs,
			AssrtLogFormula formula, Map<Role, Set<String>> variablesInScope);

	AssrtSSend newAssrtSSend(Role subj, Role obj, MsgId<?> mid, Payload payload,
			AssrtBFormula bf);
	AssrtSRecv newAssrtSReceive(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf);
	AssrtSReq newAssrtSRequest(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf);
	AssrtSAcc newAssrtSAccept(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf);
}

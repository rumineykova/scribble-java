package org.scribble.ext.assrt.model.endpoint;

import java.util.LinkedHashMap;
import java.util.Set;

import org.scribble.core.model.endpoint.EModelFactory;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEAcc;
import org.scribble.ext.assrt.model.endpoint.action.AssrtERecv;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEReq;
import org.scribble.ext.assrt.model.endpoint.action.AssrtESend;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;

public interface AssrtEModelFactory extends EModelFactory
{
	
	AssrtEState newAssrtEState(Set<RecVar> labs,
			LinkedHashMap<AssrtDataTypeVar, AssrtArithFormula> vars,
			AssrtBoolFormula ass);

	AssrtESend newAssrtESend(Role peer, MsgId<?> mid, Payload payload,
			AssrtBoolFormula bf);
	AssrtERecv newAssrtEReceive(Role peer, MsgId<?> mid, Payload payload,
			AssrtBoolFormula bf);
			// CHECKME: duality? (assertions currently ignored by toDual)
	AssrtEReq newAssrtERequest(Role peer, MsgId<?> mid, Payload payload,
			AssrtBoolFormula bf);
	AssrtEAcc newAssrtEAccept(Role peer, MsgId<?> mid, Payload payload,
			AssrtBoolFormula bf);
}

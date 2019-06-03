package org.scribble.ext.assrt.model.endpoint;

import java.util.LinkedHashMap;
import java.util.Set;

import org.scribble.core.model.endpoint.EModelFactory;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtIntVar;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEAcc;
import org.scribble.ext.assrt.model.endpoint.action.AssrtERecv;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEReq;
import org.scribble.ext.assrt.model.endpoint.action.AssrtESend;

public interface AssrtEModelFactory extends EModelFactory
{
	
	AssrtEState newAssrtEState(Set<RecVar> labs,
			LinkedHashMap<AssrtIntVar, AssrtAFormula> vars,
			AssrtBFormula ass);

	AssrtESend newAssrtESend(Role peer, MsgId<?> mid, Payload payload,
			AssrtBFormula bf);
	AssrtERecv newAssrtEReceive(Role peer, MsgId<?> mid, Payload payload,
			AssrtBFormula bf);
			// CHECKME: duality? (assertions currently ignored by toDual)
	AssrtEReq newAssrtERequest(Role peer, MsgId<?> mid, Payload payload,
			AssrtBFormula bf);
	AssrtEAcc newAssrtEAccept(Role peer, MsgId<?> mid, Payload payload,
			AssrtBFormula bf);
}

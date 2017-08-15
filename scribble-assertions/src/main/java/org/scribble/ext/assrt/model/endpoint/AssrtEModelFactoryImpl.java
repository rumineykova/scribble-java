package org.scribble.ext.assrt.model.endpoint;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.scribble.ext.assrt.model.endpoint.action.AssrtEAccept;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEReceive;
import org.scribble.ext.assrt.model.endpoint.action.AssrtERequest;
import org.scribble.ext.assrt.model.endpoint.action.AssrtESend;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.model.endpoint.EModelFactoryImpl;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAccept;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.endpoint.actions.ERequest;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.RecVar;
import org.scribble.type.name.Role;

public class AssrtEModelFactoryImpl extends EModelFactoryImpl implements AssrtEModelFactory
{

	// "Disable" old types 
	// FIXME: also used from AutParser, need to make AssrtAutParser -- or just don't disable? or create with True? -- also for newEState

	@Override
	public ESend newESend(Role peer, MessageId<?> mid, Payload payload)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: ");
	}

	@Override
	public EReceive newEReceive(Role peer, MessageId<?> mid, Payload payload)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: ");
	}

	@Override
	public ERequest newERequest(Role peer, MessageId<?> mid, Payload payload)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: ");
	}

	@Override
	public EAccept newEAccept(Role peer, MessageId<?> mid, Payload payload)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: ");
	}
	
	
	// Override existing types
	
	@Override
	public EState newEState(Set<RecVar> labs)  // Used in a more places than above "disabled" actions -- e.g., LInteractionSeqDel, to be uniform need to make an AssrtLInteractionSeqDel
	{
		return newAssrtEState(labs, Collections.emptyMap(),
				AssrtTrueFormula.TRUE);
	}

	
	// "New" types

	@Override
	//public AssrtESend newAssrtESend(Role peer, MessageId<?> mid, Payload payload, AssrtAssertion assertion)
	public AssrtESend newAssrtESend(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtESend(this, peer, mid, payload, bf);
	}

	@Override
	public AssrtEReceive newAssrtEReceive(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtEReceive(this, peer, mid, payload, bf);
	}

	@Override
	public AssrtERequest newAssrtERequest(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtERequest(this, peer, mid, payload, bf);
	}

	@Override
	public AssrtEAccept newAssrtEAccept(Role peer, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtEAccept(this, peer, mid, payload, bf);
	}

	@Override
	public AssrtEState newAssrtEState(Set<RecVar> labs, Map<AssrtDataTypeVar, AssrtArithFormula> vars,  // FIXME: AssrtIntVar?
			AssrtBoolFormula ass) 
	{
		return new AssrtEState(labs, vars,
				ass);
	}
}

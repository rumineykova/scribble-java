package org.scribble.ext.assrt.model.endpoint;

import java.util.LinkedHashMap;
import java.util.Set;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EModelFactoryImpl;
import org.scribble.core.model.endpoint.EState;
import org.scribble.core.model.endpoint.actions.EAcc;
import org.scribble.core.model.endpoint.actions.ERecv;
import org.scribble.core.model.endpoint.actions.EReq;
import org.scribble.core.model.endpoint.actions.ESend;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataVar;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEAcc;
import org.scribble.ext.assrt.model.endpoint.action.AssrtERecv;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEReq;
import org.scribble.ext.assrt.model.endpoint.action.AssrtESend;

public class AssrtEModelFactoryImpl extends EModelFactoryImpl
		implements AssrtEModelFactory
{

	public AssrtEModelFactoryImpl(ModelFactory mf)
	{
		super(mf);
	}


	// "Disable" old types 
	// FIXME: also used from AutParser, need to make AssrtAutParser -- or just don't disable? or create with True? -- also for newEState

	@Override
	public ESend ESend(Role peer, MsgId<?> mid, Payload payload)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: ");
	}

	@Override
	public ERecv ERecv(Role peer, MsgId<?> mid, Payload payload)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: ");
	}

	@Override
	public EReq EReq(Role peer, MsgId<?> mid, Payload payload)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: ");
	}

	@Override
	public EAcc EAcc(Role peer, MsgId<?> mid, Payload payload)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: ");
	}
	
	
	// Override existing types
	
	@Override
	public EState EState(Set<RecVar> labs)  // Used in a more places than above "disabled" actions -- e.g., LInteractionSeqDel, to be uniform need to make an AssrtLInteractionSeqDel
	{
		return newAssrtEState(labs, new LinkedHashMap<>(),
				AssrtTrueFormula.TRUE);
	}

	
	// "New" types

	@Override
	public AssrtEState newAssrtEState(Set<RecVar> labs,
			LinkedHashMap<AssrtDataVar, AssrtAFormula> vars,  // CHECKME: AssrtIntVar?
			AssrtBFormula ass)
	{
		return new AssrtEState(labs, vars, ass);
	}

	@Override
	//public AssrtESend newAssrtESend(Role peer, MsgId<?> mid, Payload payload, AssrtAssertion assertion)
	public AssrtESend newAssrtESend(Role peer, MsgId<?> mid, Payload payload, AssrtBFormula bf)
	{
		return new AssrtESend(this.mf, peer, mid, payload, bf);
	}

	@Override
	public AssrtERecv newAssrtEReceive(Role peer, MsgId<?> mid, Payload payload, AssrtBFormula bf)
	{
		return new AssrtERecv(this.mf, peer, mid, payload, bf);
	}

	@Override
	public AssrtEReq newAssrtERequest(Role peer, MsgId<?> mid, Payload payload, AssrtBFormula bf)
	{
		return new AssrtEReq(this.mf, peer, mid, payload, bf);
	}

	@Override
	public AssrtEAcc newAssrtEAccept(Role peer, MsgId<?> mid, Payload payload, AssrtBFormula bf)
	{
		return new AssrtEAcc(this.mf, peer, mid, payload, bf);
	}
}

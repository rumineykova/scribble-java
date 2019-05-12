package org.scribble.ext.assrt.core.model.endpoint;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAcc;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreERecv;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEReq;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.core.model.stp.AssrtStpEState;
import org.scribble.ext.assrt.core.model.stp.action.AssrtStpEReceive;
import org.scribble.ext.assrt.core.model.stp.action.AssrtStpESend;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactoryImpl;

public class AssrtCoreEModelFactoryImpl extends AssrtEModelFactoryImpl
		implements AssrtCoreEModelFactory
{

	public AssrtCoreEModelFactoryImpl(ModelFactory mf)
	{
		super(mf);
	}

	@Override
	public AssrtCoreESend newAssrtCoreESend(Role peer, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs)
	{
		return new AssrtCoreESend(this.mf, peer, mid, payload, bf, stateexprs);
	}

	@Override
	public AssrtCoreERecv newAssrtCoreEReceive(Role peer, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs)
	{
		return new AssrtCoreERecv(this.mf, peer, mid, payload, bf, stateexprs);
	}

	@Override
	public AssrtCoreEReq newAssrtCoreERequest(Role peer, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs)
	{
		return new AssrtCoreEReq(this.mf, peer, mid, payload, bf, stateexprs);
	}

	@Override
	public AssrtCoreEAcc newAssrtCoreEAccept(Role peer, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs)
	{
		return new AssrtCoreEAcc(this.mf, peer, mid, payload, bf, stateexprs);
	}


	@Override
	public AssrtStpEState newAssertStpEState(Set<RecVar> labs)
	{
		return new AssrtStpEState(labs);
	}

	@Override
	public AssrtStpESend newAssrtStpESend(Role peer, MsgId<?> mid, Payload payload,
			Map<AssrtIntVarFormula, AssrtSmtFormula<?>> sigma, AssrtBoolFormula A)
	{
		return new AssrtStpESend(this.mf, peer, mid, payload, sigma, A);
	}

	@Override
	public AssrtStpEReceive newAssrtStpEReceive(Role peer, MsgId<?> mid, Payload payload,
			Map<AssrtIntVarFormula, AssrtSmtFormula<?>> sigma, AssrtBoolFormula A)
	{
		return new AssrtStpEReceive(this.mf, peer, mid, payload, sigma, A);
	}
}

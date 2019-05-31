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
import org.scribble.ext.assrt.core.model.global.AssrtCoreEMsg;
import org.scribble.ext.assrt.core.model.stp.AssrtStpEState;
import org.scribble.ext.assrt.core.model.stp.action.AssrtStpEReceive;
import org.scribble.ext.assrt.core.model.stp.action.AssrtStpESend;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
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
	public AssrtCoreESend AssrtCoreESend(Role peer, MsgId<?> mid,
			Payload pay, AssrtBFormula ass, List<AssrtAFormula> sexprs)
	{
		return new AssrtCoreESend(this.mf, peer, mid, pay, ass, sexprs);
	}

	@Override
	public AssrtCoreERecv AssrtCoreERecv(Role peer, MsgId<?> mid,
			Payload pay, AssrtBFormula ass, List<AssrtAFormula> sexprs)
	{
		return new AssrtCoreERecv(this.mf, peer, mid, pay, ass, sexprs);
	}

	@Override
	public AssrtCoreEReq AssrtCoreEReq(Role peer, MsgId<?> mid,
			Payload pay, AssrtBFormula ass, List<AssrtAFormula> sexprs)
	{
		return new AssrtCoreEReq(this.mf, peer, mid, pay, ass, sexprs);
	}

	@Override
	public AssrtCoreEAcc AssrtCoreEAcc(Role peer, MsgId<?> mid,
			Payload pay, AssrtBFormula ass, List<AssrtAFormula> sexprs)
	{
		return new AssrtCoreEAcc(this.mf, peer, mid, pay, ass, sexprs);
	}
	
	@Override
	public AssrtCoreEMsg AssrtCoreEMsg(Role peer, MsgId<?> mid, Payload pay,
			AssrtBFormula ass, List<AssrtAFormula> sexprs,
			Map<AssrtIntVarFormula, AssrtIntVarFormula> shadow)
	{
		return new AssrtCoreEMsg(this.mf, peer, mid, pay, ass, sexprs, shadow);
	}

	@Override
	public AssrtStpEState AssertStpEState(Set<RecVar> labs)
	{
		return new AssrtStpEState(labs);
	}

	@Override
	public AssrtStpESend AssrtStpESend(Role peer, MsgId<?> mid, Payload pay,
			Map<AssrtIntVarFormula, AssrtSmtFormula<?>> sigma, AssrtBFormula A)
	{
		return new AssrtStpESend(this.mf, peer, mid, pay, sigma, A);
	}

	@Override
	public AssrtStpEReceive AssrtStpERecv(Role peer, MsgId<?> mid, Payload pay,
			Map<AssrtIntVarFormula, AssrtSmtFormula<?>> sigma, AssrtBFormula A)
	{
		return new AssrtStpEReceive(this.mf, peer, mid, pay, sigma, A);
	}
}

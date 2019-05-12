package org.scribble.ext.assrt.model.global;

import java.util.Map;
import java.util.Set;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EFsm;
import org.scribble.core.model.global.SConfig;
import org.scribble.core.model.global.SGraph;
import org.scribble.core.model.global.SModel;
import org.scribble.core.model.global.SModelFactoryImpl;
import org.scribble.core.model.global.SState;
import org.scribble.core.model.global.SingleBuffers;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.model.global.actions.AssrtSAcc;
import org.scribble.ext.assrt.model.global.actions.AssrtSRecv;
import org.scribble.ext.assrt.model.global.actions.AssrtSReq;
import org.scribble.ext.assrt.model.global.actions.AssrtSSend;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtLogFormula;

public class AssrtSModelFactoryImpl extends SModelFactoryImpl
		implements AssrtSModelFactory
{

	public AssrtSModelFactoryImpl(ModelFactory mf)
	{
		super(mf);
	}

	@Override
	public AssrtSGraphBuilderUtil SGraphBuilderUtil()
	{
		return new AssrtSGraphBuilderUtil(this.mf);
	}
	
	@Override
	public SState SState(SConfig config)
	{
		return new AssrtSState((AssrtSConfig) config);
	}

	@Override
	public SConfig SConfig(Map<Role, EFsm> state, SingleBuffers buffs)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: ");
	}
	
	@Override
	public SModel SModel(SGraph g)
	{
		return new AssrtSModel(g);
	}
	
	@Override
	public AssrtSConfig newAssrtSConfig(Map<Role, EFsm> state, SingleBuffers buffs,
			AssrtLogFormula formula, Map<Role, Set<String>> variablesInScope)
	{
		return new AssrtSConfig(this.mf, state, buffs, formula, variablesInScope);
	}
	
	@Override
	public AssrtSSend newAssrtSSend(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtSSend(subj, obj, mid, payload, bf);
	}
	
	@Override
	public AssrtSRecv newAssrtSReceive(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtSRecv(subj, obj, mid, payload, bf);
	}
	
	@Override
	public AssrtSReq newAssrtSRequest(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtSReq(subj, obj, mid, payload, bf);
	}
	
	@Override
	public AssrtSAcc newAssrtSAccept(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtSAcc(subj, obj, mid, payload, bf);
	}
}

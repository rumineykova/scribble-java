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
import org.scribble.model.global.SGraph;
import org.scribble.model.global.SModel;
import org.scribble.model.global.SModelFactoryImpl;
import org.scribble.model.global.SState;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;

public class AssrtSModelFactoryImpl extends SModelFactoryImpl implements AssrtSModelFactory
{

	@Override
	public SConfig newSConfig(Map<Role, EFSM> state, SBuffers buffs)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: ");
	}


	@Override
	public AssrtSGraphBuilderUtil newSGraphBuilderUtil()
	{
		return new AssrtSGraphBuilderUtil(this);
	}
	
	@Override
	public SState newSState(SConfig config)
	{
		return new AssrtSState((AssrtSConfig) config);
	}
	
	@Override
	public SModel newSModel(SGraph g)
	{
		return new AssrtSModel(g);
	}
	

	@Override
	public SConfig newAssrtSConfig(Map<Role, EFSM> state, SBuffers buffs, AssrtLogFormula formula, Map<Role, Set<String>> variablesInScope)
	{
		return new AssrtSConfig(this, state, buffs, formula, variablesInScope);
	}
	
	@Override
	public AssrtSSend newAssrtSSend(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtSSend(subj, obj, mid, payload, bf);
	}
	
	@Override
	public AssrtSReceive newAssrtSReceive(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtSReceive(subj, obj, mid, payload, bf);
	}
	
	@Override
	public AssrtSRequest newAssrtSRequest(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtSRequest(subj, obj, mid, payload, bf);
	}
	
	@Override
	public AssrtSAccept newAssrtSAccept(Role subj, Role obj, MessageId<?> mid, Payload payload, AssrtBoolFormula bf)
	{
		return new AssrtSAccept(subj, obj, mid, payload, bf);
	}
}

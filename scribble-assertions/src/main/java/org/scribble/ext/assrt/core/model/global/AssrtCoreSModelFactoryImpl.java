package org.scribble.ext.assrt.core.model.global;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EFsm;
import org.scribble.core.model.global.SConfig;
import org.scribble.core.model.global.SGraph;
import org.scribble.core.model.global.SModelFactoryImpl;
import org.scribble.core.model.global.SState;
import org.scribble.core.model.global.SingleBuffers;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSAcc;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSRecv;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSReq;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataVar;

public class AssrtCoreSModelFactoryImpl extends SModelFactoryImpl //AssrtSModelFactoryImpl
		implements AssrtCoreSModelFactory
{

	public AssrtCoreSModelFactoryImpl(ModelFactory mf)
	{
		super(mf);
	}

	@Override
	public AssrtCoreSGraphBuilder SGraphBuilder()
	{
		return new AssrtCoreSGraphBuilder(this.mf);
	}

	@Override
	public AssrtCoreSGraphBuilderUtil SGraphBuilderUtil()
	{
		return new AssrtCoreSGraphBuilderUtil(this.mf);
	}

	@Override
	public AssrtCoreSState SState(SConfig config)
	{
		return new AssrtCoreSState((AssrtCoreSConfig) config);
	}

	@Override
	public AssrtCoreSConfig AssrtCoreSConfig(Map<Role, EFsm> P, SingleBuffers Q,
			Map<Role, Map<AssrtDataVar, AssrtAFormula>> R,
			Map<Role, Set<AssrtBFormula>> Rass, Map<Role, Set<AssrtDataVar>> K,
			Map<Role, Set<AssrtBFormula>> F,
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename)
	{
		return new AssrtCoreSConfig(mf, P, Q, K, F, R, Rass, rename);
	}

	@Override
	public AssrtCoreSGraph SGraph(GProtoName fullname, Map<Integer, SState> states, 
			SState init)
	{
		return new AssrtCoreSGraph(fullname, states, init);
	}

	@Override
	public AssrtCoreSModel SModel(SGraph graph)
	{
		return new AssrtCoreSModel((AssrtCoreSGraph) graph);
	}

	@Override
	public AssrtCoreSSend newAssrtCoreSSend(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf, List<AssrtAFormula> stateexprs)
	{
		return new AssrtCoreSSend(subj, obj, mid, payload, bf, stateexprs);
	}

	@Override
	public AssrtCoreSRecv newAssrtCoreSReceive(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf, List<AssrtAFormula> stateexprs)
	{
		return new AssrtCoreSRecv(subj, obj, mid, payload, bf, stateexprs);
	}

	@Override
	public AssrtCoreSReq newAssrtCoreSRequest(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf, List<AssrtAFormula> stateexprs)
	{
		return new AssrtCoreSReq(subj, obj, mid, payload, bf, stateexprs);
	}

	@Override
	public AssrtCoreSAcc newAssrtCoreSAccept(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf, List<AssrtAFormula> stateexprs)
	{
		return new AssrtCoreSAcc(subj, obj, mid, payload, bf, stateexprs);
	}
}

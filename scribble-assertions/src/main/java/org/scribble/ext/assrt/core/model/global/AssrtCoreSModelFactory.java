package org.scribble.ext.assrt.core.model.global;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scribble.core.model.endpoint.EFsm;
import org.scribble.core.model.global.SModelFactory;
import org.scribble.core.model.global.SSingleBuffers;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.job.AssrtCore;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSAcc;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSRecv;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSReq;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataVar;

public interface AssrtCoreSModelFactory extends SModelFactory
{
	/*@Override
	AssrtCoreSGraphBuilder SGraphBuilder();

	@Override
	AssrtCoreSGraphBuilderUtil SGraphBuilderUtil();

	@Override
	AssrtCoreSState SState(SConfig config);

	@Override
	AssrtCoreSGraph SGraph(GProtoName fullname, Map<Integer, SState> states, 
			SState init);  // states: s.id -> s

	@Override
	AssrtCoreSModel SModel(SGraph graph);*/


	AssrtCoreSConfig AssrtCoreSConfig(Map<Role, EFsm> P, SSingleBuffers Q,
			Map<Role, Map<AssrtDataVar, AssrtAFormula>> R,
			Map<Role, Set<AssrtBFormula>> Rass, Map<Role, Set<AssrtDataVar>> K,
			Map<Role, Set<AssrtBFormula>> F,
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename);
	AssrtCoreSModel AssrtCoreSModel(AssrtCore core, AssrtCoreSGraph graph);
	
	AssrtCoreSSend newAssrtCoreSSend(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf, List<AssrtAFormula> stateexprs);
	AssrtCoreSRecv newAssrtCoreSReceive(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf, List<AssrtAFormula> stateexprs);
	AssrtCoreSReq newAssrtCoreSRequest(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf, List<AssrtAFormula> stateexprs);
	AssrtCoreSAcc newAssrtCoreSAccept(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBFormula bf, List<AssrtAFormula> stateexprs);
}

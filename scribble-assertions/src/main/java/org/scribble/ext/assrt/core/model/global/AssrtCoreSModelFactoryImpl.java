package org.scribble.ext.assrt.core.model.global;

import java.util.List;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSAcc;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSRecv;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSReq;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.model.global.AssrtSModelFactoryImpl;

public class AssrtCoreSModelFactoryImpl extends AssrtSModelFactoryImpl
		implements AssrtCoreSModelFactory
{

	public AssrtCoreSModelFactoryImpl(ModelFactory mf)
	{
		super(mf);
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

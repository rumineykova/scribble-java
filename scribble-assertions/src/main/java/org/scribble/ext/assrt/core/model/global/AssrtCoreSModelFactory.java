package org.scribble.ext.assrt.core.model.global;

import java.util.List;

import org.scribble.core.model.global.SModelFactory;
import org.scribble.core.type.name.MsgId;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSAcc;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSRecv;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSReq;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;

public interface AssrtCoreSModelFactory extends SModelFactory
{
	AssrtCoreSSend newAssrtCoreSSend(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
	AssrtCoreSRecv newAssrtCoreSReceive(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
	AssrtCoreSReq newAssrtCoreSRequest(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
	AssrtCoreSAcc newAssrtCoreSAccept(Role subj, Role obj, MsgId<?> mid,
			Payload payload, AssrtBoolFormula bf, List<AssrtArithFormula> stateexprs);
}

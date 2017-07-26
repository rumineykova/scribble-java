package org.scribble.ext.assrt.del.local;

import java.util.List;

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.ScribNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.local.LSendDel;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.main.ScribbleException;
import org.scribble.type.Payload;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Role;
import org.scribble.visit.context.EGraphBuilder;

public class AssrtLSendDel extends LSendDel
{
	@Override
	public ScribNode leaveEGraphBuilding(ScribNode parent, ScribNode child, EGraphBuilder builder, ScribNode visited) throws ScribbleException
	{
		AssrtLSend ls = (AssrtLSend) visited;
		List<RoleNode> dests = ls.getDestinations();
		if (dests.size() > 1)
		{
			throw new ScribbleException("[TODO] EFSM building for multicast not supported: " + ls);
		}
		Role peer = dests.get(0).toName();
		MessageId<?> mid = ls.msg.toMessage().getId();
		Payload payload = ls.msg.isMessageSigNode()  // Hacky?
				? ((MessageSigNode) ls.msg).payloads.toPayload()
				: Payload.EMPTY_PAYLOAD;
		
		//AssrtBoolFormula bf = AssrtFormulaFactoryImpl.AssrtTrueFormula();  
				// FIXME: because AssrtLReceiveDel makes True (because no AssrtLReceive with assertion) and toDual/getFireable needs direct correspondence
				// No: because AssrtSConfig needs the original assertion to check, e.g., history sens
		AssrtBoolFormula bf = (ls.ass == null) ? AssrtTrueFormula.TRUE : ls.ass.getFormula();
				// FIXME: now hacked in AssertSConfig.fire -- message assertion changed to true when queued
					
		builder.util.addEdge(builder.util.getEntry(),
				((AssrtEModelFactory) builder.job.ef).newAssrtESend(peer, mid, payload, bf),  // FIXME: factor out action building with LSendDel?
				builder.util.getExit());
		//return (AssrtLSend) super.leaveEGraphBuilding(parent, child, graph, ls);  // No
		// CHECKME: OK to ignore super?
		return ls;  // From super of LSendDel
	}
}

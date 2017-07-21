package org.scribble.ext.assrt.del.local;

import org.scribble.del.local.LConnectDel;

public class AssrtLConnectDel extends LConnectDel
{
	/*// Following AssrtLSendDel
	@Override
	public LConnect leaveEGraphBuilding(ScribNode parent, ScribNode child, EGraphBuilder builder, ScribNode visited) throws ScribbleException
	{
		LConnect lc = (LConnect) visited;
		RoleNode dest = lc.dest;
		Role peer = dest.toName();
		MessageId<?> mid = lc.msg.toMessage().getId();
		Payload payload = lc.msg.isMessageSigNode()  // Hacky?
					? ((MessageSigNode) lc.msg).payloads.toPayload()
					: Payload.EMPTY_PAYLOAD;
					
		AssrtBoolFormula bf = (ls.ass == null) ? AssrtTrueFormula.TRUE : ls.ass.getFormula();
					
		builder.util.addEdge(builder.util.getEntry(), 
				((AssrtEModelFactory) builder.job.ef).newAssrtEConnect(peer, mid, payload, bf),  // FIXME: factor out action building with LSendDel?
				builder.util.getExit());
		return lc;
	}*/
}

package org.scribble.ext.assrt.del.local;

import java.util.List;

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LReceive;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.local.LReceiveDel;
import org.scribble.ext.assrt.ast.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.parser.assertions.ast.formula.AssrtFormulaFactoryImpl;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.context.EGraphBuilder;

public class AssrtLReceiveDel extends LReceiveDel
{
	@Override
	public ScribNode leaveEGraphBuilding(ScribNode parent, ScribNode child, EGraphBuilder builder, ScribNode visited) throws ScribbleException
	{
		//AssrtLReceive lr = (AssrtLReceive) visited;
		LReceive lr = (LReceive) visited;
		List<RoleNode> dests = lr.getDestinations();
		if (dests.size() > 1)
		{
			throw new ScribbleException("[TODO] EFSM building for multicast not supported: " + lr);
		}
		Role peer = lr.src.toName();
		MessageId<?> mid = lr.msg.toMessage().getId();
		Payload payload = lr.msg.isMessageSigNode()  // Hacky?
					? ((MessageSigNode) lr.msg).payloads.toPayload()
					: Payload.EMPTY_PAYLOAD;
					
		//AssrtBoolFormula bf = (ls.ass == null) ? AssrtFormulaFactoryImpl.AssrtTrueFormula() : ls.ass.getFormula();
		AssrtBoolFormula bf = AssrtFormulaFactoryImpl.AssrtTrueFormula();  // FIXME: AssrtLReceive?
					
		builder.util.addEdge(builder.util.getEntry(),
				((AssrtEModelFactory) builder.job.ef).newAssrtEReceive(peer, mid, payload, bf),
				builder.util.getExit());  // FIXME: factor out action building with super?
		//return (AssrtLReceive) super.leaveEGraphBuilding(parent, child, graph, ls);  // No
		// CHECKME: OK to ignore super?
		return visited;
	}
}

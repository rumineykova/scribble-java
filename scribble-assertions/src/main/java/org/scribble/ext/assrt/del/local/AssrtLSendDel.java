/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.ext.assrt.del.local;

import java.util.List;

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LMessageTransfer;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.local.LSendDel;
import org.scribble.ext.assrt.ast.local.AssrtLSend;
import org.scribble.ext.assrt.model.endpoint.AssrtESend;
import org.scribble.main.ScribbleException;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.context.EGraphBuilder;
import org.scribble.visit.context.ProjectedChoiceSubjectFixer;
import org.scribble.visit.wf.ExplicitCorrelationChecker;

public class AssrtLSendDel extends LSendDel
{
	@Override
	public ScribNode leaveEGraphBuilding(ScribNode parent, ScribNode child, EGraphBuilder graph, ScribNode visited) throws ScribbleException
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
		graph.util.addEdge(graph.util.getEntry(), new AssrtESend(peer, mid, payload, ls.assertion), graph.util.getExit());
		return (AssrtLSend) super.leaveEGraphBuilding(parent, child, graph, ls);
	}
}

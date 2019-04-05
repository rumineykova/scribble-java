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
package org.scribble.del.global;

import java.util.List;

import org.scribble.ast.MessageNode;
import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GConnect;
import org.scribble.ast.local.LScribNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Message;
import org.scribble.del.ConnectionActionDel;
import org.scribble.util.ScribException;
import org.scribble.visit.GTypeTranslator;
import org.scribble.visit.context.Projector;
import org.scribble.visit.wf.NameDisambiguator;
import org.scribble.visit.wf.WFChoiceChecker;
import org.scribble.visit.wf.env.WFChoiceEnv;

public class GConnectDel extends ConnectionActionDel
		implements GSimpleInteractionNodeDel
{
	public GConnectDel()
	{
		
	}

	@Override
	public ScribNode leaveDisambiguation(ScribNode parent, ScribNode child,
			NameDisambiguator disamb, ScribNode visited) throws ScribException
	{
		GConnect gc = (GConnect) visited;
		/*Role src = gc.src.toName();
		Role dest = gc.dest.toName();*/
		return gc;
	}
	
	@Override
	public org.scribble.core.type.session.global.GConnect translate(ScribNode n,
			GTypeTranslator t) throws ScribException
	{
		GConnect source = (GConnect) n;
		Role src = source.getSourceChild().toName();
		List<RoleNode> ds = source.getDestinationChildren();
		if (ds.size() > 1)
		{
			throw new RuntimeException("TODO: multiple destination roles: " + source);
		}
		Role dst = ds.get(0).toName();
		Message msg = source.getMessageNodeChild().toMessage();
		return new org.scribble.core.type.session.global.GConnect(source, src, msg, dst);
	}

	@Override
	public GConnect leaveInlinedWFChoiceCheck(ScribNode parent, ScribNode child,
			WFChoiceChecker checker, ScribNode visited) throws ScribException
	{
		GConnect gc = (GConnect) visited;
		RoleNode srcNode = gc.getSourceChild();
		MessageNode msgNode = gc.getMessageNodeChild();
		RoleNode destNode = gc.getDestinationChild();

		Role src = srcNode.toName();
		if (!checker.peekEnv().isEnabled(src))
		{
			throw new ScribException(srcNode.getSource(), "Role not enabled: " + src);
		}
		Message msg = msgNode.toMessage();
		WFChoiceEnv env = checker.popEnv();
		//for (Role dest : gc.getDestinationRoles())
		Role dest = destNode.toName();
		{
			if (src.equals(dest))
			{
				throw new ScribException(gc.getSource(),
						"[TODO] Self connections not supported: " + gc);
			}
			if (env.isConnected(src, dest))
			{
				throw new ScribException(gc.getSource(),
						"Roles (possibly) already connected: " + src + ", " + dest);
			}

			env = env.connect(src, dest).addMessage(src, dest, msg);
			/*env = env
					.connect(src, dest)
					.addMessage(src, dest, new MessageSig(Op.EMPTY_OPERATOR, Payload.EMPTY_PAYLOAD));*/
		}
		checker.pushEnv(env);
		return gc;
	}

	@Override
	public ScribNode leaveProjection(ScribNode parent, ScribNode child,
			Projector proj, ScribNode visited) throws ScribException
	{
		GConnect gc = (GConnect) visited;
		Role self = proj.peekSelf();
		LScribNode projection = gc.project(proj.lang.config.af, self);
		proj.pushEnv(proj.popEnv().setProjection(projection));
		return (GConnect) GSimpleInteractionNodeDel.super.leaveProjection(parent,
				child, proj, gc);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*// Duplicated from GMessageTransferDel
	@Override
	public ScribNode leaveF17Parsing(ScribNode parent, ScribNode child, F17Parser parser, ScribNode visited) throws ScribbleException
	{
		F17ParserEnv env = parser.peekEnv();
		if (env.isUnguarded())
		{
			parser.popEnv();
			parser.pushEnv(new F17ParserEnv());  // Maybe make "setGuarded" method
		}
		return super.leaveF17Parsing(parent, child, parser, visited);
	}*/
}

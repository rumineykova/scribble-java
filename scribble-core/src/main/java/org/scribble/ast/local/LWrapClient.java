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
package org.scribble.ast.local;

import java.util.Collections;
import java.util.Set;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.Constants;
import org.scribble.ast.MessageSigNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.job.RuntimeScribbleException;
import org.scribble.job.ScribbleException;
import org.scribble.type.Message;
import org.scribble.type.name.Role;
import org.scribble.visit.context.ProjectedChoiceSubjectFixer;

public class LWrapClient extends LConnectionAction
		implements LSimpleSessionNode
{
	// ScribTreeAdaptor#create constructor
	public LWrapClient(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public LWrapClient(LWrapClient node)
	{
		super(node);
	}
	
	@Override
	public LWrapClient dupNode()
	{
		return new LWrapClient(this);
	}

	// Could make a LMessageTransfer to factor this out with LReceive
	@Override
	public Role inferLocalChoiceSubject(ProjectedChoiceSubjectFixer fixer)
	{
		return getSourceChild().toName();
		//throw new RuntimeException("TODO: " + this);
	}

	@Override
	public LSessionNode merge(AstFactory af, LSessionNode ln)
			throws ScribbleException
	{
		throw new RuntimeScribbleException("Invalid merge on LWrapClient: " + this);
	}

	@Override
	public boolean canMerge(LSessionNode ln)
	{
		return false;
	}

	@Override
	public Set<Message> getEnabling()
	{
		return Collections.emptySet();
	}

	@Override
	public String toString()
	{
		return Constants.WRAP_KW + " " + Constants.TO_KW + " "
				+ getDestinationChild().toString() + ";";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public LWrapClient(CommonTree source, MessageSigNode unit, RoleNode src, RoleNode dest)
	{
		//super(source, src, GWrap.UNIT_MESSAGE_SIG_NODE, dest);
		super(source, src, unit, dest);
	}

	/*@Override
	protected ScribNodeBase copy()
	{
		return new LWrapClient(this.source, (MessageSigNode) this.msg, this.src, this.dest);
	}
	
	@Override
	public LWrapClient clone(AstFactory af)
	{
		RoleNode src = this.src.clone(af);
		RoleNode dest = this.dest.clone(af);
		return af.LWrapClient(this.source, src, dest);
	}

	@Override
	public LWrapClient reconstruct(RoleNode src, MessageNode msg, RoleNode dest)
	//public LWrapClient reconstruct(RoleNode src, RoleNode dest)
	{
		ScribDel del = del();
		LWrapClient ls = new LWrapClient(this.source, (MessageSigNode) this.msg, src, dest);
		ls = (LWrapClient) ls.del(del);
		return ls;
	}*/
}

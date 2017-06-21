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
package org.scribble.visit.wf;

import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.ScribNode;
import org.scribble.del.AScribDel;
import org.scribble.del.ScribDel;
import org.scribble.main.AJob;
import org.scribble.main.ScribbleException;
import org.scribble.visit.EnvVisitor;
import org.scribble.visit.wf.env.AAnnotationEnv;

public class AAnnotationChecker extends EnvVisitor<AAnnotationEnv>
{
	public AAnnotationChecker(AJob job)
	{
		super(job);
	}
	
	@Override
	protected AAnnotationEnv makeRootProtocolDeclEnv(ProtocolDecl<?> pd)
	{
		AAnnotationEnv env = new AAnnotationEnv();
		return env;
	}
	
	@Override
	protected final void envEnter(ScribNode parent, ScribNode child) throws ScribbleException
	{
		super.envEnter(parent, child);
		ScribDel del = child.del();
		if (del instanceof AScribDel)  // FIXME?
		{
			((AScribDel) del).enterAnnotCheck(parent, child, this);
		}
	}
	
	@Override
	protected ScribNode envLeave(ScribNode parent, ScribNode child, ScribNode visited) throws ScribbleException
	{
		ScribDel del = visited.del();
		if (del instanceof AScribDel)  // FIXME?
		{
			visited = ((AScribDel) del).leaveAnnotCheck(parent, child, this, visited);
		}
		return super.envLeave(parent, child, visited);
	}
}

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
package org.scribble.ext.assrt.visit.wf;

import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.ScribNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.del.AssrtScribDel;
import org.scribble.ext.assrt.main.AssrtJob;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;
import org.scribble.visit.EnvVisitor;

public class AssrtAnnotationChecker extends EnvVisitor<AssrtAnnotationEnv>
{
	public AssrtAnnotationChecker(AssrtJob job)
	{
		super(job);
	}
	
	@Override
	protected AssrtAnnotationEnv makeRootProtocolDeclEnv(ProtocolDecl<?> pd)
	{
		AssrtAnnotationEnv env = new AssrtAnnotationEnv();
		return env;
	}
	
	@Override
	protected final void envEnter(ScribNode parent, ScribNode child) throws ScribbleException
	{
		super.envEnter(parent, child);
		ScribDel del = child.del();
		if (del instanceof AssrtScribDel)  // FIXME?
		{
			((AssrtScribDel) del).enterAnnotCheck(parent, child, this);
		}
	}
	
	@Override
	protected ScribNode envLeave(ScribNode parent, ScribNode child, ScribNode visited) throws ScribbleException
	{
		ScribDel del = visited.del();
		if (del instanceof AssrtScribDel)  // FIXME?
		{
			visited = ((AssrtScribDel) del).leaveAnnotCheck(parent, child, this, visited);
		}
		return super.envLeave(parent, child, visited);
	}
}

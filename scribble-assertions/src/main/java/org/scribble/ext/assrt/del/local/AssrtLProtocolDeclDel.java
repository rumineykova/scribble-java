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

import java.util.Collections;

import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LProtocolDecl;
import org.scribble.del.local.LProtocolDeclDel;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.visit.context.EGraphBuilder;

@Deprecated
public class AssrtLProtocolDeclDel extends LProtocolDeclDel
{
	public AssrtLProtocolDeclDel()
	{

	}
	
	@Override
	protected AssrtLProtocolDeclDel copy()
	{
		return new AssrtLProtocolDeclDel();
	}

	@Override
	public void enterEGraphBuilding(ScribNode parent, ScribNode child, EGraphBuilder builder)
	{
		LProtocolDecl lpd = (LProtocolDecl) child;
		builder.util.init(((AssrtEModelFactory) builder.job.ef).newAssrtEState(Collections.emptySet(), 
				//lpd.getHeader()));
				Collections.emptyMap()));
	}
}

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

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.ConnectAction;
import org.scribble.ast.MessageNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.type.kind.Local;

public abstract class LConnectionAction extends ConnectAction<Local>
		implements LSimpleSessionNode
{
	// ScribTreeAdaptor#create constructor
	public LConnectionAction(Token t)
	{
		super(t);
	}

	// Tree#dupNode constructor
	public LConnectionAction(LConnectionAction node)
	{
		super(node);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public LConnectionAction(CommonTree source, RoleNode src, MessageNode msg,
			RoleNode dest)
	{
		super(source, src, msg, dest);
	}
}

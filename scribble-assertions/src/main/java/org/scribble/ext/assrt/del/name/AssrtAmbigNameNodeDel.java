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
package org.scribble.ext.assrt.del.name;

import org.scribble.ast.ScribNode;
import org.scribble.ast.name.simple.AmbigNameNode;
import org.scribble.del.name.AmbigNameNodeDel;
import org.scribble.ext.assrt.sesstype.kind.AssrtVarNameKind;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.name.AmbigName;
import org.scribble.visit.wf.NameDisambiguator;

public class AssrtAmbigNameNodeDel extends AmbigNameNodeDel
{
	public AssrtAmbigNameNodeDel()
	{

	}

	// Currently only in "message positions (see Scribble.g ambiguousname)
	@Override
	public ScribNode leaveDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb, ScribNode visited) throws ScribbleException
	{
		AmbigNameNode ann = (AmbigNameNode) visited;
		AmbigName name = ann.toName();

		return disamb.isVarnameInScope(name.toString())
				? disamb.job.af.SimpleNameNode(ann.getSource(), AssrtVarNameKind.KIND, name.getLastElement())
				: super.leaveDisambiguation(parent, child, disamb, visited);
	}
}

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
package org.scribble.ext.assrt.visit;

import org.scribble.ast.AstFactory;
import org.scribble.ast.ProtocolBlock;
import org.scribble.ast.Recursion;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.main.Job;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Global;
import org.scribble.type.name.RecVar;
import org.scribble.visit.InlinedProtocolUnfolder;

@Deprecated
public class AssrtInlinedProtocolUnfolder extends InlinedProtocolUnfolder
{
	public AssrtInlinedProtocolUnfolder(Job job)
	{
		super(job);
	}

	// Duplicated from super
	@Override
	public void setRecVar(AstFactory af, RecVar recvar, Recursion<?> rec) throws ScribbleException
	{
		ProtocolBlock<?> block = (ProtocolBlock<?>) rec.getBlock().accept(this);
		RecVarNode rv = rec.recvar.clone(af);
		Recursion<?> unfolded;
		if (rec.getKind() == Global.KIND)
		{
			unfolded = ((GRecursion) rec).reconstruct(rv, (GProtocolBlock) block);
		}
		else
		{
			AssrtLRecursion lr = (AssrtLRecursion) rec;
			unfolded = lr.reconstruct(rv, (LProtocolBlock) block, lr.ass);  // FIXME: factor out better? -- could just use clone and replace del? (no, need to set new block)
		}
		this.recs.put(recvar, unfolded);
	}
}

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
package org.scribble.ext.assrt.core.visit.global;

import org.scribble.core.job.Core;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.session.Do;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSType;
import org.scribble.ext.assrt.core.type.session.NoSeq;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGType;
import org.scribble.ext.assrt.core.visit.AssrtCoreSTypeInliner;

public class AssrtCoreGTypeInliner extends AssrtCoreSTypeInliner<Global, 
		AssrtCoreGType>
{
	public AssrtCoreGTypeInliner(Core core)
	{
		super(core);
	}

	@Override
	public AssrtCoreSType<Global, AssrtCoreGType> visitDo(Do<Global, 
			NoSeq<Global>> n)
	{
		throw new RuntimeException("[TODO]");
	/*
		AssrtCoreSTypeFactory tf = (AssrtCoreSTypeFactory) this.core.config.tf;
		AssrtCoreGDo n1 = (AssrtCoreGDo) n;
		ProtoName<Global> fullname = n.proto;
		SubprotoSig sig = new SubprotoSig(fullname, n.roles, n.args);  // FIXME: assrt subproto sig with state vars
		RecVar rv = getInlinedRecVar(sig);
		if (hasSig(sig))
		{
			return tf.global.AssrtCoreGRecVar(n.getSource(), rv, n);
		}
		pushSig(sig);
		GProtocol g = this.core.getContext().getIntermediate(fullname);
		Substitutor<Global, GSeq> subs = this.core.config.vf.Substitutor(g.roles,
				n.roles, g.params, n.args);
		//GSeq inlined = (GSeq) g.def.visitWithNoEx(subs).visitWithNoEx(this);
		GSeq inlined = visitSeq(subs.visitSeq(g.def));
				// i.e. returning a GSeq -- rely on parent GSeq to inline
		popSig();
		return this.core.config.tf.global.GRecursion(null, rv, inlined);
	*/
	}

}

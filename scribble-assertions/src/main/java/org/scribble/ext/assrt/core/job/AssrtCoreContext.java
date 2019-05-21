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
package org.scribble.ext.assrt.core.job;

import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.core.job.Core;
import org.scribble.core.job.CoreContext;
import org.scribble.core.lang.global.GProtocol;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.ProtoName;
import org.scribble.core.visit.global.GTypeInliner;

// Global "static" context information for a Job -- single instance per Job, should not be shared between Jobs
// Mutable: projections, graphs, etc are added mutably later -- replaceModule also mutable setter -- "users" get this from the Job and expect to setter mutate "in place"
public class AssrtCoreContext extends CoreContext
{
	//protected final Map<ProtoName<Global>, GProtocol> imeds;

	/*// N.B. protos have pruned role decls -- CHECKME: prune args?
	// Mods are preserved
  // Keys are full names
	protected final Map<ProtoName<Global>, GProtocol> inlined = new HashMap<>();

	// CHECKME: rename projis?
	protected final Map<ProtoName<Local>, LProjection> iprojs = new HashMap<>();  // Projected from inlined; keys are full names*/
	
	protected AssrtCoreContext(Core core, Set<GProtocol> imeds)
	{
		super(core, imeds);
	}
	
	// Used by Core for pass running
	// Safer to return names and require user to get the target value by name, to make sure the value is created
	public Set<ProtoName<Global>> getParsedFullnames()
	{
		return this.imeds.keySet().stream().collect(Collectors.toSet());
	}
	
	public GProtocol getIntermediate(ProtoName<Global> fullname)
	{
		return this.imeds.get(fullname);
	}
	
	public GProtocol getInlined(ProtoName<Global> fullname)
	{
		GProtocol inlined = this.inlined.get(fullname);
		if (inlined == null)
		{
			GTypeInliner v = this.core.config.vf.global.GTypeInliner(this.core);  // Factor out?
			inlined = this.imeds.get(fullname).getInlined(v);  // Protocol.getInlined does pruneRecs
			addInlined(fullname, inlined);
		}
		return inlined;
	}
	
	protected void addInlined(ProtoName<Global> fullname, GProtocol g)
	{
		this.inlined.put(fullname, g);
	}
}

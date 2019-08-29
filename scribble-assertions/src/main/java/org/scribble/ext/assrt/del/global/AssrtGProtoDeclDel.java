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
package org.scribble.ext.assrt.del.global;

import org.scribble.del.global.GProtoDeclDel;

public class AssrtGProtoDeclDel extends GProtoDeclDel
{
	public AssrtGProtoDeclDel()
	{

	}
}















/*
	@Override
	protected AssrtGProtoDeclDel copy()
	{
		return new AssrtGProtoDeclDel();
	}

	// Duplicated from super
	@Override
	public GProtocolDecl
			leaveProjection(ScribNode parent, ScribNode child, Projector proj, ScribNode visited) throws ScribbleException
	{
		AssrtAstFactory af = (AssrtAstFactory) proj.job.af;

		Module root = proj.job.getContext().getModule(proj.getModuleContext().root);
		GProtocolDecl gpd = (GProtocolDecl) visited;
		AssrtGProtoHeader gph = (AssrtGProtoHeader) gpd.getHeader();
		Role self = proj.peekSelf();

		LProtocolNameNode pn = Projector.makeProjectedSimpleNameNode(af, gph.getSource(), gph.getDeclName(), self);
		RoleDeclList roledecls = gph.roledecls.project(af, self);
		NonRoleParamDeclList paramdecls = gph.paramdecls.project(af, self);
		//AssrtAssertion ass = gph.ass;  // null for empty  // FIXME: project?
		
		AssrtLProtoHeader hdr = gph.project(af, self, pn, roledecls, paramdecls, //ass);  // FIXME: make a header del and move there? -- and in the base clase, then don't need to override here, only the header
				gph.annotvars, gph.annotexprs,
				gph.ass);
		
		LProtocolDef def = (LProtocolDef) ((ProjectionEnv) gpd.def.del().env()).getProjection();
		LProtocolDecl lpd = gpd.project(af, root, self, hdr, def);  // FIXME: is root (always) the correct module? (wrt. LProjectionDeclDel?)
		
		Map<GProtocolName, Set<Role>> deps = ((AssrtGProtoDeclDel) gpd.del()).getGlobalProtocolDependencies(self);
		Module projected = ((ModuleDel) root.del()).createModuleForProjection(proj, root, gpd, lpd, deps);
		proj.addProjection(gpd.getFullMemberName(root), self, projected);
		return gpd;
	}
//*/

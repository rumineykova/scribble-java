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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ast.RoleDecl;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LProtocolDecl;
import org.scribble.ast.local.LProtocolHeader;
import org.scribble.del.local.LProtocolDeclDel;
import org.scribble.ext.assrt.ast.local.AssrtLProtocolHeader;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.main.ScribbleException;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;
import org.scribble.visit.context.EGraphBuilder;
import org.scribble.visit.context.ProjectedRoleDeclFixer;

public class AssrtLProjectionDeclDel extends org.scribble.del.local.LProjectionDeclDel
{
	public AssrtLProjectionDeclDel(GProtocolName fullname, Role self)
	{
		super(fullname, self);
	}
	
	@Override
	protected LProtocolDeclDel copy()
	{
		return new AssrtLProjectionDeclDel(this.fullname, this.self);
	}

	// Duplicated from super
	@Override
	public ScribNode leaveProjectedRoleDeclFixing(ScribNode parent, ScribNode child, ProjectedRoleDeclFixer fixer, ScribNode visited) throws ScribbleException
	{
		LProtocolDecl lpd = (LProtocolDecl) visited;
		// FIXME: ensure all role params are used, to avoid empty roledecllist
		Set<Role> occs = ((LProtocolDeclDel) lpd.del()).getProtocolDeclContext().getRoleOccurrences();
		List<RoleDecl> rds = lpd.header.roledecls.getDecls().stream().filter((rd) -> 
				occs.contains(rd.getDeclName())).collect(Collectors.toList());
		RoleDeclList rdl = fixer.job.af.RoleDeclList(lpd.header.roledecls.getSource(), rds);
		
		AssrtLProtocolHeader tmp = (AssrtLProtocolHeader) lpd.getHeader();
		LProtocolHeader hdr = tmp.reconstruct(tmp.getNameNode(), rdl, tmp.paramdecls, tmp.ass);
		LProtocolDecl fixed = lpd.reconstruct(hdr, lpd.def);
		
		fixer.job.debugPrintln("\n[DEBUG] Projected " + getSourceProtocol() + " for " + getSelfRole() + ":\n" + fixed);
		
		//return super.leaveProjectedRoleDeclFixing(parent, child, fixer, fixed);
		return fixed;
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

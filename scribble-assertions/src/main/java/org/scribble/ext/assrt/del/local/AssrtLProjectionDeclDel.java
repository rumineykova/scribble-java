package org.scribble.ext.assrt.del.local;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ast.RoleDecl;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LProjectionDecl;
import org.scribble.ast.local.LProtocolDecl;
import org.scribble.ast.local.LProtocolHeader;
import org.scribble.del.local.LProtocolDeclDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.local.AssrtLProtocolHeader;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
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
		
		AssrtLProtocolHeader tmp = (AssrtLProtocolHeader) lpd.getHeader();  // FIXME: make a LProtocolHeaderDel and factor out there? (would be less code duplication here)
		LProtocolHeader hdr = tmp.reconstruct(tmp.getNameNode(), rdl, tmp.paramdecls, //tmp.ass);
				tmp.annotvars, tmp.annotexprs);
		LProtocolDecl fixed = lpd.reconstruct(hdr, lpd.def);
		
		fixer.job.debugPrintln("\n[DEBUG] Projected " + getSourceProtocol() + " for " + getSelfRole() + ":\n" + fixed);
		
		//return super.leaveProjectedRoleDeclFixing(parent, child, fixer, fixed);
		return fixed;
	}

	@Override
	public void enterEGraphBuilding(ScribNode parent, ScribNode child, EGraphBuilder builder)
	{
		//LProtocolDecl lpd = (LProtocolDecl) child;
		LProjectionDecl lpd = (LProjectionDecl) child;
		AssrtLProtocolHeader hdr = (AssrtLProtocolHeader) lpd.header;
		
		Map<AssrtDataTypeVar, AssrtArithFormula> vars = new HashMap<>();
		//if (hdr.ass != null)
		if (!hdr.annotvars.isEmpty())
		{
			/*AssrtBinCompFormula bcf = hdr.getAnnotDataTypeVarDecls();
			vars.put(((AssrtIntVarFormula) bcf.left).toName(), (AssrtArithFormula) bcf.right);*/
			Iterator<AssrtArithExpr> exprs = hdr.annotexprs.iterator();
			hdr.annotvars.forEach(v -> vars.put(v.toName(), exprs.next().getFormula()));
		}
		
		//..FIXME: add rec-annots to AssrtSConfig
		
		builder.util.init(((AssrtEModelFactory) builder.job.ef).newAssrtEState(Collections.emptySet(), vars));
	}
}

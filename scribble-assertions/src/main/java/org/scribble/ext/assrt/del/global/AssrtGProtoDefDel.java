package org.scribble.ext.assrt.del.global;

import org.scribble.del.global.GProtoDefDel;
import org.scribble.ext.assrt.del.AssrtScribDel;

public class AssrtGProtoDefDel extends GProtoDefDel
		implements AssrtScribDel
{
	public AssrtGProtoDefDel()
	{

	}

	/*
	// Cf. GProtocolDefDel::enter/leaveProjection
	@Override
	//public AssrtGProtocolHeader leaveAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker, ScribNode visited) throws ScribException
	public void enterAnnotCheck(ScribNode parent, ScribNode child,
			AssrtAnnotationChecker checker) throws ScribException
			// Need to do on entry, before going to def
	{
		AssrtScribDel.super.enterAnnotCheck(parent, child, checker);  // Unnecessary
		
		AssrtGProtoHeader hdr = (AssrtGProtoHeader) ((GProtocolDecl) parent).getHeader();
		//if (hdr.ass == null)
		if (hdr.annotvars.isEmpty())
		{
			//return hdr;
			return;
		}
		
		AssrtAnnotationEnv env = checker.peekEnv().enterContext();

		//AssrtBinCompFormula vid
		Map<AssrtDataTypeVar, AssrtArithFormula> vid
				= ((AssrtGProtoHeader) hdr).getAnnotDataTypeVarDecls();  // Int var initialised-decl expr
		/*RoleCollector coll = new RoleCollector(checker.job, checker.getModuleContext());  // Would need to do for general recs
		((GProtocolDecl) parent).getDef().accept(coll);
		Set<Role> names = coll.getNames();* /

		GProtocolDecl gpd = (GProtocolDecl) parent;
		//Set<String> rds = gpd.header.roledecls.getRoles().stream().map(x -> x.toString()).collect(Collectors.toSet());
		Set<AssrtDataTypeVar> vars = //vid.right.getVars();
				 vid.values().stream()
				.flatMap(f -> f.getIntVars().stream())

				//.filter(v -> !rds.contains(v.toString()))  // HACK: filter out role occurrences -- all names currently parsed as AssrtIntVarFormula

				.collect(Collectors.toSet());

		Set<Role> ros = ((GProtocolDeclDel) gpd.del()).getProtocolDeclContext().getRoleOccurrences();
		
		for (AssrtDataTypeVar v : vars) 
		{
			for (Role r : ros)
			{
				if (!env.isDataTypeVarKnown(r, v))
				{
					throw new AssrtException("[assrt] Protocol header var " + v
							+ " is not in scope for role: " + r);
				}
			}
		}
		
		// N.B. this is a "syntactic" check -- may not directly correspond to model validation, which can "unfold" to give "repeat decls"
		//AssrtDataTypeVar var = ((AssrtIntVarFormula) vid.left).toName();
		for (AssrtDataTypeVar var : vid.keySet())
		{
			if (env.isDataTypeVarBound(var))  // Root env is made on ProtocolDecl enter -- so header env is defined
			{
				throw new AssrtException("[assrt] Protocol header var name " + var
						+ " is already declared.");
			}
			for (Role r : ros)
			{
				env = env.addAnnotDataType(r,
						new AssrtAnnotDataType(var, new DataType("int")));
						// FIXME: factor out int constant
			}
		}

		checker.pushEnv(env);
	}

	@Override
	public ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child,
			AssrtAnnotationChecker checker, ScribNode visited) throws ScribException
	{
		AssrtGProtoHeader hdr = (AssrtGProtoHeader) ((GProtocolDecl) parent).getHeader();
		//return (hdr.ass == null)  // FIXME -- cf. enterAnnotCheck
		return (hdr.annotvars.isEmpty())
				? AssrtScribDel.super.leaveAnnotCheck(parent, child, checker, visited)
				: ScribDelBase.popAndSetVisitorEnv(this, checker, visited);  // N.B.: doesn't call super -- cf. enter, which always calls super -- FIXME?
	}
	*/

}

package org.scribble.ext.assrt.del.global;

import java.util.Set;

import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.del.ScribDelBase;
import org.scribble.del.global.GProtocolDeclDel;
import org.scribble.del.global.GProtocolDefDel;
import org.scribble.ext.assrt.ast.global.AssrtGProtocolHeader;
import org.scribble.ext.assrt.del.AssrtScribDel;
import org.scribble.ext.assrt.main.AssrtException;
import org.scribble.ext.assrt.type.formula.AssrtBinCompFormula;
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;
import org.scribble.type.name.DataType;
import org.scribble.type.name.Role;

public class AssrtGProtocolDefDel extends GProtocolDefDel implements AssrtScribDel
{
	public AssrtGProtocolDefDel()
	{

	}

	@Override
	protected GProtocolDefDel copy()
	{
		AssrtGProtocolDefDel copy = new AssrtGProtocolDefDel();
		copy.inlined = this.inlined;
		return copy;
	}

	// Cf. GProtocolDefDel::enter/leaveProjection
	@Override
	//public AssrtGProtocolHeader leaveAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	public void enterAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker) throws ScribbleException  
			// Need to do on entry, before going to def
	{
		AssrtScribDel.super.enterAnnotCheck(parent, child, checker);  // Unnecessary
		
		AssrtGProtocolHeader hdr = (AssrtGProtocolHeader) ((GProtocolDecl) parent).getHeader();
		if (hdr.ass == null)
		{
			//return hdr;
			return;
		}
		
		AssrtAnnotationEnv env = checker.peekEnv().enterContext();

		AssrtBinCompFormula vid = ((AssrtGProtocolHeader) hdr).getAnnotDataTypeVarInitDecl();  // Int var initialised-decl expr
		/*RoleCollector coll = new RoleCollector(checker.job, checker.getModuleContext());  // Would need to do for general recs
		((GProtocolDecl) parent).getDef().accept(coll);
		Set<Role> names = coll.getNames();*/
		Set<Role> rs = ((GProtocolDeclDel) ((GProtocolDecl) parent).del()).getProtocolDeclContext().getRoleOccurrences();

		Set<AssrtDataTypeVar> vars = vid.right.getVars();
		for (Role r : rs)
		{
			for (AssrtDataTypeVar v : vars) 
			{
				if (!env.isDataTypeVarKnown(r, v))
				{
					throw new AssrtException("[assrt] Protocol header var " + v + " is not in scope for role: " + r);
				}
			}
		}
		
		AssrtDataTypeVar var = ((AssrtIntVarFormula) vid.left).toName();
		if (env.isDataTypeVarBound(var))  // Root env is made on ProtocolDecl enter -- so header env is defined
		{
			throw new AssrtException("[assrt] Protocol header var name " + var + " is already declared."); 
		}
		for (Role r : rs)
		{
			env = env.addAnnotDataType(r, new AssrtAnnotDataType(var, new DataType("int")));   // FIXME: factor out int constant
		}

		checker.pushEnv(env);
	}

	@Override
	public ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child,  AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		AssrtGProtocolHeader hdr = (AssrtGProtocolHeader) ((GProtocolDecl) parent).getHeader();
		return (hdr.ass == null)  // FIXME -- cf. enterAnnotCheck
				? AssrtScribDel.super.leaveAnnotCheck(parent, child, checker, visited)
				: ScribDelBase.popAndSetVisitorEnv(this, checker, visited);  // N.B.: doesn't call super -- cf. enter, which always calls super -- FIXME?
	}
}

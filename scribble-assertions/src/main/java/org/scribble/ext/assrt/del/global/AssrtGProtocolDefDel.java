package org.scribble.ext.assrt.del.global;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GInteractionSeq;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ast.global.GProtocolDef;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDelBase;
import org.scribble.del.global.GProtocolDeclDel;
import org.scribble.del.global.GProtocolDefDel;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.global.AssrtGProtocolHeader;
import org.scribble.ext.assrt.ast.global.AssrtGRecursion;
import org.scribble.ext.assrt.del.AssrtScribDel;
import org.scribble.ext.assrt.main.AssrtException;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBinCompFormula;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;
import org.scribble.type.SubprotocolSig;
import org.scribble.type.kind.RecVarKind;
import org.scribble.type.name.DataType;
import org.scribble.type.name.Role;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.env.InlineProtocolEnv;

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

	@Override
	public ScribNode leaveProtocolInlining(ScribNode parent, ScribNode child, ProtocolDefInliner dinlr, ScribNode visited) throws ScribbleException
	{
		AssrtAstFactory af = (AssrtAstFactory) dinlr.job.af;

		CommonTree blame = ((GProtocolDecl) parent).header.getSource();
		SubprotocolSig subsig = dinlr.peekStack();
		GProtocolDef def = (GProtocolDef) visited;
		GProtocolBlock block = (GProtocolBlock) ((InlineProtocolEnv) def.block.del().env()).getTranslation();	
		RecVarNode recvar = (RecVarNode) af.SimpleNameNode(blame, RecVarKind.KIND, dinlr.getSubprotocolRecVar(subsig).toString());

		//GRecursion rec = inl.job.af.GRecursion(blame, recvar, block);
		AssrtGProtocolHeader hdr = (AssrtGProtocolHeader) ((GProtocolDecl) parent).getHeader();
		//AssrtAssertion ass = hdr.ass;

		AssrtAssertion ass = hdr.annotvars.isEmpty()
				? null
				:  af.AssrtAssertion(hdr.getSource(), AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq,
								AssrtFormulaFactory.AssrtIntVar(hdr.annotvars.get(0).toString()), hdr.annotexprs.get(0).getFormula()));  // FIXME

		AssrtGRecursion rec = ((AssrtAstFactory) dinlr.job.af).AssrtGRecursion(blame, recvar, block, ass);

		GInteractionSeq gis = af.GInteractionSeq(blame, Arrays.asList(rec));
		GProtocolDef inlined = af.GProtocolDef(def.getSource(), dinlr.job.af.GProtocolBlock(blame, gis));
		dinlr.pushEnv(dinlr.popEnv().setTranslation(inlined));
		GProtocolDefDel copy = setInlinedProtocolDef(inlined);
		return (GProtocolDef) ScribDelBase.popAndSetVisitorEnv(this, dinlr, (GProtocolDef) def.del(copy));
	}

	// Cf. GProtocolDefDel::enter/leaveProjection
	@Override
	//public AssrtGProtocolHeader leaveAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	public void enterAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker) throws ScribbleException  
			// Need to do on entry, before going to def
	{
		AssrtScribDel.super.enterAnnotCheck(parent, child, checker);  // Unnecessary
		
		AssrtGProtocolHeader hdr = (AssrtGProtocolHeader) ((GProtocolDecl) parent).getHeader();
		//if (hdr.ass == null)
		if (hdr.annotvars.isEmpty())
		{
			//return hdr;
			return;
		}
		
		AssrtAnnotationEnv env = checker.peekEnv().enterContext();

		//AssrtBinCompFormula vid
		Map<AssrtDataTypeVar, AssrtArithFormula> vid
				= ((AssrtGProtocolHeader) hdr).getAnnotDataTypeVarDecls();  // Int var initialised-decl expr
		/*RoleCollector coll = new RoleCollector(checker.job, checker.getModuleContext());  // Would need to do for general recs
		((GProtocolDecl) parent).getDef().accept(coll);
		Set<Role> names = coll.getNames();*/
		Set<Role> rs = ((GProtocolDeclDel) ((GProtocolDecl) parent).del()).getProtocolDeclContext().getRoleOccurrences();

		Set<AssrtDataTypeVar> vars = //vid.right.getVars();
				vid.values().stream().flatMap(f -> f.getVars().stream()).collect(Collectors.toSet());
		for (AssrtDataTypeVar v : vars) 
		{
			for (Role r : rs)
			{
				if (!env.isDataTypeVarKnown(r, v))
				{
					throw new AssrtException("[assrt] Protocol header var " + v + " is not in scope for role: " + r);
				}
			}
		}
		
		// N.B. this is a "syntactic" check -- may not directly correspond to model validation, which can "unfold" to give "repeat decls"
		//AssrtDataTypeVar var = ((AssrtIntVarFormula) vid.left).toName();
		for (AssrtDataTypeVar var : vid.keySet())
		{
			if (env.isDataTypeVarBound(var))  // Root env is made on ProtocolDecl enter -- so header env is defined
			{
				throw new AssrtException("[assrt] Protocol header var name " + var + " is already declared."); 
			}
			for (Role r : rs)
			{
				env = env.addAnnotDataType(r, new AssrtAnnotDataType(var, new DataType("int")));   // FIXME: factor out int constant
			}
		}

		checker.pushEnv(env);
	}

	@Override
	public ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child,  AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		AssrtGProtocolHeader hdr = (AssrtGProtocolHeader) ((GProtocolDecl) parent).getHeader();
		//return (hdr.ass == null)  // FIXME -- cf. enterAnnotCheck
		return (hdr.annotvars.isEmpty())
				? AssrtScribDel.super.leaveAnnotCheck(parent, child, checker, visited)
				: ScribDelBase.popAndSetVisitorEnv(this, checker, visited);  // N.B.: doesn't call super -- cf. enter, which always calls super -- FIXME?
	}
}

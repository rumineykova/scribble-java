package org.scribble.ext.assrt.del.local;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.RoleArg;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.ScribNode;
import org.scribble.ast.context.ModuleContext;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ast.local.LDo;
import org.scribble.ast.local.LInteractionSeq;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.local.LProtocolDecl;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.local.LDoDel;
import org.scribble.del.local.LProjectionDeclDel;
import org.scribble.del.local.LProtocolDeclDel;
import org.scribble.ext.assrt.ast.AssrtArithAnnotation;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.ext.assrt.ast.local.AssrtLDo;
import org.scribble.ext.assrt.ast.local.AssrtLProtocolHeader;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.type.formula.AssrtBinCompFormula;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;
import org.scribble.main.JobContext;
import org.scribble.main.ScribbleException;
import org.scribble.type.SubprotocolSig;
import org.scribble.type.kind.RecVarKind;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.LProtocolName;
import org.scribble.type.name.Role;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.context.ProjectedRoleDeclFixer;
import org.scribble.visit.env.InlineProtocolEnv;
import org.scribble.visit.wf.NameDisambiguator;

public class AssrtLDoDel extends LDoDel
{
	@Override
	public ScribNode leaveDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb, ScribNode visited) throws ScribbleException
	{
		AssrtLDo doo = (AssrtLDo) visited;
		ModuleContext mc = disamb.getModuleContext();
		LProtocolName fullname = (LProtocolName) mc.getVisibleProtocolDeclFullName(doo.getProtocolNameNode().toName());
		LProtocolNameNode pnn = (LProtocolNameNode)
				disamb.job.af.QualifiedNameNode(doo.proto.getSource(), fullname.getKind(), fullname.getElements()); 
						// Didn't keep original namenode del

		//return doo.reconstruct(doo.roles, doo.args, pnn);
		return doo.reconstruct(doo.roles, doo.args, pnn, doo.annot);
	}

	// Only called if cycle
	public LDo visitForSubprotocolInlining(ProtocolDefInliner builder, LDo child)
	{
		CommonTree blame = child.getSource();  // Cf., GDoDel
		SubprotocolSig subsig = builder.peekStack();
		RecVarNode recvar = (RecVarNode) builder.job.af.SimpleNameNode(blame,
				RecVarKind.KIND, builder.getSubprotocolRecVar(subsig).toString());

		//LContinue inlined = builder.job.af.LContinue(blame, recvar);
		AssrtArithAnnotation annot = ((AssrtLDo) child).annot;
		AssrtLContinue inlined = ((AssrtAstFactory) builder.job.af).AssrtLContinue(blame, recvar, annot);

		builder.pushEnv(builder.popEnv().setTranslation(inlined));
		return child;
	}
	
	@Override
	public LDo leaveProtocolInlining(ScribNode parent, ScribNode child, ProtocolDefInliner dinlr, ScribNode visited) throws ScribbleException
	{
		CommonTree blame = visited.getSource();  // Cf., LDoDel
		SubprotocolSig subsig = dinlr.peekStack();
		if (!dinlr.isCycle())
		{
			RecVarNode recvar = (RecVarNode) dinlr.job.af.SimpleNameNode(blame, RecVarKind.KIND, dinlr.getSubprotocolRecVar(subsig).toString());
			LInteractionSeq gis = (LInteractionSeq) (((InlineProtocolEnv) dinlr.peekEnv()).getTranslation());
			LProtocolBlock gb = dinlr.job.af.LProtocolBlock(blame, gis);
			
			//LRecursion inlined = dinlr.job.af.LRecursion(blame, recvar, gb);
			AssrtLDo ldo = (AssrtLDo) child;
			LProtocolDecl lpd = ldo.getTargetProtocolDecl(dinlr.job.getContext(), dinlr.getModuleContext());
			AssrtBinCompFormula bcf = (AssrtBinCompFormula) ((AssrtLProtocolHeader) lpd.getHeader()).ass.getFormula();  // FIXME: bcf
			AssrtAssertion ass = ((AssrtAstFactory) dinlr.job.af).AssrtAssertion(null,
					AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq, (AssrtIntVarFormula) bcf.left, ldo.annot.getFormula()));  // FIXME: null source, and bcf
			AssrtLRecursion inlined = ((AssrtAstFactory) dinlr.job.af).AssrtLRecursion(blame, recvar, gb, ass);

			dinlr.pushEnv(dinlr.popEnv().setTranslation(inlined));
			dinlr.removeSubprotocolRecVar(subsig);
		}	
		return (LDo) super.leaveProtocolInlining(parent, child, dinlr, visited);
	}

	// Pre: this pass is only run on projections (LProjectionDeclDel has source global protocol info)
	@Override
	public ScribNode
			leaveProjectedRoleDeclFixing(ScribNode parent, ScribNode child, ProjectedRoleDeclFixer fixer, ScribNode visited) throws ScribbleException
	{
		JobContext jc = fixer.job.getContext();

		AssrtLDo ld = (AssrtLDo) visited;

		LProtocolDecl lpd = ld.getTargetProtocolDecl(jc, fixer.getModuleContext());
		
		GProtocolName source = ((LProjectionDeclDel) lpd.del()).getSourceProtocol();
		GProtocolDecl gpd = (GProtocolDecl) jc.getModule(source.getPrefix()).getProtocolDecl(source.getSimpleName());
		Iterator<RoleArg> roleargs = ld.roles.getDoArgs().iterator();
		Map<Role, Role> rolemap = gpd.header.roledecls.getRoles().stream().collect(
				Collectors.toMap(r -> r, r -> roleargs.next().val.toName()));
		Set<Role> occs = ((LProtocolDeclDel) lpd.del()).getProtocolDeclContext().getRoleOccurrences().stream().map(r ->
				rolemap.get(r)).collect(Collectors.toSet());

		List<RoleArg> ras = ld.roles.getDoArgs().stream().filter(ra -> occs.contains(ra.val.toName())).collect(Collectors.toList());
		RoleArgList roles = ld.roles.reconstruct(ras);

		//return super.leaveProjectedRoleDeclFixing(parent, child, fixer, ld.reconstruct(roles, ld.args, ld.getProtocolNameNode()));
		return ld.reconstruct(roles, ld.args, ld.getProtocolNameNode(), ld.annot);
	}
}

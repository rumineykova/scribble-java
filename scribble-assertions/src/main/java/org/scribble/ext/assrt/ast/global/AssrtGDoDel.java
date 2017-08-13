package org.scribble.ext.assrt.ast.global;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.ScribNode;
import org.scribble.ast.context.ModuleContext;
import org.scribble.ast.global.GDo;
import org.scribble.ast.global.GInteractionSeq;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ast.local.LDo;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDelBase;
import org.scribble.del.global.GDoDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.main.ScribbleException;
import org.scribble.type.SubprotocolSig;
import org.scribble.type.kind.RecVarKind;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.context.Projector;
import org.scribble.visit.env.InlineProtocolEnv;
import org.scribble.visit.wf.NameDisambiguator;

public class AssrtGDoDel extends GDoDel
{
	// Convert all visible names to full names for protocol inlining: otherwise could get clashes if directly inlining external visible names under the root modulecontext
	// Not done in G/LProtocolNameNodeDel because it's only for do-targets that this is needed (cf. ProtocolHeader)
	@Override
	public ScribNode leaveDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb, ScribNode visited) throws ScribbleException
	{
		AssrtGDo doo = (AssrtGDo) visited;
		ModuleContext mc = disamb.getModuleContext();
		GProtocolName fullname = (GProtocolName) mc.getVisibleProtocolDeclFullName(doo.getProtocolNameNode().toName());
		GProtocolNameNode pnn = (GProtocolNameNode)
				disamb.job.af.QualifiedNameNode(doo.proto.getSource(), fullname.getKind(), fullname.getElements()); 
						// Didn't keep original namenode del

		//return doo.reconstruct(doo.roles, doo.args, pnn);
		return doo.reconstruct(doo.roles, doo.args, pnn, //doo.annot);
				doo.annotexprs);
	}

	// Only called if cycle
	public GDo visitForSubprotocolInlining(ProtocolDefInliner builder, GDo child)
	{
		CommonTree blame = child.getSource();
		SubprotocolSig subsig = builder.peekStack();
		RecVarNode recvar = (RecVarNode) builder.job.af.SimpleNameNode(blame, RecVarKind.KIND, builder.getSubprotocolRecVar(subsig).toString());

		//GContinue inlined = builder.job.af.GContinue(blame, recvar);
		//AssrtArithExpr annot = ((AssrtGDo) child).annot;
		AssrtGDo gdo = (AssrtGDo) child;
		if (gdo.annotexprs.size() > 1)
		{
			throw new RuntimeException("[assrt] TODO: " + child);
		}
		AssrtArithExpr annot = gdo.annotexprs.isEmpty() ? null : gdo.annotexprs.get(0);
		AssrtGContinue inlined = ((AssrtAstFactory) builder.job.af).AssrtGContinue(blame, recvar, annot);

		builder.pushEnv(builder.popEnv().setTranslation(inlined));
		return child;
	}
	
	@Override
	public GDo leaveProtocolInlining(ScribNode parent, ScribNode child, ProtocolDefInliner dinlr, ScribNode visited) throws ScribbleException
	{
		if (!dinlr.isCycle())
		{
			CommonTree blame = visited.getSource();
			SubprotocolSig subsig = dinlr.peekStack();
			RecVarNode recvar = (RecVarNode) dinlr.job.af.SimpleNameNode(blame,
					RecVarKind.KIND, dinlr.getSubprotocolRecVar(subsig).toString());
			GInteractionSeq gis = (GInteractionSeq) (((InlineProtocolEnv) dinlr.peekEnv()).getTranslation());
			GProtocolBlock gb = dinlr.job.af.GProtocolBlock(blame, gis);

			//GRecursion inlined = inl.job.af.GRecursion(blame, recvar, gb);
			AssrtGDo gdo = (AssrtGDo) child;
			GProtocolDecl gpd = gdo.getTargetProtocolDecl(dinlr.job.getContext(), dinlr.getModuleContext());
			
			AssrtGProtocolHeader hdr = (AssrtGProtocolHeader) gpd.getHeader();
			AssrtGRecursion inlined;
			//if (hdr.ass == null)
			if (hdr.annotvars.isEmpty())
			{
				inlined = (AssrtGRecursion) dinlr.job.af.GRecursion(blame, recvar, gb);
			}
			else
			{
				List<AssrtIntVarNameNode> annotvars = ((AssrtGProtocolHeader) gpd.getHeader()).annotvars;
				inlined = ((AssrtAstFactory) dinlr.job.af).AssrtGRecursion(blame, recvar, gb, annotvars, gdo.annotexprs);
			}

			dinlr.pushEnv(dinlr.popEnv().setTranslation(inlined));
			dinlr.removeSubprotocolRecVar(subsig);
		}	

		//return (GDo) super.leaveProtocolInlining(parent, child, dinlr, visited);
		return (GDo) ScribDelBase.popAndSetVisitorEnv(this, dinlr, visited);
	}

	/*@Override
	public void enterProjection(ScribNode parent, ScribNode child, Projector proj) throws ScribbleException
	{
		GSimpleInteractionNodeDel.super.enterProjection(parent, child, proj);

		GDo gd = (GDo) child;
		Role self = proj.peekSelf();
		if (gd.roles.getRoles().contains(self))
		{
			// For correct name mangling, need to use the parameter corresponding to the self argument
			// N.B. -- this depends on Projector not following the Subprotocol pattern, otherwise self is wrong
			Role param = gd.getTargetRoleParameter(proj.job.getContext(), proj.getModuleContext(), self);
			proj.pushSelf(param);
		}
		else
		{
			proj.pushSelf(self);  // Dummy: just to make pop in leave work
		}
	}*/
	
	@Override
	public GDo leaveProjection(ScribNode parent, ScribNode child, Projector proj, ScribNode visited) throws ScribbleException //throws ScribbleException
	{
		//GDo gd = (GDo) visited;
		AssrtGDo gd = (AssrtGDo) visited;

		Role popped = proj.popSelf();
		Role self = proj.peekSelf();
		LDo ld = null;
		if (gd.roles.getRoles().contains(self))
		{
			ModuleContext mc = proj.getModuleContext();
			LProtocolNameNode target = Projector.makeProjectedFullNameNode(proj.job.af, gd.proto.getSource(), gd.getTargetProtocolDeclFullName(mc), popped);

			//projection = gd.project(proj.job.af, self, target);
			ld = gd.project(proj.job.af, self, target, //gd.annot);
					gd.annotexprs);
			
			// FIXME: do guarded recursive subprotocol checking (i.e. role is used during chain) in reachability checking? -- required role-usage makes local choice subject inference easier, but is restrictive (e.g. proto(A, B, C) { choice at A {A->B.do Proto(A,B,C)} or {A->B.B->C} }))
		}
		proj.pushEnv(proj.popEnv().setProjection(ld));

		//return (GDo) GSimpleInteractionNodeDel.super.leaveProjection(parent, child, proj, gd);
		return (GDo) ScribDelBase.popAndSetVisitorEnv(this, proj, visited);
	}
}

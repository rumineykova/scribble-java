package org.scribble.ext.assrt.del.global;

import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDelBase;
import org.scribble.del.global.GRecursionDel;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.global.AssrtGRecursion;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.del.AssrtICompoundInteractionNodeDel;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.context.Projector;
import org.scribble.visit.context.env.ProjectionEnv;
import org.scribble.visit.env.InlineProtocolEnv;

public class AssrtGRecursionDel extends GRecursionDel implements AssrtICompoundInteractionNodeDel
		//, AssrtScribDel  // FIXME: enter/leaveAnnotCheck, when assrt rec/continue supported by surface grammar
{

	@Override
	public ScribNode leaveProtocolInlining(ScribNode parent, ScribNode child, ProtocolDefInliner inl, ScribNode visited) throws ScribbleException
	{
		//GRecursion gr = (GRecursion) visited;
		AssrtGRecursion gr = (AssrtGRecursion) visited;

		RecVarNode recvar = (RecVarNode) ((InlineProtocolEnv) gr.recvar.del().env()).getTranslation();	
		GProtocolBlock block = (GProtocolBlock) ((InlineProtocolEnv) gr.block.del().env()).getTranslation();	

		//GRecursion inlined = inl.job.af.GRecursion(gr.getSource(), recvar, block);
		AssrtGRecursion inlined = ((AssrtAstFactory) inl.job.af).AssrtGRecursion(gr.getSource(), recvar, block, //gr.ass);
				gr.annotvars, gr.annotexprs,
				gr.ass);

		inl.pushEnv(inl.popEnv().setTranslation(inlined));
		return (GRecursion) super.leaveProtocolInlining(parent, child, inl, gr);
	}

	@Override
	public AssrtGRecursion leaveProjection(ScribNode parent, ScribNode child, Projector proj, ScribNode visited) throws ScribbleException
	{
		//GRecursion gr = (GRecursion) visited;
		AssrtGRecursion gr = (AssrtGRecursion) visited;

		LProtocolBlock block = (LProtocolBlock) ((ProjectionEnv) gr.block.del().env()).getProjection();

		//LRecursion lr = gr.project(proj.job.af, proj.peekSelf(), block);
		AssrtLRecursion lr = gr.project(proj.job.af, proj.peekSelf(), block, //gr.ass);
				gr.annotvars, gr.annotexprs,
				gr.ass);

		proj.pushEnv(proj.popEnv().setProjection(lr));

		//return (GRecursion) GCompoundInteractionNodeDel.super.leaveProjection(parent, child, proj, gr);
		return (AssrtGRecursion) ScribDelBase.popAndSetVisitorEnv(this, proj, visited);
	}
	
	@Override
	public ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child,  AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		
		// FIXME: handle ass if not null
		
		// Duplicated from GRecursionDel.leaveInlinedWFChoiceCheck
		GRecursion rec = (GRecursion) visited;
		AssrtAnnotationEnv merged = checker.popEnv().mergeContext((AssrtAnnotationEnv) rec.block.del().env());  // Merge block child env into current rec env
		checker.pushEnv(merged);
		return (GRecursion) AssrtICompoundInteractionNodeDel.super.leaveAnnotCheck(parent, child, checker, rec);  // Will merge current rec env into parent (and set env on del)
	}
}

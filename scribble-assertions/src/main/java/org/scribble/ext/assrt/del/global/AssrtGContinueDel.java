package org.scribble.ext.assrt.del.global;

import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GContinue;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDelBase;
import org.scribble.del.global.GContinueDel;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.global.AssrtGContinue;
import org.scribble.ext.assrt.ast.local.AssrtLContinue;
import org.scribble.main.ScribbleException;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.context.Projector;
import org.scribble.visit.env.InlineProtocolEnv;

public class AssrtGContinueDel extends GContinueDel
{
	@Override
	public GContinue leaveProtocolInlining(ScribNode parent, ScribNode child, ProtocolDefInliner dinlr, ScribNode visited) throws ScribbleException
	{
		//GContinue gc = (GContinue) visited;
		AssrtGContinue gc = (AssrtGContinue) visited;

		RecVarNode recvar = (RecVarNode) ((InlineProtocolEnv) gc.recvar.del().env()).getTranslation();	

		//GContinue inlined = inl.job.af.GContinue(gc.getSource(), recvar);
		GContinue inlined = ((AssrtAstFactory) dinlr.job.af).AssrtGContinue(gc.getSource(), recvar, gc.ass);

		dinlr.pushEnv(dinlr.popEnv().setTranslation(inlined));
		return (GContinue) super.leaveProtocolInlining(parent, child, dinlr, gc);
	}

	@Override
	public AssrtGContinue leaveProjection(ScribNode parent, ScribNode child, Projector proj, ScribNode visited) throws ScribbleException
	{
		//GContinue gc = (GContinue) visited;
		AssrtGContinue gc = (AssrtGContinue) visited;

		//LContinue lc = gc.project(proj.job.af, proj.peekSelf());
		AssrtLContinue lc = gc.project(proj.job.af, proj.peekSelf(), gc.ass);

		proj.pushEnv(proj.popEnv().setProjection(lc));

		//return (GContinue) GSimpleInteractionNodeDel.super.leaveProjection(parent, child, proj, gc);
		return (AssrtGContinue) ScribDelBase.popAndSetVisitorEnv(this, proj, visited);
	}
}

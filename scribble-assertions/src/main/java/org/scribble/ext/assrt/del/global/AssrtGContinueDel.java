package org.scribble.ext.assrt.del.global;

import org.scribble.del.global.GContinueDel;

public class AssrtGContinueDel extends GContinueDel
		//implements AssrtScribDel  // FIXME: enter/leaveAnnotCheck, when assrt rec/continue supported by surface grammar
{

}













/*
	@Override
	public AssrtGContinue leaveProjection(ScribNode parent, ScribNode child,
			Projector proj, ScribNode visited) throws ScribException
	{
		//GContinue gc = (GContinue) visited;
		AssrtGContinue gc = (AssrtGContinue) visited;

		//LContinue lc = gc.project(proj.job.af, proj.peekSelf());
		AssrtLContinue lc = gc.project(proj.job.af, proj.peekSelf(), gc.annotexprs);

		proj.pushEnv(proj.popEnv().setProjection(lc));

		//return (GContinue) GSimpleInteractionNodeDel.super.leaveProjection(parent, child, proj, gc);
		return (AssrtGContinue) ScribDelBase.popAndSetVisitorEnv(this, proj, visited);
	}
*/

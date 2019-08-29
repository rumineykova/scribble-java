package org.scribble.ext.assrt.del.global;

import org.scribble.del.global.GRecursionDel;
import org.scribble.ext.assrt.del.AssrtICompoundInteractionNodeDel;

public class AssrtGRecursionDel extends GRecursionDel
		implements AssrtICompoundInteractionNodeDel
		//, AssrtScribDel  // FIXME: enter/leaveAnnotCheck, when assrt rec/continue supported by surface grammar
{
	/*
	@Override
	public ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child,
			AssrtAnnotationChecker checker, ScribNode visited)
			throws ScribException
	{
		// FIXME: handle ass if not null
		
		// Duplicated from GRecursionDel.leaveInlinedWFChoiceCheck
		GRecursion rec = (GRecursion) visited;
		AssrtAnnotationEnv merged = checker.popEnv().mergeContext((AssrtAnnotationEnv) rec.block.del().env());  // Merge block child env into current rec env
		checker.pushEnv(merged);
		return (GRecursion) AssrtICompoundInteractionNodeDel.super.leaveAnnotCheck(
				parent, child, checker, rec);
				// Will merge current rec env into parent (and set env on del)
	}
	*/
}




/*
	@Override
	public AssrtGRecursion leaveProjection(ScribNode parent, ScribNode child, Projector proj, ScribNode visited) throws ScribException
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
*/

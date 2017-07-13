package org.scribble.ext.assrt.del.global;

import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GRecursion;
import org.scribble.del.global.GRecursionDel;
import org.scribble.ext.assrt.del.AssrtICompoundInteractionNodeDel;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;

public class AssrtGRecursionDel extends GRecursionDel implements AssrtICompoundInteractionNodeDel
{
	
	@Override
	public ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child,  AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		// Duplicated from GRecursionDel.leaveInlinedWFChoiceCheck
		GRecursion rec = (GRecursion) visited;
		AssrtAnnotationEnv merged = checker.popEnv().mergeContext((AssrtAnnotationEnv) rec.block.del().env());  // Merge block child env into current rec env
		checker.pushEnv(merged);
		return (GRecursion) AssrtICompoundInteractionNodeDel.super.leaveAnnotCheck(parent, child, checker, rec);  // Will merge current rec env into parent (and set env on del)
	}
}

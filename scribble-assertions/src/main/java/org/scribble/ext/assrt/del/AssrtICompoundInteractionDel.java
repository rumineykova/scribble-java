package org.scribble.ext.assrt.del;

import org.scribble.ast.ScribNode;
import org.scribble.del.ScribDelBase;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.main.ScribbleException;

public interface AssrtICompoundInteractionDel extends AssrtScribDel
{
	// Following CompoundInteractionDel.enter/leaveInlinedWFChoiceCheck
	@Override
	default void enterAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker) throws ScribbleException
	{
		ScribDelBase.pushVisitorEnv(this, checker);
	}
	
	@Override
	default ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		return ScribDelBase.popAndSetVisitorEnv(this, checker, visited);
	}
}

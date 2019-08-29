package org.scribble.ext.assrt.del;

public interface AssrtICompoundInteractionDel extends AssrtScribDel
{
	/*// Following CompoundInteractionDel.enter/leaveInlinedWFChoiceCheck
	@Override
	default void enterAnnotCheck(ScribNode parent, ScribNode child,
			AssrtAnnotationChecker checker) throws ScribbleException
	{
		ScribDelBase.pushVisitorEnv(this, checker);
	}
	
	@Override
	default ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child,
			AssrtAnnotationChecker checker, ScribNode visited)
			throws ScribbleException
	{
		return ScribDelBase.popAndSetVisitorEnv(this, checker, visited);
	}*/
}

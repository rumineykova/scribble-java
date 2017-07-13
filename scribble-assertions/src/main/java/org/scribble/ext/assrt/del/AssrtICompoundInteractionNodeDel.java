package org.scribble.ext.assrt.del;

import org.scribble.ast.ScribNode;
import org.scribble.ext.assrt.visit.wf.AssrtAnnotationChecker;
import org.scribble.ext.assrt.visit.wf.env.AssrtAnnotationEnv;
import org.scribble.main.ScribbleException;

public interface AssrtICompoundInteractionNodeDel extends AssrtICompoundInteractionDel
{

	@Override
	default ScribNode leaveAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker, ScribNode visited) throws ScribbleException
	{
		// Duplicated from CompoundInteractionNodeDel.leaveInlinedWFChoiceCheck
		AssrtAnnotationEnv visited_env = checker.popEnv();  // popAndSet current
		setEnv(visited_env);
		AssrtAnnotationEnv parent_env = checker.popEnv();  // pop-merge-push parent
		parent_env = parent_env.mergeContext(visited_env);
		checker.pushEnv(parent_env);
		return visited;
	}
}

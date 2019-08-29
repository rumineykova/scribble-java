package org.scribble.ext.assrt.del.global;

import org.scribble.del.global.GChoiceDel;
import org.scribble.ext.assrt.del.AssrtICompoundInteractionNodeDel;

public class AssrtGChoiceDel extends GChoiceDel
		implements AssrtICompoundInteractionNodeDel
{

	/*@Override
	public GChoice leaveAnnotCheck(ScribNode child,
			AssrtAnnotationChecker checker, ScribNode visited) throws ScribException
	{
		// Cf. GChoiceDel.leaveInlinedWFChoiceCheck
		GChoice cho = (GChoice) visited;
		List<AssrtAnnotationEnv> benvs = cho.getBlocks().stream()
				.map(b -> (AssrtAnnotationEnv) b.del().env()).collect(Collectors.toList());
		AssrtAnnotationEnv merged = checker.popEnv().mergeContexts(benvs); 
		checker.pushEnv(merged);
		return (GChoice) AssrtICompoundInteractionNodeDel.super.leaveAnnotCheck(
				parent, child, checker, visited);
				// Replaces base popAndSet to do pop, merge and set
	}*/
}





















	/*@Override
	public void enterAnnotCheck(ScribNode parent, ScribNode child, AssrtAnnotationChecker checker) throws ScribException
	{
		AssrtAnnotationEnv env = checker.peekEnv().enterContext();
		checker.pushEnv(env);
	}*/
	
	/*// Cf. GChoiceDel.leaveInlinedWFChoiceCheck
	@Override
	public GChoice leaveAnnotCheck(ScribNode parent, ScribNode child,  AssrtAnnotationChecker checker, ScribNode visited) throws ScribException
	{
		GChoice cho = (GChoice) visited;
		
		List<AssrtAnnotationEnv> benvs =
				cho.getBlocks().stream().map((b) -> (AssrtAnnotationEnv) b.del().env()).collect(Collectors.toList());
		AssrtAnnotationEnv merged = checker.popEnv().mergeContexts(benvs); 
		checker.pushEnv(merged);
				// Merged children into one here, now merge it into parent	

		// From CompoundInteractionNodeDel.leaveInlinedWFChoiceCheck
		// FIXME: don't need to push above, just to pop again here
		AssrtAnnotationEnv visited_env = checker.popEnv();  // popAndSet current
		setEnv(visited_env);
		AssrtAnnotationEnv parent_env = checker.popEnv();  // pop-merge-push parent
		parent_env = parent_env.mergeContext(visited_env);
		checker.pushEnv(parent_env);
		
		return cho;
	}*/
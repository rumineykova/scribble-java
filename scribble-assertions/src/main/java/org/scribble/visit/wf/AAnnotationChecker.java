package org.scribble.visit.wf;

import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.ScribNode;
import org.scribble.del.AScribDel;
import org.scribble.del.ScribDel;
import org.scribble.main.AJob;
import org.scribble.main.ScribbleException;
import org.scribble.visit.EnvVisitor;
import org.scribble.visit.wf.env.AAnnotationEnv;

public class AAnnotationChecker extends EnvVisitor<AAnnotationEnv>
{
	public AAnnotationChecker(AJob job)
	{
		super(job);
	}
	
	@Override
	protected AAnnotationEnv makeRootProtocolDeclEnv(ProtocolDecl<?> pd)
	{
		AAnnotationEnv env = new AAnnotationEnv();
		return env;
	}
	
	@Override
	protected final void envEnter(ScribNode parent, ScribNode child) throws ScribbleException
	{
		super.envEnter(parent, child);
		ScribDel del = child.del();
		if (del instanceof AScribDel)  // FIXME?
		{
			((AScribDel) del).enterAnnotCheck(parent, child, this);
		}
	}
	
	@Override
	protected ScribNode envLeave(ScribNode parent, ScribNode child, ScribNode visited) throws ScribbleException
	{
		ScribDel del = visited.del();
		if (del instanceof AScribDel)  // FIXME?
		{
			visited = ((AScribDel) del).leaveAnnotCheck(parent, child, this, visited);
		}
		return super.envLeave(parent, child, visited);
	}
}

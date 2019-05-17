package org.scribble.ext.assrt.visit.wf;

public class AssrtAnnotationChecker //extends EnvVisitor<AssrtAnnotationEnv>
{
	/*
	//public AssrtAnnotationChecker(AssrtJob job)
	public AssrtAnnotationChecker(Job job)  // FIXME: to fit reflection constructor call in Job.runVisitorPass
	{
		super(job);
	}
	
	@Override
	protected AssrtAnnotationEnv makeRootProtocolDeclEnv(ProtocolDecl<?> pd)
	{
		AssrtAnnotationEnv env = new AssrtAnnotationEnv();
		return env;
	}
	
	@Override
	protected final void envEnter(ScribNode parent, ScribNode child) throws ScribbleException
	{
		super.envEnter(parent, child);
		ScribDel del = child.del();
		if (del instanceof AssrtScribDel)  // FIXME?
		{
			((AssrtScribDel) del).enterAnnotCheck(parent, child, this);
		}
	}
	
	@Override
	protected ScribNode envLeave(ScribNode parent, ScribNode child, ScribNode visited) throws ScribbleException
	{
		ScribDel del = visited.del();
		if (del instanceof AssrtScribDel)  // FIXME?
		{
			visited = ((AssrtScribDel) del).leaveAnnotCheck(parent, child, this, visited);
		}
		return super.envLeave(parent, child, visited);
	}
	*/
}

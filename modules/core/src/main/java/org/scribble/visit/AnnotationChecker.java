package org.scribble.visit;

import java.util.HashMap;
import java.util.Map;

import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.ScribNode;
import org.scribble.main.Job;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.kind.ProtocolKind;
import org.scribble.sesstype.name.PayloadType;
import org.scribble.visit.wf.env.AnnotationEnv;
import org.scribble.visit.wf.env.ExplicitCorrelationEnv;
import org.scribble.visit.wf.env.ReachabilityEnv;


// By default, EnvVisitor only manipulates internal Env stack -- so AST/dels not affected
// Attaching Envs to Dels has to be done manually by each pass
public class AnnotationChecker extends EnvVisitor<AnnotationEnv>
{
	public Map<String, PayloadType<? extends PayloadTypeKind>> payloads;    
	
	public AnnotationChecker(Job job)
	{
		super(job);
		this.payloads = new HashMap<String, PayloadType<? extends PayloadTypeKind>>(); 
	}
	
	@Override
	protected AnnotationEnv makeRootProtocolDeclEnv(ProtocolDecl<?> pd)
	{
		AnnotationEnv env = new AnnotationEnv();
		return env;
	}
	
	
	@Override
	protected final void envEnter(ScribNode parent, ScribNode child) throws ScribbleException
	{
		super.envEnter(parent, child);
		child.del().enterAnnotCheck(parent, child, this);
	}
	
	@Override
	protected ScribNode envLeave(ScribNode parent, ScribNode child, ScribNode visited) throws ScribbleException
	{
		visited = visited.del().leaveAnnotCheck(parent, child, this, visited);
		return super.envLeave(parent, child, visited);
	}

}

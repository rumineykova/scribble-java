package org.scribble.visit;

import java.util.HashMap;
import java.util.Map;

import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.ScribNode;
import org.scribble.del.AScribDel;
import org.scribble.main.AJob;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.name.PayloadType;
import org.scribble.visit.wf.env.AnnotationEnv;


// By default, EnvVisitor only manipulates internal Env stack -- so AST/dels not affected
// Attaching Envs to Dels has to be done manually by each pass
public class AnnotationChecker extends EnvVisitor<AnnotationEnv>
{
	public Map<String, PayloadType<? extends PayloadTypeKind>> payloads;    
	
	public AnnotationChecker(AJob job)
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
		((AScribDel) child.del()).enterAnnotCheck(parent, child, this);  // FIXME: cast error
	}
	
	@Override
	protected ScribNode envLeave(ScribNode parent, ScribNode child, ScribNode visited) throws ScribbleException
	{
		visited = ((AScribDel) visited.del()).leaveAnnotCheck(parent, child, this, visited);
		return super.envLeave(parent, child, visited);
	}

}

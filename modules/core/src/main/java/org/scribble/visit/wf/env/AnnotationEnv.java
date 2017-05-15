package org.scribble.visit.wf.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.name.PayloadType;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.env.Env;

public class AnnotationEnv extends Env<AnnotationEnv>
{
	public Map<Role, Set<PayloadType<? extends PayloadTypeKind>>> payloads;
	
	public AnnotationEnv()
	{
		this(Collections.emptyMap());
	}
	
	protected AnnotationEnv(Map<Role, Set<PayloadType<? extends PayloadTypeKind>>> payloads)
	{
		this.payloads = new HashMap<Role, Set<PayloadType<? extends PayloadTypeKind>>>(payloads);
	}

	@Override
	public AnnotationEnv copy()
	{
		return new AnnotationEnv(new HashMap<Role, Set<PayloadType<? extends PayloadTypeKind>>>(this.payloads));
	}

	@Override
	public AnnotationEnv enterContext()
	{
		return copy();
	}

	public boolean checkIfPayloadValid(PayloadType<?> pe, Role src, List<Role> dests) throws ScribbleException 
	{
		boolean payloadExist = this.payloads.values().stream().anyMatch(x -> x.contains(pe)); 
		
		if (pe.isAnnotPayloadDecl() && payloadExist)
		{
			throw new ScribbleException("Payload " + pe.toString() + " is already declared"); 
		}
		else if (pe.isAnnotPayloadDecl() && !payloadExist)
		{
			this.addPayloadToRole(src, pe); 
			for(Role dest: dests) {
				this.addPayloadToRole(dest, pe);
			}
		}
		else if (pe.isAnnotPayloadInScope() && this.payloads.containsKey(src) && this.payloads.get(src).contains(pe))
		{
			throw new ScribbleException("Payload " + pe.toString() + " is not in scope");
		}
		else if (pe.isAnnotPayloadInScope())
		{
			// add the type int to the varname before adding the scope of the payload.
			for(Role dest: dests) {
				this.addPayloadToRole(dest, pe);
			}
		}
		return true; 
	}
	
	public void addPayloadToRole(Role role, PayloadType<?> pe) {
		
		if (!this.payloads.containsKey(role))
		{
			this.payloads.put(role, new HashSet<PayloadType<? extends PayloadTypeKind>>());
			
		}
		this.payloads.get(role).add(pe);
	}
	
	@Override
	public AnnotationEnv mergeContexts(List<AnnotationEnv> envs)
	{
		//AnnotationEnv env = copy(); 
		return this;
	}
	
}

package org.scribble.visit.wf.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.scribble.main.ScribbleException;
import org.scribble.sesstype.AnnotPayload;
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
		else if (pe.isAnnotPayloadInScope() && !this.payloads.containsKey(src) || 
				!this.payloads.get(src).stream().anyMatch(v -> ((AnnotPayload)v).varName.equals(pe)))
		{
			throw new ScribbleException("Payload " + pe.toString() + " is not in scope");
		}
		else if (pe.isAnnotPayloadInScope())
		{
			// add the type int to the varname before adding the scope of the payload.
			for(Role dest: dests) {
				Optional<PayloadType<? extends PayloadTypeKind>> newPe= this.payloads.get(src).stream()
						.filter(v -> ((AnnotPayload)v).varName.equals(pe)).findAny(); 
				this.addPayloadToRole(dest, newPe.get());
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
		
		Map<Role, Set<PayloadType<? extends PayloadTypeKind>>> payloads = 
		//		envs.stream().findAny().get().payloads;  
		//AnnotationEnv env = copy();
		//Map<Role, HashSet<PayloadType<? extends PayloadTypeKind>>> payloads = 
				envs.stream().flatMap(e-> e.payloads.entrySet().stream()).
				collect(Collectors.toMap(
		                Map.Entry<Role, Set<PayloadType<? extends PayloadTypeKind>>>::getKey,
		                Map.Entry<Role, Set<PayloadType<? extends PayloadTypeKind>>>::getValue,  
						(v1, v2) -> {
							Set<PayloadType<? extends PayloadTypeKind>> s = 
									v1.stream().filter(e -> v2.contains(e)).collect(Collectors.toSet()); 
							return s; })) ; 
				
		
		return new AnnotationEnv(payloads);  
	}
}

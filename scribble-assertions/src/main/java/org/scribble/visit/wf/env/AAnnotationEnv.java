package org.scribble.visit.wf.env;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.main.ScribbleException;
import org.scribble.sesstype.AAnnotPayload;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.name.APayloadType;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.env.Env;

public class AAnnotationEnv extends Env<AAnnotationEnv>
{
	public Map<Role, Set<APayloadType<? extends PayloadTypeKind>>> payloads;
	
	public AAnnotationEnv()
	{
		this(Collections.emptyMap());
	}
	
	protected AAnnotationEnv(Map<Role, Set<APayloadType<? extends PayloadTypeKind>>> payloads)
	{
		this.payloads = new HashMap<Role, Set<APayloadType<? extends PayloadTypeKind>>>(payloads);
	}

	@Override
	public AAnnotationEnv copy()
	{
		return new AAnnotationEnv(new HashMap<Role, Set<APayloadType<? extends PayloadTypeKind>>>(this.payloads));
	}

	@Override
	public AAnnotationEnv enterContext()
	{
		return copy();
	}

	public boolean checkIfPayloadValid(APayloadType<?> pe, Role src, List<Role> dests) throws ScribbleException 
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
				!this.payloads.get(src).stream().anyMatch(v -> ((AAnnotPayload)v).varName.equals(pe)))
		{
			throw new ScribbleException("Payload " + pe.toString() + " is not in scope");
		}
		else if (pe.isAnnotPayloadInScope())
		{
			// add the type int to the varname before adding the scope of the payload.
			for(Role dest: dests) {
				Optional<APayloadType<? extends PayloadTypeKind>> newPe= this.payloads.get(src).stream()
						.filter(v -> ((AAnnotPayload)v).varName.equals(pe)).findAny(); 
				this.addPayloadToRole(dest, newPe.get());
			}
		}
		return true; 
	}
	
	public void addPayloadToRole(Role role, APayloadType<?> pe) {
		
		if (!this.payloads.containsKey(role))
		{
			this.payloads.put(role, new HashSet<APayloadType<? extends PayloadTypeKind>>());
			
		}
		
		
		this.payloads.get(role).add(pe);
	}
	
	@Override
	public AAnnotationEnv mergeContexts(List<AAnnotationEnv> envs)
	{
		
		Map<Role, Set<APayloadType<? extends PayloadTypeKind>>> payloads = 
		//		envs.stream().findAny().get().payloads;  
		//AnnotationEnv env = copy();
		//Map<Role, HashSet<PayloadType<? extends PayloadTypeKind>>> payloads = 
				envs.stream().flatMap(e-> e.payloads.entrySet().stream()).
				collect(Collectors.toMap(
		                Map.Entry<Role, Set<APayloadType<? extends PayloadTypeKind>>>::getKey,
		                Map.Entry<Role, Set<APayloadType<? extends PayloadTypeKind>>>::getValue,  
						(v1, v2) -> {
							Set<APayloadType<? extends PayloadTypeKind>> s = 
									v1.stream().filter(e -> v2.contains(e)).collect(Collectors.toSet()); 
							return s; })) ; 
				
		
		return new AAnnotationEnv(payloads);  
	}
}

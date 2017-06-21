/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.ext.assrt.visit.wf.env;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.sesstype.AssrtAnnotPayload;
import org.scribble.ext.assrt.sesstype.name.AssrtPayloadType;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.env.Env;

public class AssrtAnnotationEnv extends Env<AssrtAnnotationEnv>
{
	public Map<Role, Set<AssrtPayloadType<? extends PayloadTypeKind>>> payloads;
	
	public AssrtAnnotationEnv()
	{
		this(Collections.emptyMap());
	}
	
	protected AssrtAnnotationEnv(Map<Role, Set<AssrtPayloadType<? extends PayloadTypeKind>>> payloads)
	{
		this.payloads = new HashMap<Role, Set<AssrtPayloadType<? extends PayloadTypeKind>>>(payloads);
	}

	@Override
	public AssrtAnnotationEnv copy()
	{
		return new AssrtAnnotationEnv(new HashMap<Role, Set<AssrtPayloadType<? extends PayloadTypeKind>>>(this.payloads));
	}

	@Override
	public AssrtAnnotationEnv enterContext()
	{
		return copy();
	}

	public boolean checkIfPayloadValid(AssrtPayloadType<?> pe, Role src, List<Role> dests) throws ScribbleException 
	{
		boolean payloadExist = this.payloads.values().stream().anyMatch(x -> x.contains(pe)); 
		
		if (pe.isAnnotPayloadDecl() && payloadExist)
		{
			throw new ScribbleException("Payload " + pe.toString() + " is already declared"); 
		}
		else if (pe.isAnnotPayloadDecl() && !payloadExist)
		{
			this.addPayloadToRole(src, pe); 
			for(Role dest: dests)
			{
				this.addPayloadToRole(dest, pe);
			}
		}
		else if (pe.isAnnotPayloadInScope() && !this.payloads.containsKey(src) || 
				!this.payloads.get(src).stream().anyMatch(v -> ((AssrtAnnotPayload)v).varName.equals(pe)))
		{
			throw new ScribbleException("Payload " + pe.toString() + " is not in scope");
		}
		else if (pe.isAnnotPayloadInScope())
		{
			// add the type int to the varname before adding the scope of the payload.
			for(Role dest: dests) {
				Optional<AssrtPayloadType<? extends PayloadTypeKind>> newPe= this.payloads.get(src).stream()
						.filter(v -> ((AssrtAnnotPayload)v).varName.equals(pe)).findAny(); 
				this.addPayloadToRole(dest, newPe.get());
			}
		}
		return true; 
	}
	
	public void addPayloadToRole(Role role, AssrtPayloadType<?> pe)
	{
		if (!this.payloads.containsKey(role))
		{
			this.payloads.put(role, new HashSet<AssrtPayloadType<? extends PayloadTypeKind>>());
			
		}
		
		this.payloads.get(role).add(pe);
	}
	
	@Override
	public AssrtAnnotationEnv mergeContext(AssrtAnnotationEnv child)
	{
		return mergeContexts(Arrays.asList(child));
	}
	
	@Override
	public AssrtAnnotationEnv mergeContexts(List<AssrtAnnotationEnv> envs)
	{
		Map<Role, Set<AssrtPayloadType<? extends PayloadTypeKind>>> payloads = 
		//		envs.stream().findAny().get().payloads;  
		//AnnotationEnv env = copy();
		//Map<Role, HashSet<PayloadType<? extends PayloadTypeKind>>> payloads = 
				envs.stream().flatMap(e-> e.payloads.entrySet().stream()).
				collect(Collectors.toMap(
		                Map.Entry<Role, Set<AssrtPayloadType<? extends PayloadTypeKind>>>::getKey,
		                Map.Entry<Role, Set<AssrtPayloadType<? extends PayloadTypeKind>>>::getValue,  
						(v1, v2) -> {
							Set<AssrtPayloadType<? extends PayloadTypeKind>> s = 
									v1.stream().filter(e -> v2.contains(e)).collect(Collectors.toSet()); 
							return s; })) ; 
		
		return new AssrtAnnotationEnv(payloads);  
	}
}

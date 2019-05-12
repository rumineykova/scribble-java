package org.scribble.ext.assrt.visit.wf.env;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.core.type.name.AssrtDataTypeVar;
import org.scribble.type.name.Role;
import org.scribble.visit.env.Env;

// FIXME: check and refactor syntactic checks with AssrtNameDisambiguator
public class AssrtAnnotationEnv extends Env<AssrtAnnotationEnv>
{
	// "May" analysis -- context merge takes the union
	private Map<Role, Set<AssrtAnnotDataType>> decls;  // Var declaration binding  // Role is the src role of the transfer -- not important?

	// "Must" analysis -- context merge takes the intersection
	private Map<Role, Set<AssrtDataTypeVar>> vars;  // "Knowledge" of var (according to message passing)
			// Redundant for assrt-core (validated on model, rather than syntactically), but still used by base assrt
	
	public AssrtAnnotationEnv()
	{
		this(Collections.emptyMap(), Collections.emptyMap());
	}
	
	protected AssrtAnnotationEnv(Map<Role, Set<AssrtAnnotDataType>> decls, Map<Role, Set<AssrtDataTypeVar>> vars)
	{
		this.decls = new HashMap<>(decls);
		this.vars = new HashMap<>(vars);
	}

	// "Global" syntactic scoping -- binding insensitive to roles (and DataType)
	public boolean isDataTypeVarBound(AssrtDataTypeVar v)
	{
		return this.decls.values().stream().flatMap(s -> s.stream()).anyMatch(adt -> adt.var.equals(v));
	}
	
	// Redundant for assrt-core (handled by model validation), but still used by base assrt
	public boolean isDataTypeVarKnown(Role r, AssrtDataTypeVar avn)
	{
		Set<AssrtDataTypeVar> tmp = this.vars.get(r);
		return tmp != null && tmp.stream().anyMatch(v -> v.equals(avn));
	}

	@Override
	public AssrtAnnotationEnv copy()
	{
		return new AssrtAnnotationEnv(new HashMap<>(this.decls), new HashMap<>(this.vars));
	}

	@Override
	public AssrtAnnotationEnv enterContext()
	{
		return copy();
	}

	@Override
	public AssrtAnnotationEnv mergeContext(AssrtAnnotationEnv child)
	{
		return mergeContexts(Arrays.asList(child));
	}
	
  // Cf. WFChoiceEnv merge pattern -- unlike WFChoiceEnv, no env "clearing" on choice enter -- child envs are originally direct copies of parent, so can merge for updated parent directly from children (cf. "running" merge parameterised on original parent)
	@Override
	public AssrtAnnotationEnv mergeContexts(List<AssrtAnnotationEnv> children)
	{
		AssrtAnnotationEnv copy = copy();

		// "Union"
		Map<Role, Set<AssrtAnnotDataType>> decls = children.stream()
				.flatMap(c -> c.decls.entrySet().stream())
				.collect(Collectors.toMap(
						Map.Entry<Role, Set<AssrtAnnotDataType>>::getKey,
						Map.Entry<Role, Set<AssrtAnnotDataType>>::getValue,
						(v1, v2) -> { v1.addAll(v2); return v1; }
				));
		copy.decls = decls;
		
		// "Intersection" -- implicitly handles vars "inherited" from before, e.g., a choice
		Set<Role> varsRoles = children.stream()  // FIXME: factor out with above?
				.flatMap(e -> e.vars.keySet().stream())
				.filter(r -> children.stream().map(e -> e.vars.keySet()).allMatch(ks -> ks.contains(r)))
				.collect(Collectors.toSet());
		Map<Role, Set<AssrtDataTypeVar>> vars = new HashMap<>();
		for (Role r : varsRoles)
		{
			vars.put(r, children.stream().map(c -> c.vars.get(r))
					 .reduce((s1, s2) -> s1.stream().filter(s2::contains).collect(Collectors.toSet())).get()  // Is there a "contains" directly for Stream? (i.e., bypass Set.contains)
			);
		}
		copy.vars = vars;
		
		return copy;
	}

	// Also bootstraps src as "knowing" adt.var
	public AssrtAnnotationEnv addAnnotDataType(Role src, AssrtAnnotDataType adt)
	{
		AssrtAnnotationEnv copy = copy();
		copy.addAnnotDataTypeAux(src, adt);
		copy.addDataTypeVarNameAux(src, adt.var);
		return copy;
	}
	
	private void addAnnotDataTypeAux(Role role, AssrtAnnotDataType adt)
	{
		Set<AssrtAnnotDataType> tmp = this.decls.get(role);
		if (tmp == null)
		{
			tmp = new HashSet<>();
			this.decls.put(role, tmp);
		}
		tmp.add(adt);
	}

	public AssrtAnnotationEnv addDataTypeVarName(Role role, AssrtDataTypeVar v)
	{
		AssrtAnnotationEnv copy = copy();
		copy.addDataTypeVarNameAux(role, v);
		return copy;
	}
	
	private void addDataTypeVarNameAux(Role role, AssrtDataTypeVar v)
	{
		Set<AssrtDataTypeVar> tmp = this.vars.get(role);
		if (tmp == null)
		{
			tmp = new HashSet<>();
			this.vars.put(role, tmp);
		}
		tmp.add(v);
	}
	
	@Override
	public String toString()
	{
		return "[decls=" + this.decls + ", vars=" + this.vars + "]";
	}
	
	

	/*public boolean checkIfPayloadValid(AssrtPayloadType<?> pe, Role src, List<Role> dests) throws ScribbleException 
	{
		boolean payloadExist = this.payloadTypes.values().stream().anyMatch(x -> x.contains(pe)); 
		
		if (pe.isAnnotVarDecl() && payloadExist)
		{
			throw new ScribbleException("Payload " + pe.toString() + " is already declared"); 
		}
		else if (pe.isAnnotVarDecl() && !payloadExist)
		{
			addPayloadToRole(src, pe); 
			for(Role dest: dests)
			{
				addPayloadToRole(dest, pe);
			}
		}
		else if (pe.isAnnotVarName() && !this.payloadTypes.containsKey(src)
				|| !this.payloadTypes.get(src).stream().anyMatch(v -> ((AssrtAnnotDataType) v).varName.equals(pe)))
		{
			throw new ScribbleException("Payload " + pe.toString() + " is not in scope");
		}
		else if (pe.isAnnotVarName())
		{
			// add the type int to the varname before adding the scope of the payload.
			for(Role dest: dests)
			{
				Optional<AssrtPayloadType<?>> newPe = this.payloadTypes.get(src).stream()
						.filter(v -> ((AssrtAnnotDataType) v).varName.equals(pe)).findAny(); 
				addPayloadToRole(dest, newPe.get());
			}
		}
		return true; 
	}
	
	// FIXME: not using immutable pattern
	public void addPayloadToRole(Role role, AssrtPayloadType<?> pe)
	{
		if (!this.payloadTypes.containsKey(role))
		{
			this.payloadTypes.put(role, new HashSet<AssrtPayloadType<? extends PayloadTypeKind>>());
			
		}
		this.payloadTypes.get(role).add(pe);
	}*/
	
	/*@Override
	public AssrtAnnotationEnv mergeContext(AssrtAnnotationEnv child)
	{
		//return mergeContexts(Arrays.asList(child));
		return child;  // FIXME: cf. original GChoiceDel.leaveAnnotCheck
	}
	
	// FIXME? not being used to merge into this, as supposed for Env -- factor out separately?
	@Override
	public AssrtAnnotationEnv mergeContexts(List<AssrtAnnotationEnv> envs)
	{
		Map<Role, Set<AssrtPayloadType<? extends PayloadTypeKind>>> payloads = 
		//		envs.stream().findAny().get().payloads;  
		//AnnotationEnv env = copy();
		//Map<Role, HashSet<PayloadType<? extends PayloadTypeKind>>> payloads = 
				envs.stream().flatMap(e -> e.payloadTypes.entrySet().stream())
						.collect(Collectors.toMap(
								Map.Entry<Role, Set<AssrtPayloadType<? extends PayloadTypeKind>>>::getKey,   // e.payloads is: Role -> Set<AsertPayloadType>
								Map.Entry<Role, Set<AssrtPayloadType<? extends PayloadTypeKind>>>::getValue,  
								(v1, v2) -> {
									Set<AssrtPayloadType<? extends PayloadTypeKind>> s = 
											v1.stream().filter(e -> v2.contains(e)).collect(Collectors.toSet());   // Intersection of v1 and v2 sets
									return s; 
								})
						); 
		
		return new AssrtAnnotationEnv(payloads);  
	}*/
}

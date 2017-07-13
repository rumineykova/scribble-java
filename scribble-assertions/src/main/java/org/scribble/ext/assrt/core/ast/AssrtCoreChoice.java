package org.scribble.ext.assrt.core.ast;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.scribble.sesstype.name.Role;

public abstract class AssrtCoreChoice<A extends AssrtCoreAction, C extends AssrtCoreType> implements AssrtCoreType
{
	public final Role src;  // Singleton -- no disconnect for now
	public final AssrtCoreActionKind kind;
	public final Role dest;
	public final Map<A, C> cases;
	
	public AssrtCoreChoice(Role src, AssrtCoreActionKind kind, Role dest, Map<A, C> cases)
	{
		this.src = src;
		this.kind = kind;
		this.dest = dest;
		this.cases = Collections.unmodifiableMap(cases);
	}
	
	/*@Override
	public Set<RecVar> freeVariables()
	{
		return cases.values().stream()
				.flatMap((v) -> v.body.freeVariables().stream())
				.collect(Collectors.toSet());
	}
	
	@Override
	public Set<Role> roles()
	{
		Set<Role> roles = cases.values().stream()
				.flatMap((v) -> v.body.roles().stream())
				.collect(Collectors.toSet());
		roles.addAll(java.util.Arrays.asList(src, dest));
		
		return roles;
	}*/
	
	@Override
	public String toString()
	{
		String s = 
				
				this.cases.entrySet().stream().map((e) -> e.getKey() + "." + e.getValue()).collect(Collectors.joining(" + "));
		if (this.cases.size() > 1)
		{
			s = "(" + s + ")";
		}
		return s;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 29;
		hash = 31 * hash + this.src.hashCode();
		hash = 31 * hash + this.kind.hashCode();
		hash = 31 * hash + this.dest.hashCode();
		hash = 31 * hash + this.cases.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreChoice))
		{
			return false;
		}
		AssrtCoreChoice<?, ?> them = (AssrtCoreChoice<?, ?>) obj;
		return them.canEquals(this)
				&& this.src.equals(them.src) && this.kind.equals(them.kind) && this.dest.equals(them.dest) && this.cases.equals(them.cases);
				// FIXME: check A, C are equal
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreChoice;
	}
}

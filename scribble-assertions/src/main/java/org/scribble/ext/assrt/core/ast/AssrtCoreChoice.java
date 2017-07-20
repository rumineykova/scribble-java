package org.scribble.ext.assrt.core.ast;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.scribble.sesstype.kind.ProtocolKind;
import org.scribble.sesstype.name.Role;

public abstract class AssrtCoreChoice<A extends AssrtCoreAction, C extends AssrtCoreType, K extends ProtocolKind> implements AssrtCoreType
{
	public final Role role;  // FIXME: RoleNode?  Cf. AssrtCoreAction.op/pay
	public final AssrtCoreActionKind<K> kind;
	public final Map<A, C> cases;
	
	// Pre: cases.size() > 1
	public AssrtCoreChoice(Role role, AssrtCoreActionKind<K> kind, Map<A, C> cases)
	{
		this.role = role;
		this.kind = kind;
		this.cases = Collections.unmodifiableMap(cases);
	}
	
	public abstract AssrtCoreActionKind<K> getKind();
	
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
	public int hashCode()
	{
		int hash = 29;
		hash = 31 * hash + this.role.hashCode();
		hash = 31 * hash + this.kind.hashCode();
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
		AssrtCoreChoice<?, ?, ?> them = (AssrtCoreChoice<?, ?, ?>) obj; 
		return them.canEquals(this)
				&& this.role.equals(them.role) && this.kind.equals(them.kind) && this.cases.equals(them.cases);
				// FIXME: check A, C are equal
				// FIXME: check kind
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreChoice;
	}
	
	protected String casesToString()
	{
		String s = this.cases.entrySet().stream()
				.map(e -> e.getKey() + "." + e.getValue()).collect(Collectors.joining(", "));
		if (this.cases.size() > 1)
		{
			s = "{ " + s + " }";
		}
		else
		{
			s = ":" + s;
		}
		return s;
	}
}

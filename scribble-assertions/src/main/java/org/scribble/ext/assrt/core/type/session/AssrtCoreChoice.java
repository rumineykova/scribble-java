package org.scribble.ext.assrt.core.type.session;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.ProtoKind;
import org.scribble.core.type.name.Role;

public abstract class AssrtCoreChoice<K extends ProtoKind, 
			B extends AssrtCoreType<K>>  // Without Seq complication, take kinded Type directly
		extends AssrtCoreTypeBase<K>
{
	public final Role role;
	public final AssrtCoreActionKind<K> kind;
	public final Map<AssrtCoreMsg, B> cases;
	
	// Pre: cases.size() > 1
	protected AssrtCoreChoice(CommonTree source, Role role,
			AssrtCoreActionKind<K> kind, Map<AssrtCoreMsg, B> cases)
	{
		super(source);
		this.role = role;
		this.kind = kind;
		this.cases = Collections.unmodifiableMap(cases);
	}
	
	public abstract AssrtCoreActionKind<K> getKind();
	
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
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreChoice))
		{
			return false;
		}
		AssrtCoreChoice<?, ?> them = (AssrtCoreChoice<?, ?>) o; 
		return super.equals(o)  // Checks canEquals -- implicitly checks kind
				&& this.role.equals(them.role) && this.kind.equals(them.kind)
				&& this.cases.equals(them.cases);
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreChoice;
	}
	
	protected String casesToString()
	{
		String s = this.cases.entrySet().stream()
				.map(e -> e.getKey() + "." + e.getValue())
				.collect(Collectors.joining(", "));
		s = (this.cases.size() > 1)
				? "{ " + s + " }"
				: ":" + s;
		return s;
	}
}

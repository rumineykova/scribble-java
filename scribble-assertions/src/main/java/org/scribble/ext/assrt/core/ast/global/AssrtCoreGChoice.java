package org.scribble.ext.assrt.core.ast.global;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.core.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.ast.AssrtCoreAction;
import org.scribble.ext.assrt.core.ast.AssrtCoreActionKind;
import org.scribble.ext.assrt.core.ast.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.ast.AssrtCoreChoice;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLActionKind;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLChoice;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLRecVar;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.sesstype.kind.Global;
import org.scribble.sesstype.name.RecVar;
import org.scribble.sesstype.name.Role;

public class AssrtCoreGChoice extends AssrtCoreChoice<AssrtCoreAction, AssrtCoreGType, Global> implements AssrtCoreGType
{
	public final Role src;   // Singleton -- no disconnect for now
	public final Role dest;  // this.dest == super.role

	public AssrtCoreGChoice(Role src, AssrtCoreGActionKind kind, Role dest, Map<AssrtCoreAction, AssrtCoreGType> cases)
	{
		super(dest, kind, cases);
		this.src = src;
		this.dest = dest;
	}
	
	public AssrtCoreGActionKind getKind()
	{
		return (AssrtCoreGActionKind) this.kind;
	}
	

	@Override
	public AssrtCoreLType project(AssrtCoreAstFactory af, Role subj) throws AssrtCoreSyntaxException
	{
		Map<AssrtCoreAction, AssrtCoreLType> projs = new HashMap<>();
		for (Entry<AssrtCoreAction, AssrtCoreGType> e : this.cases.entrySet())
		{
			//cases.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().project(af, subj)));
			projs.put(e.getKey(), e.getValue().project(af, subj));
		}
		
		if (this.src.equals(subj) || this.dest.equals(subj))
		{
			Role role = this.src.equals(subj) ? this.dest : this.src;
			return af.AssrtCoreLChoice(role, getKind().project(this.src, subj), projs);
		}
		else
		{
			if (projs.values().stream().allMatch(v -> (v instanceof AssrtCoreLRecVar)))
			{
				Set<RecVar> rvs = projs.values().stream().map(v -> ((AssrtCoreLRecVar) v).var).collect(Collectors.toSet());
				if (rvs.size() > 1)
				{
					throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n" + this + "\n onto " + subj + ": mixed unguarded rec vars: " + rvs);
				}
				return af.AssrtCoreLRecVar(rvs.iterator().next());
			}

			Map<AssrtCoreAction, AssrtCoreLType> filtered = projs.entrySet().stream()
				.filter(e -> !e.getValue().equals(AssrtCoreLEnd.END))
				//.collect(Collectors.toMap(e -> Map.Entry<AssrtCoreAction, AssrtCoreLType>::getKey, e -> Map.Entry<AssrtCoreAction, AssrtCoreLType>::getValue));
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		
			if (filtered.size() == 0)
			{
				return AssrtCoreLEnd.END;
			}
		
			Set<Role> roles = filtered.values().stream().map(v -> ((AssrtCoreLChoice) v).role).collect(Collectors.toSet());  // Subj not one of curent src/dest, must be projected inside each case to a guarded continuation
			if (roles.size() > 1)
			{
				throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n" + this + "\n onto " + subj + ": mixed peer roles: " + roles);
			}
			Set<AssrtCoreActionKind<?>> kinds = filtered.values().stream().map(v -> ((AssrtCoreLChoice) v).kind).collect(Collectors.toSet());  // Subj not one of curent src/dest, must be projected inside each case to a guarded continuation
			if (kinds.size() > 1)
			{
				throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n" + this + "\n onto " + subj + ": mixed action kinds: " + kinds);
			}
			
			Map<AssrtCoreAction, AssrtCoreLType> merged = new HashMap<>();
			filtered.values().forEach(v ->
			{
				AssrtCoreLChoice lc = (AssrtCoreLChoice) v;
				if (!lc.kind.equals(AssrtCoreLActionKind.RECEIVE))
				{
					throw new RuntimeException("[assrt-core] Shouldn't get here: " + lc);  // By role-enabling?
				}
				lc.cases.entrySet().forEach(e ->
				{
					AssrtCoreAction k = e.getKey();
					AssrtCoreLType b = e.getValue();
					if (merged.containsKey(k)) //&& !b.equals(merged.get(k))) // TODO
					{
						throw new RuntimeException("[assrt-core] Cannot project \n" + this + "\n onto " + subj + ": cannot merge: " + b + " and " + merged.get(k));
					}
					merged.put(k, b);
				});
			});
			
			return af.AssrtCoreLChoice(dest, getKind().project(this.src, subj), merged);
		}
	}
	
	@Override
	public int hashCode()
	{
		int hash = 2339;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.src.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreGChoice))
		{
			return false;
		}
		return super.equals(obj) && this.src.equals(((AssrtCoreGChoice) obj).src);  // Does canEquals
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreGChoice;
	}

	@Override
	public String toString()
	{
		return this.src.toString() + this.kind + this.dest + casesToString();  // toString needed?
	}
}

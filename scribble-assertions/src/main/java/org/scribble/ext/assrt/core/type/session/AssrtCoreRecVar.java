package org.scribble.ext.assrt.core.type.session;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.ProtoKind;
import org.scribble.core.type.name.RecVar;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;


public abstract class AssrtCoreRecVar<K extends ProtoKind>
		extends AssrtCoreSTypeBase<K>
{
	public final RecVar recvar;
	public final List<AssrtArithFormula> annotexprs;
	
	protected AssrtCoreRecVar(CommonTree source, RecVar var,
			List<AssrtArithFormula> annotexprs)
	{
		super(source);
		this.recvar = var;
		this.annotexprs = Collections.unmodifiableList(annotexprs);
	}

	@Override 
	public String toString()
	{
		return this.recvar.toString() + "<" + this.annotexprs.stream()
				.map(e -> e.toString()).collect(Collectors.joining(", ")) + ">";
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof AssrtCoreRecVar))
		{
			return false;
		}
		AssrtCoreRecVar<?> them = (AssrtCoreRecVar<?>) o;
		return super.equals(o) // Checks canEquals -- implicitly checks kind
				&& this.recvar.equals(them.recvar)
				&& this.annotexprs.equals(them.annotexprs);
	}

	@Override
	public int hashCode()
	{
		int hash = 6733;
		hash = 31*hash + this.recvar.hashCode();
		hash = 31*hash + this.annotexprs.hashCode();
		return hash;
	}
}

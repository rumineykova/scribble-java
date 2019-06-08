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
package org.scribble.ext.assrt.core.type.session;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.job.Core;
import org.scribble.core.type.kind.NonRoleParamKind;
import org.scribble.core.type.kind.ProtoKind;
import org.scribble.core.type.name.ProtoName;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Arg;
import org.scribble.ext.assrt.core.lang.AssrtCoreProtocol;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;

public abstract class AssrtCoreDo<K extends ProtoKind, B extends AssrtCoreSType<K, B>>
		extends AssrtCoreSTypeBase<K, B>
{
	public final ProtoName<K> proto;  // Currently disamb'd to fullname by GTypeTranslator (see GDoDel::translate)
	public final List<Role> roles;  // Ordered role args; pre: size > 2
	public final List<Arg<? extends NonRoleParamKind>> args;
			// NonRoleParamKind, not NonRoleArgKind, because latter includes AmbigKind due to parsing requirements

	public final List<AssrtAFormula> stateexprs;  // Cf. AssrtCoreRecVar

	public AssrtCoreDo(CommonTree source, ProtoName<K> proto, List<Role> roles,
			List<Arg<? extends NonRoleParamKind>> args, List<AssrtAFormula> sexprs)
	{
		super(source);
		this.proto = proto;
		this.roles = Collections.unmodifiableList(roles);
		this.args = Collections.unmodifiableList(args);
		this.stateexprs = sexprs;
	}

	@Override
	public <T> Stream<T> assrtCoreGather(
			Function<AssrtCoreSType<K, B>, Stream<T>> f)
	{
		// Currently, assrt-core gather only for rec pruning -- done after inlining, so not needed for do
		throw new RuntimeException("Deprecated for " + getClass() + ":\n\t" + this);
	}

	public abstract AssrtCoreProtocol<K, ?, ?> getTarget(Core core);  // CHECKME: "?"
	
	@Override
	public String toString()
	{
		return "do " + this.proto 
				+ "<"
				+ this.args.stream().map(x -> x.toString())
						.collect(Collectors.joining(", "))
				+ ">"
				+ "(" + this.roles.stream().map(x -> x.toString())
						.collect(Collectors.joining(", "))
				+ ")"
				+ "<" + this.stateexprs.stream().map(x -> x.toString())  // Cf. AssrtCoreRecVar
						.collect(Collectors.joining(", "))
				+ ">";
	}

	@Override
	public int hashCode()
	{
		int hash = 22129;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.proto.hashCode();
		hash = 31 * hash + this.roles.hashCode();
		hash = 31 * hash + this.args.hashCode();
		hash = 31 * hash + this.stateexprs.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreDo))
		{
			return false;
		}
		AssrtCoreDo<?, ?> them = (AssrtCoreDo<?, ?>) o;
		return super.equals(this)  // Does canEquals
				&& this.proto.equals(them.proto) && this.roles.equals(them.roles) 
				&& this.args.equals(them.args) && this.stateexprs.equals(them.stateexprs);
	}
}

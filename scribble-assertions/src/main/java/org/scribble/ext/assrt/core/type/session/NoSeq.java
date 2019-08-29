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

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.ProtoKind;
import org.scribble.core.type.session.SType;
import org.scribble.core.type.session.Seq;
import org.scribble.core.visit.STypeAgg;
import org.scribble.core.visit.STypeAggNoThrow;
import org.scribble.util.ScribException;

public abstract class NoSeq<K extends ProtoKind> extends Seq<K, NoSeq<K>>
{
	private NoSeq(CommonTree source)
	{
		super(source, Collections.emptyList());
	}

	@Override
	public NoSeq<K> reconstruct(CommonTree source,
			List<? extends SType<K, NoSeq<K>>> elems)
	{
		throw new RuntimeException("Unsupported for NoSeq: ");
	}
	
	@Override
	public <T> T visitWith(STypeAgg<K, NoSeq<K>, T> v) throws ScribException
	{
		throw new RuntimeException("Unsupported for NoSeq: ");
	}
	
	@Override
	public <T> T visitWithNoThrow(STypeAggNoThrow<K, NoSeq<K>, T> v)
	{
		throw new RuntimeException("Unsupported for NoSeq: ");
	}

	@Override
	public List<SType<K, NoSeq<K>>> getElements()
	{
		throw new RuntimeException("Unsupported for NoSeq: ");
	}

	/*@Override
	public int hashCode()
	{
		int hash = ...;
		hash = 31 * hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof NoSeq))
		{
			return false;
		}
		return super.equals(o); // Does canEquals
	}*/

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof NoSeq;
	}
}



















/*	@Override
	public Set<Role> checkRoleEnabling(Set<Role> enabled) throws ScribException
	{
		for (GType elem : getElements())
		{
			enabled = elem.checkRoleEnabling(enabled);
		}
		return enabled;
	}

	@Override
	public Map<Role, Role> checkExtChoiceConsistency(Map<Role, Role> enablers)
			throws ScribException
	{
		for (GType elem : getElements())
		{
			enablers = elem.checkExtChoiceConsistency(enablers);
		}
		return enablers;

	@Override
	public LSeq projectInlined(Role self)
	{
		return projectAux(this.elems.stream()
				.map(x -> ((GType) x).projectInlined(self)));  
	}
	
	private LSeq projectAux(Stream<LType> elems)
	{
		List<LType> tmp = elems.filter(x -> !x.equals(LSkip.SKIP))
				.collect(Collectors.toList());
		return new LSeq(null, tmp);  
				// Empty seqs converted to LSkip by GChoice/Recursion projection
				// And a WF top-level protocol cannot produce empty LSeq
				// So a projection never contains an empty LSeq -- i.e., "empty choice/rec" pruning unnecessary
	}

	@Override
	public LSeq project(ProjEnv v)
	{
		return projectAux(this.elems.stream()
				.map(x -> ((GType) x).project(v)));  
	}
	}
*/
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

import java.util.function.Function;
import java.util.stream.Stream;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.ProtoKind;
import org.scribble.core.type.session.SType;
import org.scribble.core.type.session.STypeBase;
import org.scribble.core.visit.STypeAgg;
import org.scribble.core.visit.STypeAggNoThrow;
import org.scribble.util.ScribException;

// SessTypeBase is to SessType as ScribNodeBase is to ScribNode
public abstract class AssrtCoreSTypeBase<K extends ProtoKind>
		extends STypeBase<K, NoSeq<K>>
{
	public AssrtCoreSTypeBase(CommonTree source)
	{
		super(source);
	}

	// CHECME: make AssrtCoreTypeVisitor?  Currently AssrtCoreSTypes aren't actual STypes, e.g., AssrtCoreChoice not a Choice
	// So STypeVisitor doesn't work directly, instead can override below by casting to, e.g., AssrtCoreSTypeVisitor, with a "custom" visiting pattern
	
	@Override
	public <T> T visitWith(STypeAgg<K, NoSeq<K>, T> v)
			throws ScribException
	{
		throw new RuntimeException("Unsupported: " + v.getClass());
	}

	@Override
	public <T> T visitWithNoThrow(STypeAggNoThrow<K, NoSeq<K>, T> v)
	{
		throw new RuntimeException("Unsupported: " + v.getClass());
	}

	@Override
	public <T> Stream<T> gather(
			Function<SType<K, NoSeq<K>>, Stream<T>> f)
	{
		throw new RuntimeException("Unsupported: ");
	}

	// Does *not* include this.source -- equals/hashCode is for "surface-level" syntactic equality of SessType only
	@Override
	public int hashCode()
	{
		int hash = 27239;
		hash = 31*hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreSTypeBase))
		{
			return false;
		}
		return super.equals(this);  // Checks canEquals
	}
}

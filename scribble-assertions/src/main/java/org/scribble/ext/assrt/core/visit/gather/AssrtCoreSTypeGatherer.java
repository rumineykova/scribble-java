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
package org.scribble.ext.assrt.core.visit.gather;

import java.util.Optional;
import java.util.stream.Stream;

import org.scribble.core.type.kind.ProtoKind;
import org.scribble.ext.assrt.core.type.session.AssrtCoreChoice;
import org.scribble.ext.assrt.core.type.session.AssrtCoreDo;
import org.scribble.ext.assrt.core.type.session.AssrtCoreEnd;
import org.scribble.ext.assrt.core.type.session.AssrtCoreRec;
import org.scribble.ext.assrt.core.type.session.AssrtCoreRecVar;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSType;

// Refactor as special case of Agg?
public abstract class AssrtCoreSTypeGatherer<K extends ProtoKind, 
		B extends AssrtCoreSType<K, B>, T>
{
	// Pass this to SType.gather, e.g., n.gather(new RoleGatherer<Global, GSeq>()::visit)
	public Stream<T> visit(AssrtCoreSType<K, B> n)
	{
		return typeSwitch(n).get();
	}

	// Can override by adding new cases when super call returns empty
	protected Optional<Stream<T>> typeSwitch(AssrtCoreSType<K, B> n)
	{
		return (n instanceof AssrtCoreChoice) 
					? Optional.of(visitChoice((AssrtCoreChoice<K, B>) n))
			: (n instanceof AssrtCoreEnd)
					? Optional.of(visitEnd((AssrtCoreEnd<K, B>) n))
			: (n instanceof AssrtCoreRec)    
					? Optional.of(visitRec((AssrtCoreRec<K, B>) n))
			: (n instanceof AssrtCoreRecVar)       
					? Optional.of(visitRecVar((AssrtCoreRecVar<K, B>) n))
			/*: (n instanceof AssrtCoreDo)     
					? Optional.of(this.visitDo((AssrtCoreDo<K, B>) n))*/
			: Optional.empty(); 
					// Better for extensibility than "manually" throwing Exception (e.g., for overriding)
					// N.B. currently causes exception on get in visit
	}

	public Stream<T> visitChoice(AssrtCoreChoice<K, B> n)
	{
		return Stream.of();
	}

	// CHECKME: split into ConnectionAction and MessageTransfer? cf. NonProtoDepsCollector -- and how about locals?
	// This is the "level" of reconstruct, though
	// Or offer all, with ConnectionAction and MessageTransfer delegating to DirectedInteraction by default?  (must order correctly in typeSwitch)

	public Stream<T> visitEnd(AssrtCoreEnd<K, B> n)
	{
		return Stream.of();
	}

	public Stream<T> visitRec(AssrtCoreRec<K, B> n)
	{
		return Stream.of();
	}

	public Stream<T> visitRecVar(AssrtCoreRecVar<K, B> n)
	{
		return Stream.of();
	}

	public Stream<T> visitDo(AssrtCoreDo<K, B> n)
	{
		return Stream.of();
	}
}

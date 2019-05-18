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
package org.scribble.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.antlr.runtime.Token;
import org.scribble.ast.global.GProtoDecl;
import org.scribble.ast.local.LProtoDecl;
import org.scribble.core.type.name.DataName;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.LProtoName;
import org.scribble.core.type.name.MemberName;
import org.scribble.core.type.name.ModuleName;
import org.scribble.core.type.name.ProtoName;
import org.scribble.core.type.name.SigName;
import org.scribble.del.DelFactory;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;

public class Module extends ScribNodeBase
{
	public static final int MODDECL_CHILD_INDEX = 0;
	
	// ScribTreeAdaptor#create constructor
	public Module(Token payload)
	{
		super(payload);
	}

	// Tree#dupNode constructor
	protected Module(Module node)
	{
		super(node);
	}

	public ModuleDecl getModuleDeclChild()
	{
		return (ModuleDecl) getChild(MODDECL_CHILD_INDEX);
	}
	
	public List<ImportDecl<?>> getImportDeclChildren()
	{
		return getMemberChildren(x -> x instanceof ImportDecl,
				x -> (ImportDecl<?>) x);
	}

	public List<NonProtoDecl<?>> getNonProtoDeclChildren()
	{
		return getMemberChildren(x -> x instanceof NonProtoDecl,
				x -> (NonProtoDecl<?>) x);
	}
	
	public List<ProtoDecl<?>> getProtoDeclChildren()
	{
		return getMemberChildren(x -> x instanceof ProtoDecl,
				x -> (ProtoDecl<?>) x);
	}

	// Not requiring T extends ModuleMember, ImportDecl is not a ModuleMember
	protected <T> List<T> getMemberChildren(
			Predicate<ScribNode> instanceOf, Function<ScribNode, T> cast)
	{
		List<T> res = new LinkedList<>();
		boolean b = false;
		// Start collecting from first instance, and stop on first non-instance or end
		for (ScribNode c : getChildren())
		{
			if (!b && instanceOf.test(c)) b = true;
			else if (b && !instanceOf.test(c)) break;
			if (b) res.add(cast.apply(c));
		}
		return res;
	}

	// "add", not "set"
	public void addScribChildren(ModuleDecl modd,
			List<? extends ImportDecl<?>> impds,
			List<? extends NonProtoDecl<?>> nprods, List<? extends ProtoDecl<?>> prods)
	{
		// Cf. above getters and Scribble.g children order
		addChild(modd);
		addChildren(impds);
		addChildren(nprods);
		addChildren(prods);
	}

	// Cf. CommonTree#dupNode
	@Override
	public Module dupNode()
	{
		return new Module(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		df.Module(this);
	}
	
	// Set args as children on a dup of this -- children *not* cloned
	protected Module reconstruct(ModuleDecl modd, List<ImportDecl<?>> imps,
			List<NonProtoDecl<?>> nprods, List<ProtoDecl<?>> prods)
	{
		Module dup = dupNode();
		dup.addScribChildren(modd, imps, nprods, prods);
		dup.setDel(del());  // No copy
		return dup;
	}
	
	@Override
	public Module visitChildren(AstVisitor v) throws ScribException
	{
		ModuleDecl modd = (ModuleDecl) visitChild(getModuleDeclChild(), v);
		// class equality check probably too restrictive -- FIXME: remove class checks
		List<ImportDecl<?>> impds = ScribNodeBase
				.visitChildListWithClassEqualityCheck(this, getImportDeclChildren(), v);
		List<NonProtoDecl<?>> nprods = ScribNodeBase
				.visitChildListWithClassEqualityCheck(this, getNonProtoDeclChildren(), v);
		List<ProtoDecl<?>> prods = ScribNodeBase
				.visitChildListWithClassEqualityCheck(this, getProtoDeclChildren(), v);
		return reconstruct(modd, impds, nprods, prods);
	}

	public DataDecl getTypeDeclChild(DataName simpname)
	{
		return (DataDecl) getNonProtoDeclChild(simpname, NonProtoDecl::isDataDecl);
	}

	public SigDecl getSigDeclChild(SigName simpname)
	{
		return (SigDecl) getNonProtoDeclChild(simpname, NonProtoDecl::isSigDecl);
	}

	// CHECKME: refactor like hasProtoDeclChild ?
	private NonProtoDecl<?> getNonProtoDeclChild(MemberName<?> simpname,
			Predicate<NonProtoDecl<?>> f)
	{
		Optional<? extends ScribNode> res = getChildren().stream()
				.filter(x -> (x instanceof NonProtoDecl) && f.test((NonProtoDecl<?>) x)
						&& ((NonProtoDecl<?>) x).getDeclName().equals(simpname))
				.findFirst();  // No duplication check, rely on WF (or currently ModuleContextBuilder?)
		if (!res.isPresent())
		{
			throw new RuntimeException("Non proto decl not found: " + simpname);
		}
		return (NonProtoDecl<?>) res.get();
	}
	
	public List<GProtoDecl> getGProtoDeclChildren()
	{
		return getProtoDeclChildren().stream().filter(ProtoDecl::isGlobal)
				.map(x -> (GProtoDecl) x).collect(Collectors.toList());
						// Less efficient, but smaller code
	}
	
	public List<LProtoDecl> getLProtoDeclChildren()
	{
		return getProtoDeclChildren().stream().filter(ProtoDecl::isLocal)
				.map(x -> (LProtoDecl) x).collect(Collectors.toList());
						// Less efficient, but smaller code
	}
	
	// CHECKME: allow global and local protocols with same simpname in same module? -- currently, no?
	public boolean hasGProtoDecl(GProtoName simpname)
	{
		return hasProtoDeclChild(simpname, ProtoDecl::isGlobal).isPresent();
	}
	
	// Pre: hasGProtocolDecl(simpname)
	public GProtoDecl getGProtoDeclChild(GProtoName simpname)
	{
		Optional<ProtoDecl<?>> res = hasProtoDeclChild(simpname,
				ProtoDecl::isGlobal);
		if (!res.isPresent())
		{
			throw new RuntimeException("Global proto decl not found: " + simpname);
		}
		return (GProtoDecl) res.get();
	}

	public LProtoDecl getLProtoDeclChild(LProtoName simpname)
	{
		Optional<ProtoDecl<?>> res = hasProtoDeclChild(simpname,
				ProtoDecl::isLocal);
		if (!res.isPresent())
		{
			throw new RuntimeException("Local proto decl not found: " + simpname);
		}
		return (LProtoDecl) res.get();
	}

	private Optional<ProtoDecl<?>> hasProtoDeclChild(
			ProtoName<?> simpname, Predicate<ProtoDecl<?>> f)
	{
		return getProtoDeclChildren().stream()
				.filter(x -> f.test(x) && x.getHeaderChild().getDeclName().equals(simpname))
				.findFirst();  // No duplication check, rely on WF (or currently ModuleContextBuilder?)
	}

	public ModuleName getFullModuleName()
	{
		return getModuleDeclChild().getFullModuleName();
	}

	@Override
	public String toString()
	{
		// CHECKME: just do on getChildren instead ?
		return getModuleDeclChild().toString()
				+ getImportDeclChildren().stream().map(x -> "\n" + x)
						.collect(Collectors.joining(""))
				+ getNonProtoDeclChildren().stream().map(x -> "\n" + x)
						.collect(Collectors.joining(""))
				+ getProtoDeclChildren().stream().map(x -> "\n" + x)
						.collect(Collectors.joining(""));
	}
}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*private static final Predicate<ScribNode> IS_GPROTOCOLDECL =
			x -> (x instanceof ProtocolDecl<?>) && ((ProtocolDecl<?>) x).isGlobal();

	private static final Function <ScribNode, GProtocolDecl> TO_GPROTOCOLDECL = 
			x -> (GProtocolDecl) x;*/

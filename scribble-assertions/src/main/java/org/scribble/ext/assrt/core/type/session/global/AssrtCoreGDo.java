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
package org.scribble.ext.assrt.core.type.session.global;

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.job.Core;
import org.scribble.core.lang.SubprotoSig;
import org.scribble.core.lang.global.GProtocol;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.kind.NonRoleParamKind;
import org.scribble.core.type.name.ProtoName;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Arg;
import org.scribble.core.type.session.global.GSeq;
import org.scribble.core.visit.Substitutor;
import org.scribble.ext.assrt.core.job.AssrtCore;
import org.scribble.ext.assrt.core.job.AssrtCoreContext;
import org.scribble.ext.assrt.core.lang.global.AssrtCoreGProtocol;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataName;
import org.scribble.ext.assrt.core.type.session.AssrtCoreDo;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.type.session.NoSeq;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLType;
import org.scribble.ext.assrt.core.visit.global.AssrtCoreGTypeInliner;

public class AssrtCoreGDo extends AssrtCoreDo<Global, AssrtCoreGType>
		implements AssrtCoreGType
{
	
	protected AssrtCoreGDo(CommonTree source, ProtoName<Global> proto,
			List<Role> roles, List<Arg<? extends NonRoleParamKind>> args)
	{
		super(source, proto, roles, args);
	}

	@Override
	public AssrtCoreGType inline(AssrtCoreGTypeInliner v)
	{
		ProtoName<Global> fullname = this.proto;
		SubprotoSig sig = new SubprotoSig(fullname, this.roles, this.args);
		RecVar rv = v.getInlinedRecVar(sig);
		AssrtCoreGTypeFactory tf = (AssrtCoreGTypeFactory) v.core.config.tf.global;
		if (v.hasSig(sig))
		{
			return tf.AssrtCoreGRecVar(getSource(), rv, this.aforms);
		}
		v.pushSig(sig);
		AssrtCoreGProtocol gpro = ((AssrtCoreContext) v.core.getContext())
				.getIntermediate(fullname);
		/*Substitutor<Global, NoSeq<Global>> subs = v.core.config.vf.Substitutor(gpro.roles,
				this.roles, gpro.params, this.args);
		//GSeq inlined = (GSeq) g.def.visitWithNoEx(subs).visitWithNoEx(this);
		GSeq inlined = visitSeq(subs.visitSeq(gpro.def));
				// i.e. returning a GSeq -- rely on parent GSeq to inline*/

		//Substitutions(rold, rnew, aold, anew)

		v.popSig();
		return tf.AssrtCoreGRec(null, rv, gpro.avars, inlined, gpro.bform);  
				// TODO: f/w entry: inline (replace) target avar exprs by this.aforms 
	}

	@Override
	public AssrtCoreGType pruneRecs(AssrtCore core)
	{
		return this;
	}

	@Override
	public AssrtCoreLType projectInlined(AssrtCore core, Role self,
			AssrtBFormula f) throws AssrtCoreSyntaxException
	{
		throw new RuntimeException("[TODO] :\n\t" + this);
	}

	@Override
	public List<AssrtAnnotDataName> collectAnnotDataVarDecls()
	{
		return Collections.emptyList();
	}
	
	@Override
	public AssrtCoreGProtocol getTarget(Core core)
	{
		return ((AssrtCore) core).getContext().getIntermediate(this.proto);  // Subproto visiting hardcoded to intermediate (i.e., parsed)
	}

	@Override
	public AssrtCoreGDo reconstruct(CommonTree source,
			ProtoName<Global> proto, List<Role> roles,
			List<Arg<? extends NonRoleParamKind>> args)
	{
		return new AssrtCoreGDo(source, proto, roles, args);
	}

	@Override
	public int hashCode()
	{
		int hash = 24917;
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
		if (!(o instanceof AssrtCoreGDo))
		{
			return false;
		}
		return super.equals(o);  // Does canEquals
	}

	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreGDo;
	}

}







	
	
	
	
	
	
	
	
	
	

	/*
	@Override
	public GProtoName getProto()
	{
		return (GProtoName) this.proto;
	}
 
	// CHECKME: factor up to base?
	@Override
	public GType getInlined(STypeInliner v)
	{
		GProtocolName fullname = this.proto;
		SubprotoSig sig = new SubprotoSig(fullname, this.roles, this.args);
		RecVar rv = v.getInlinedRecVar(sig);
		if (v.hasSig(sig))
		{
			return new GContinue(getSource(), rv);
		}
		v.pushSig(sig);
		GProtocol g = v.job.getContext().getIntermediate(fullname);
		Substitutions subs = 
				new Substitutions(g.roles, this.roles, g.params, this.args);
		GSeq inlined = g.def.substitute(subs).getInlined(v);
				// i.e. returning a GSeq -- rely on parent GSeq to inline
		v.popSig();
		return new GRecursion(null, rv, inlined);
	}
	
	@Override
	public Set<Role> checkRoleEnabling(Set<Role> enabled) throws ScribException
	{
		throw new RuntimeException("Unsupported for Do: " + this);
	}

	@Override
	public Map<Role, Role> checkExtChoiceConsistency(Map<Role, Role> enablers)
			throws ScribException
	{
		throw new RuntimeException("Unsupported for Do: " + this);
	}

	@Override
	public LType projectInlined(Role self)
	{
		throw new RuntimeException("Unsupported for Do: " + this);
	}

	@Override
	public LType project(ProjEnv v)
	{
		if (!this.roles.contains(v.self))
		{
			return LSkip.SKIP;
		}

		JobContext jobc = v.job.getContext();
		//GProtocolDecl gpd = jobc.getParsed(this.proto);
		GProtocol gpd = jobc.getIntermediate(this.proto);
		Role targSelf = gpd.roles.get(this.roles.indexOf(v.self));

		GProtocol imed = jobc.getIntermediate(this.proto);
		if (!imed.roles.contains(targSelf))  // CHECKME: because roles already pruned for intermed decl?
		{
			return LSkip.SKIP;
		}

		LProtocolName fullname = ProjEnv.projectFullProtocolName(this.proto,
				targSelf);
		Substitutions subs = new Substitutions(imed.roles, this.roles,
				Collections.emptyList(), Collections.emptyList());
		List<Role> used = jobc.getInlined(this.proto).roles.stream()
				.map(x -> subs.subsRole(x)).collect(Collectors.toList());
		List<Role> rs = this.roles.stream().filter(x -> used.contains(x))
				.map(x -> x.equals(v.self) ? Role.SELF : x)
						// CHECKME: "self" also explcitily used for Choice, but implicitly for MessageTransfer, inconsistent?
				.collect(Collectors.toList());
		return new LDo(null, fullname, rs, this.args);  // TODO CHECKME: prune args?
	}
	*/











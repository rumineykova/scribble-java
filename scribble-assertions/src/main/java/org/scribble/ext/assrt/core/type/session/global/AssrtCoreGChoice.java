package org.scribble.ext.assrt.core.type.session.global;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBinBoolFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.core.type.session.AssrtCoreActionKind;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSTypeFactory;
import org.scribble.ext.assrt.core.type.session.AssrtCoreChoice;
import org.scribble.ext.assrt.core.type.session.AssrtCoreMsg;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLActionKind;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLChoice;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLRecVar;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLType;

public class AssrtCoreGChoice extends AssrtCoreChoice<Global, AssrtCoreGType>
		implements AssrtCoreGType
{
	public final Role src;   // Singleton -- no disconnect for now
	public final Role dest;  // this.dest == super.role

	protected AssrtCoreGChoice(CommonTree source, Role src,
			AssrtCoreGActionKind kind, Role dest,
			Map<AssrtCoreMsg, AssrtCoreGType> cases)
	{
		super(source, dest, kind, cases);
		this.src = src;
		this.dest = dest;
	}

	@Override
	public List<AssrtAnnotDataType> collectAnnotDataTypeVarDecls()
	{
		List<AssrtAnnotDataType> res = this.cases.keySet().stream()
				.flatMap(a -> a.pay.stream()).collect(Collectors.toList());
		this.cases.keySet().forEach(
				a -> res.addAll(this.cases.get(a).collectAnnotDataTypeVarDecls()));
		return res;
	}

	@Override
	public AssrtCoreLType project(AssrtCoreSTypeFactory af, Role r,
			AssrtBoolFormula f) throws AssrtCoreSyntaxException
	{
		Map<AssrtCoreMsg, AssrtCoreLType> projs = new HashMap<>();
		for (Entry<AssrtCoreMsg, AssrtCoreGType> e : this.cases.entrySet())
		{
			AssrtCoreMsg a = e.getKey();
			AssrtBoolFormula fproj = AssrtFormulaFactory
					.AssrtBinBool(AssrtBinBoolFormula.Op.And, f, a.ass);

			if (this.dest.equals(r))  // Projecting receiver side
			{
				/*Set<AssrtDataTypeVar> vs = fproj.getVars();
						// FIXME: converting Set to List
				vs.remove(a.ass.getVars());
				if (!vs.isEmpty())
				{
					List<AssrtIntVarFormula> tmp = vs.stream().map(v -> AssrtFormulaFactory.AssrtIntVar(v.toString())).collect(Collectors.toList());  
					fproj = AssrtFormulaFactory.AssrtExistsFormula(tmp, fproj);
				}*/
				
				//..FIXME: Checking TS on model, so we don't need the projection to "syntactically" record the "assertion history" in this way?
				//..or just follow original sender-only assertion implementation?
				fproj = AssrtTrueFormula.TRUE;  
						// HACK FIXME: currently also hacking all "message-carried assertions" to True, i.e., AssrtCoreState::fireSend/Request -- cf. AssrtSConfig::fire
						// AssrtCoreState::getReceive/AcceptFireable currently use syntactic equality of assertions

				a = af.AssrtCoreAction(a.op, a.pay, fproj);
			}

			projs.put(a, e.getValue().project(af, r, fproj));
					// N.B. local actions directly preserved from globals -- so core-receive also has assertion (cf. AssrtGMessageTransfer.project, currently no AssrtLReceive)
					// FIXME: receive assertion projection -- should not be the same as send?
		}
		
		// "Simple" cases
		if (this.src.equals(r) || this.dest.equals(r))
		{
			Role role = this.src.equals(r) ? this.dest : this.src;
			return af.local.AssrtCoreLChoice(null, role,
					getKind().project(this.src, r), projs);
		}

		// "Merge"
		if (projs.values().stream().anyMatch(v -> (v instanceof AssrtCoreLRecVar)))
		{
			if (projs.values().stream()
					.anyMatch(v -> !(v instanceof AssrtCoreLRecVar)))
			{
				throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n"
						+ this + "\n onto " + r + ": cannot merge unguarded rec vars.");
			}

			Set<RecVar> rvs = projs.values().stream()
					.map(v -> ((AssrtCoreLRecVar) v).recvar).collect(Collectors.toSet());
			Set<List<AssrtArithFormula>> fs = projs.values().stream()
					.map(v -> ((AssrtCoreLRecVar) v).annotexprs)
					.collect(Collectors.toSet());
					// CHECKME? syntactic equality of exprs
			if (rvs.size() > 1 || fs.size() > 1)
			{
				throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n"
						+ this + "\n onto " + r + ": mixed unguarded rec vars: " + rvs);
			}

			return af.local.AssrtCoreLRecVar(null, rvs.iterator().next(),
					fs.iterator().next());
		}
		
		List<AssrtCoreLType> filtered = projs.values().stream()
			.filter(v -> !v.equals(AssrtCoreLEnd.END))
			////.collect(Collectors.toMap(e -> Map.Entry<AssrtCoreAction, AssrtCoreLType>::getKey, e -> Map.Entry<AssrtCoreAction, AssrtCoreLType>::getValue));
			//.map(v -> (AssrtCoreLChoice) v)
			.collect(Collectors.toList());
	
		if (filtered.size() == 0)
		{
			return AssrtCoreLEnd.END;
		}
		else if (filtered.size() == 1)
		{
			return //(AssrtCoreLChoice)
					filtered.iterator().next();  // RecVar disallowed above
		}
		
		List<AssrtCoreLChoice> choices = filtered.stream()
				.map(v -> (AssrtCoreLChoice) v).collect(Collectors.toList());
	
		Set<Role> roles = choices.stream().map(v -> v.role)
				.collect(Collectors.toSet());
				// Subj not one of curent src/dest, must be projected inside each case to a guarded continuation
		if (roles.size() > 1)
		{
			throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n" + this
					+ "\n onto " + r + ": mixed peer roles: " + roles);
		}
		Set<AssrtCoreActionKind<?>> kinds = choices.stream().map(v -> v.kind)
				.collect(Collectors.toSet());
				// Subj not one of curent src/dest, must be projected inside each case to a guarded continuation
		if (kinds.size() > 1)
		{
			throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n" + this
					+ "\n onto " + r + ": mixed action kinds: " + kinds);
		}
		
		Map<AssrtCoreMsg, AssrtCoreLType> merged = new HashMap<>();
		choices.forEach(v ->
		{
			if (!v.kind.equals(AssrtCoreLActionKind.RECV))
			{
				throw new RuntimeException("[assrt-core] Shouldn't get here: " + v);  // By role-enabling?
			}
			v.cases.entrySet().forEach(e ->
			{
				AssrtCoreMsg k = e.getKey();
				AssrtCoreLType b = e.getValue();
				if (merged.containsKey(k)) //&& !b.equals(merged.get(k))) // TODO
				{
							throw new RuntimeException(
									"[assrt-core] Cannot project \n" + this + "\n onto " + r
											+ ": cannot merge: " + b + " and " + merged.get(k));
						}
				merged.put(k, b);
			});
		});
		
		return af.local.AssrtCoreLChoice(null, roles.iterator().next(),
				AssrtCoreLActionKind.RECV, merged);
	}
	
	@Override
	public AssrtCoreGActionKind getKind()
	{
		return (AssrtCoreGActionKind) this.kind;
	}

	@Override
	public String toString()
	{
		return this.src.toString() + this.kind + this.dest + casesToString();  // toString needed?
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
		return super.equals(obj)  // Checks canEquals
				&& this.src.equals(((AssrtCoreGChoice) obj).src);  
	}
	
	@Override
	public boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreGChoice;
	}
}

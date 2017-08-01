package org.scribble.ext.assrt.core.ast.global;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.core.ast.AssrtCoreAction;
import org.scribble.ext.assrt.core.ast.AssrtCoreActionKind;
import org.scribble.ext.assrt.core.ast.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.ast.AssrtCoreChoice;
import org.scribble.ext.assrt.core.ast.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLActionKind;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLChoice;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLRecVar;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBinBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;
import org.scribble.type.kind.Global;
import org.scribble.type.name.RecVar;
import org.scribble.type.name.Role;

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
	
	/*public Map<AssrtCoreAction, AssrtCoreGType> getCases()
	{
		return this.cases;
	}*/

	@Override
	public List<AssrtAnnotDataType> collectAnnotDataTypes()
	{
		List<AssrtAnnotDataType> res = this.cases.keySet().stream().map(a -> a.pay).collect(Collectors.toList());
		this.cases.keySet().forEach(a -> res.addAll(this.cases.get(a).collectAnnotDataTypes()));
		return res;
	}

	@Override
	public AssrtCoreLType project(AssrtCoreAstFactory af, Role r, AssrtBoolFormula f) throws AssrtCoreSyntaxException
	{
		Map<AssrtCoreAction, AssrtCoreLType> projs = new HashMap<>();
		for (Entry<AssrtCoreAction, AssrtCoreGType> e : this.cases.entrySet())
		{
			AssrtCoreAction a = e.getKey();
			AssrtBoolFormula fproj = AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, f, a.ass);

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
			return af.AssrtCoreLChoice(role, getKind().project(this.src, r), projs);
		}

		// "Merge"
		if (projs.values().stream().anyMatch(v -> (v instanceof AssrtCoreLRecVar)))
		{
			if (projs.values().stream().anyMatch(v -> !(v instanceof AssrtCoreLRecVar)))
			{
				throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n" + this + "\n onto " + r + ": cannot merge unguarded rec vars.");
			}

			Set<RecVar> rvs = projs.values().stream().map(v -> ((AssrtCoreLRecVar) v).var).collect(Collectors.toSet());
			Set<AssrtArithFormula> fs = projs.values().stream().map(v -> ((AssrtCoreLRecVar) v).expr).collect(Collectors.toSet());  // FIXME? syntactic equality of exprs
			if (rvs.size() > 1 || fs.size() > 1)
			{
				throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n" + this + "\n onto " + r + ": mixed unguarded rec vars: " + rvs);
			}
			return af.AssrtCoreLRecVar(rvs.iterator().next(), fs.iterator().next());
		}
		
		List<AssrtCoreLChoice> filtered = projs.values().stream()
			.filter(v -> !v.equals(AssrtCoreLEnd.END))
			//.collect(Collectors.toMap(e -> Map.Entry<AssrtCoreAction, AssrtCoreLType>::getKey, e -> Map.Entry<AssrtCoreAction, AssrtCoreLType>::getValue));
			.map(v -> (AssrtCoreLChoice) v)
			.collect(Collectors.toList());
	
		if (filtered.size() == 0)
		{
			return AssrtCoreLEnd.END;
		}
		else if (filtered.size() == 1)
		{
			return (AssrtCoreLChoice) filtered.iterator().next();  // RecVar disallowed above
		}
	
		Set<Role> roles = filtered.stream().map(v -> v.role).collect(Collectors.toSet());  // Subj not one of curent src/dest, must be projected inside each case to a guarded continuation
		if (roles.size() > 1)
		{
			throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n" + this + "\n onto " + r + ": mixed peer roles: " + roles);
		}
		Set<AssrtCoreActionKind<?>> kinds = filtered.stream().map(v -> v.kind).collect(Collectors.toSet());  // Subj not one of curent src/dest, must be projected inside each case to a guarded continuation
		if (kinds.size() > 1)
		{
			throw new AssrtCoreSyntaxException("[assrt-core] Cannot project \n" + this + "\n onto " + r + ": mixed action kinds: " + kinds);
		}
		
		Map<AssrtCoreAction, AssrtCoreLType> merged = new HashMap<>();
		filtered.forEach(v ->
		{
			if (!v.kind.equals(AssrtCoreLActionKind.RECEIVE))
			{
				throw new RuntimeException("[assrt-core] Shouldn't get here: " + v);  // By role-enabling?
			}
			v.cases.entrySet().forEach(e ->
			{
				AssrtCoreAction k = e.getKey();
				AssrtCoreLType b = e.getValue();
				if (merged.containsKey(k)) //&& !b.equals(merged.get(k))) // TODO
				{
					throw new RuntimeException("[assrt-core] Cannot project \n" + this + "\n onto " + r + ": cannot merge: " + b + " and " + merged.get(k));
				}
				merged.put(k, b);
			});
		});
		
		return af.AssrtCoreLChoice(roles.iterator().next(), AssrtCoreLActionKind.RECEIVE, merged);
	}
	
	@Override
	public AssrtCoreGActionKind getKind()
	{
		return (AssrtCoreGActionKind) this.kind;
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

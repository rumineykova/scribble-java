package org.scribble.ext.assrt.core.model.global;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.core.model.endpoint.actions.ESend;
import org.scribble.core.model.global.SModel;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.job.AssrtCoreArgs;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.job.AssrtJob;
import org.scribble.job.Job;

// 1-bounded LTS
// Factor out with SGraph/SModel?
public class AssrtCoreSModel extends SModel
{
	protected AssrtCoreSModel(AssrtCoreSGraph graph)
	{
		super(graph);
	}
	
	public AssrtCoreSafetyErrors getSafetyErrors(Job job, GProtoName simpname)
			// Maybe refactor simpname (root proto) into the (AssrtCore)Job
	{
		AssrtJob ajob = (AssrtJob) job;
		AssrtCoreArgs args = (AssrtCoreArgs) ajob.config.args;
		
		Collection<AssrtCoreSState> all = this.allStates.values();
		
		Set<AssrtCoreSState> recepts = all.stream()
				.filter(AssrtCoreSState::isReceptionError).collect(Collectors.toSet());
		Set<AssrtCoreSState> orphans = all.stream()
				.filter(s -> s.isOrphanError(this.E0)).collect(Collectors.toSet());
		Set<AssrtCoreSState> unfins = all.stream()
				.filter(s -> s.isUnfinishedRoleError(this.E0))
				.collect(Collectors.toSet());
		Set<AssrtCoreSState> conns = all.stream()
				.filter(AssrtCoreSState::isConnectionError).collect(Collectors.toSet());
		Set<AssrtCoreSState> unconns = all.stream()
				.filter(AssrtCoreSState::isUnconnectedError)
				.collect(Collectors.toSet());
		Set<AssrtCoreSState> syncs = all.stream()
				.filter(AssrtCoreSState::isSynchronisationError)
				.collect(Collectors.toSet());
		Set<AssrtCoreSState> disconns = Collections.emptySet();  // TODO
				//this.allStates.values().stream().filter(AssrtCoreSState::isDisconnectedError).collect(Collectors.toSet());

		Set<AssrtCoreSState> unknownVars = all.stream()
				.filter(s -> s.getUnknownDataVarError(job, simpname))
				.collect(Collectors.toSet());

		Set<AssrtCoreSState> asserts = null;  
		Set<AssrtCoreSState> unsats = null;   
		Set<AssrtCoreSState> recasserts = null;

		if (args.z3Batching)
		{
			// Check for all errors in a single pass -- any errors can be categorised later
			Set<AssrtBFormula> fs = new HashSet<>();
			fs.addAll(all.stream()
					.flatMap(s -> s.getAssertionProgressChecks(job, simpname).stream())
					.collect(Collectors.toSet()));
			fs.addAll(all.stream()
					.flatMap(s -> s.getSatisfiableChecks(job, simpname).stream())
					.collect(Collectors.toSet()));
			fs.addAll(all.stream().flatMap(
					s -> s.getRecursionAssertionChecks(job, simpname, this.init).stream())
					.collect(Collectors.toSet()));
			/*String smt2 = fs.stream().filter(f -> !f.equals(AssrtTrueFormula.TRUE))
						.map(f -> "(assert " + f.toSmt2Formula() + ")\n").collect(Collectors.joining(""))
					+ "(check-sat)\n(exit)";
			if (Z3Wrapper.checkSat(smt2))*/  // FIXME: won't work for unint-funs without using Z3Wrapper.toSmt2
			if (ajob.checkSat(simpname, fs))
			{	
				asserts = Collections.emptySet();
				unsats = Collections.emptySet();
				recasserts = Collections.emptySet();
			}
		}
		
		if (!args.z3Batching || asserts == null)
		{
			asserts = all.stream()
					.filter(s -> s.isAssertionProgressError(job, simpname))
					.collect(Collectors.toSet());
			unsats = all.stream().filter(s -> s.getAssertUnsatErrors(job, simpname))
					.collect(Collectors.toSet());
			recasserts = all.stream()
					.filter(s -> s.isRecursionAssertionError(job, simpname, this.init))
					.collect(Collectors.toSet());
		}
		
		/*Set<AssrtCoreSState> portOpens = this.allStates.values().stream().filter(AssrtCoreSState::isPortOpenError).collect(Collectors.toSet());
		Set<AssrtCoreSState> portOwners = this.allStates.values().stream().filter(AssrtCoreSState::isPortOwnershipError).collect(Collectors.toSet());*/

		return new AssrtCoreSafetyErrors(recepts, orphans, unfins, conns, unconns,
				syncs, disconns, unknownVars, asserts, unsats, recasserts);
	}
	
	public boolean isActive(AssrtCoreSState s, Role r)
	{
		return AssrtCoreSState.isActive(s.getP().get(r), this.E0.get(r).id);
	}
	
	public AssrtCoreProgressErrors getProgressErrors()
	{
		//return new AssrtCoreProgressErrors(Collections.emptyMap(), Collections.emptyMap());
		Map<Role, Set<Set<AssrtCoreSState>>> roleProgress = new HashMap<>();
				/*this.E0.keySet().stream().collect(Collectors.toMap((r) -> r, (r) ->
					this.termSets.stream().map((ts) -> ts.stream().map((i) -> this.allStates.get(i)).collect(Collectors.toSet()))
						.filter((ts) -> ts.stream().allMatch((s) -> !s.getSubjects().contains(r)))
							.collect(Collectors.toSet())));*/
		for (Role r : this.E0.keySet())
		{
			for (Set<Integer> ts : this.termSets)	
			{
				if (ts.stream().allMatch(i -> isActive(this.allStates.get(i), r)
						&& !this.allStates.get(i).getSubjects().contains(r)))
				{
					Set<Set<AssrtCoreSState>> set = roleProgress.get(r);
					if (set == null)
					{
						set = new HashSet<>();
						roleProgress.put(r, set);
					}
					set.add(ts.stream().map((i) -> this.allStates.get(i))
							.collect(Collectors.toSet()));
				}	
			}
		}

		Map<ESend, Set<Set<AssrtCoreSState>>> eventualReception = new HashMap<>();
		for (Role r1 : this.E0.keySet())
		{
			for (Role r2 : this.E0.keySet())
			{
				if (!r1.equals(r2))
				{
					for (Set<Integer> ts : this.termSets)	
					{
						AssrtCoreSState s1 = this.allStates.get(ts.iterator().next());
						ESend es = s1.getQ().get(r1).get(r2);

						if (es != null && !(es instanceof AssrtCoreEBot)  // FIXME: hasMessage?
								&& ts.stream().allMatch(i -> es
										.equals(this.allStates.get(i).getQ().get(r1).get(r2))))
						{
							Set<Set<AssrtCoreSState>> set = eventualReception.get(es);
							if (set == null)
							{
								set = new HashSet<Set<AssrtCoreSState>>();
								eventualReception.put(es,  set);
							}
							set.add(ts.stream().map(i -> this.allStates.get(i))
									.collect(Collectors.toSet()));
						}
					}
				}
			}
		}
		
		return new AssrtCoreProgressErrors(roleProgress, eventualReception);
	}
	//*/
	
	// Revised "eventual reception" -- 1-bounded stable property with subject role side condition
	// FIXME: refactor as actual eventual reception -- though original one may be better for error feedback
	public Set<AssrtCoreSState> getStableErrors()
	{
		Set<AssrtCoreSState> res = new HashSet<>();
		for (AssrtCoreSState s : this.allStates.values())
		{
			if (!AssrtCoreSModel.isStable(s))
			{
				Set<AssrtCoreSState> seen = new HashSet<>();
				if (!canReachStable(seen, s))  // FIXME: subj role side condition for compatibility
				{
					res.add(s);
				}
			}
		}
		return res;
	}
	
	private static boolean isStable(AssrtCoreSState s)  // FIXME: refactor to F17SState
	{
		return s.getQ().values().stream().flatMap(m -> m.values().stream())
				.filter(v -> v != null && !(v instanceof AssrtCoreEBot)).count() == 0;
				// FIXME: connections
	}
	
	//private boolean canReachStable(Set<F17SState> seen, F17SState s, Role r)
	private boolean canReachStable(Set<AssrtCoreSState> seen, AssrtCoreSState s)
			// FIXME: subj role side condition for compatibility -- maybe integrate with this.reach -- currently no point to search this way, should just check stable on all reachable
	{
		if (AssrtCoreSModel.isStable(s))
		{
			return true;
		}
		else if (seen.contains(s))
		{
			return false;
		}
		/*for (F17SState succ : s.getAllSuccessors())
		{
			if (isStable(succ))
			{
				return true;
			}
		}*/
		for (AssrtCoreSState succ : s.getSuccs())
		{
			Set<AssrtCoreSState> tmp = new HashSet<>(seen);
			tmp.add(s);
			if (canReachStable(tmp, succ))
			{
				return true;
			}
		}
		return false;
	}
}

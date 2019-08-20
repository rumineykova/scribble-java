package org.scribble.ext.assrt.core.model.global;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.scribble.core.model.global.SModel;
import org.scribble.core.model.global.SState;
import org.scribble.core.type.name.GProtoName;
import org.scribble.ext.assrt.core.job.AssrtCore;
import org.scribble.ext.assrt.core.job.AssrtCoreArgs;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;

// 1-bounded LTS
// Factor out with SGraph/SModel?
public class AssrtCoreSModel extends SModel
{
	protected final AssrtCore core;
	
	protected AssrtCoreSModel(AssrtCore core, AssrtCoreSGraph graph)
	{
		super(graph);
		this.core = core;
	}
	
	//public AssrtCoreSafetyErrors getSafetyErrors(Job job, GProtoName simpname)
			// Maybe refactor simpname (root proto) into the (AssrtCore)Job
	@Override
	protected SortedMap<Integer, AssrtCoreSStateErrors> getSafetyErrors()  // s.id key lighter than full SConfig
	{
		boolean batch = ((AssrtCoreArgs) this.core.config.args).Z3_BATCH;
		boolean foo = false;
		
		if (batch)
		{
			GProtoName fullname = this.graph.proto;
			Collection<SState> all = this.graph.states.values();

			if (all.stream().allMatch(x -> ((AssrtCoreSConfig) x.config)
					.getUnknownDataVarErrors(core, fullname).isEmpty()))
			{
				// Check for all errors in a single pass -- any errors can be categorised later
				Set<AssrtBFormula> fs = new HashSet<>();
				fs.addAll(
						all.stream()
								.flatMap(s -> ((AssrtCoreSConfig) s.config)
										.getAssertProgressChecks(this.core, fullname)
										.stream())
								.collect(Collectors.toSet()));
				fs.addAll(
						all.stream()
								.flatMap(s -> ((AssrtCoreSConfig) s.config)
										.getAssertSatChecks(this.core, fullname).stream())
								.collect(Collectors.toSet()));
				fs.addAll(
						all.stream().flatMap(s -> ((AssrtCoreSConfig) s.config)
								.getRecAssertChecks(this.core, fullname,
										s.id == this.graph.init.id)
								.stream())
								.collect(Collectors.toSet()));
				/*String smt2 = fs.stream().filter(f -> !f.equals(AssrtTrueFormula.TRUE))
							.map(f -> "(assert " + f.toSmt2Formula() + ")\n").collect(Collectors.joining(""))
						+ "(check-sat)\n(exit)";
				if (Z3Wrapper.checkSat(smt2))*/  // FIXME: won't work for unint-funs without using Z3Wrapper.toSmt2

				foo = this.core.checkSat(fullname, fs);
				if (foo)
				{
					return new TreeMap<>();
				}
				else
				{
					System.out.println("bbbb: " + fs);
					System.exit(1);
				}
			}
		}

		SortedMap<Integer, AssrtCoreSStateErrors> res = new TreeMap<>();
		for (int id : this.graph.states.keySet())
		{
			//SStateErrors errs = this.graph.states.get(id).getErrors();  // TODO: getErrors needs core/fullname args
			AssrtCoreSStateErrors errs = new AssrtCoreSStateErrors(this.core,
					this.graph.proto, (AssrtCoreSState) this.graph.init,
					(AssrtCoreSState) this.graph.states.get(id));
			if (!errs.isEmpty())
			{
				res.put(id, errs);
			}
		}

		if (res.isEmpty() && batch && !foo)  // Testing
		{
			throw new RuntimeException("FIXME: ");
		}

		return res;
	}
}











	
	/*public boolean isActive(AssrtCoreSState s, Role r)
	{
		return AssrtCoreSConfig.isActive(s.getP().get(r), this.E0.get(r).id);
	}
	
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
//		for (F17SState succ : s.getAllSuccessors())
//		{
//			if (isStable(succ))
//			{
//				return true;
//			}
//		}
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
	*/

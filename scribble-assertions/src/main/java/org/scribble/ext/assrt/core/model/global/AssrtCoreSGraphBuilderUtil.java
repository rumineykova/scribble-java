package org.scribble.ext.assrt.core.model.global;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EFsm;
import org.scribble.core.model.endpoint.EGraph;
import org.scribble.core.model.global.SGraphBuilderUtil;
import org.scribble.core.model.global.SingleBuffers;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataVar;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;

public class AssrtCoreSGraphBuilderUtil extends SGraphBuilderUtil
{
	protected AssrtCoreSGraphBuilderUtil(ModelFactory mf)
	{
		super(mf);
	}
	
	// TODO: factor out of util, cf. SGraphBuilder.createInitConfig
	@Override
	protected AssrtCoreSConfig createInitConfig(Map<Role, EGraph> egraphs,
			boolean explicit)
	{
		Map<Role, EFsm> P = egraphs.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().toFsm()));
		SingleBuffers Q = new SingleBuffers(P.keySet(), !explicit);
		return ((AssrtCoreSModelFactory) this.mf.global).AssrtCoreSConfig(P, Q,
				makeR(P), makeRass(P), makeK(P.keySet()), makeF(P), P.keySet().stream()
						.collect(Collectors.toMap(r -> r, r -> new HashMap<>())));
	}

	// TODO: EFsm -> EGraph
	private static Map<Role, Map<AssrtDataVar, AssrtAFormula>> makeR(
			Map<Role, EFsm> P)
	{
		Map<Role, Map<AssrtDataVar, AssrtAFormula>> R = P.entrySet()
				.stream().collect(Collectors.toMap(Entry::getKey, e -> new HashMap<>(
						((AssrtEState) e.getValue().graph.init).getStateVars())));
		/*Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R = P.keySet().stream().collect(Collectors.toMap(r -> r, r ->
				Stream.of(false).collect(Collectors.toMap(
						x -> AssrtCoreESend.DUMMY_VAR,
						x -> AssrtCoreESend.ZERO))
			));*/
		return R;
	}
	
	private static Map<Role, Set<AssrtBFormula>> makeRass(Map<Role, EFsm> P)
	{
		return P.entrySet().stream().collect(Collectors.toMap(
				Entry::getKey,
				e ->
				{
					Set<AssrtBFormula> set = new HashSet<>();
						AssrtBFormula ass = ((AssrtEState) e.getValue().graph.init)
								.getAssertion();
						if (!ass.equals(AssrtTrueFormula.TRUE))
					{
						set.add(ass);
					}
					return set;
				}
		));
	}

	private static Map<Role, Set<AssrtDataVar>> makeK(Set<Role> rs)
	{
		return rs.stream().collect(Collectors.toMap(r -> r, r -> new HashSet<>()));
	}

	//private static Map<Role, Set<AssrtBoolFormula>> makeF(Set<Role> rs)
	private static Map<Role, Set<AssrtBFormula>> makeF(
			Map<Role, EFsm> P)
	{
		//return rs.stream().collect(Collectors.toMap(r -> r, r -> new HashSet<>()));
		return P.entrySet().stream().collect(Collectors.toMap(
				Entry::getKey,
				/*e -> e.getValue().getStateVars().entrySet().stream()
						.map(b -> AssrtFormulaFactory.AssrtBinComp(
								AssrtBinCompFormula.Op.Eq, 
								AssrtFormulaFactory.AssrtIntVar(b.getKey().toString()),
								b.getValue()))
						.collect(Collectors.toSet())*/
				e -> new HashSet<>()
		));
	}
}

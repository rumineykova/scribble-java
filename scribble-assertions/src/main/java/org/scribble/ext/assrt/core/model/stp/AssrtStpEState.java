package org.scribble.ext.assrt.core.model.stp;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAction;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEReceive;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.core.model.stp.action.AssrtStpEAction;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.type.name.RecVar;

public class AssrtStpEState extends AssrtEState
{
	public AssrtStpEState(Set<RecVar> labs)
	{
		super(labs, new LinkedHashMap<>(), AssrtTrueFormula.TRUE);  // State-vars and rec-assertions not supported
	}

	public static AssrtStpEState from(AssrtCoreEModelFactory ef, AssrtEState init)
	{
		AssrtStpEState tmp = ef.newAssertStpEState(init.getLabels());
		Map<Integer, AssrtEState> m1 = new HashMap<>();
		Map<Integer, AssrtStpEState> m2 = new HashMap<>();
		m1.put(tmp.id, init);
		m2.put(init.id, tmp);
		Set<AssrtStpEState> todo = Stream.of(tmp).collect(Collectors.toSet());  // Pre: not in seen
		Set<AssrtStpEState> seen = new HashSet<>();

		while (!todo.isEmpty())
		{
			Iterator<AssrtStpEState> i = todo.iterator();
			AssrtStpEState curr = i.next();
			i.remove();
			seen.add(curr);
			
			AssrtEState orig = m1.get(curr.id);
			for (EAction a : orig.getActions())
			{
				AssrtEState osucc = orig.getSuccessor(a);
				AssrtStpEState succ;
				if (m2.containsKey(osucc.id))
				{
					succ = m2.get(osucc.id);
				}
				else
				{
					succ = ef.newAssertStpEState(init.getLabels());
					m1.put(succ.id, osucc);
					m2.put(osucc.id, succ);
				}
				curr.addEdge((EAction) foobar(ef, (AssrtCoreEAction) a), succ);
				if (!todo.contains(succ) && !seen.contains(succ))
				{
					todo.add(succ);
				}
			}
		}
		return tmp;
	}
	
	private static AssrtStpEAction foobar(AssrtCoreEModelFactory ef, AssrtCoreEAction a)
	{
		if (a instanceof AssrtCoreESend)
		{
			AssrtCoreESend es = (AssrtCoreESend) a;
			AssrtBoolFormula A = a.getAssertion();
			Map<AssrtDataTypeVar, AssrtSmtFormula<?>> sigma = Collections.emptyMap();
			return ef.newAssrtStpEReceive(es.peer, es.mid, es.payload, sigma, A);
		}
		else if (a instanceof AssrtCoreESend)
		{
			AssrtCoreEReceive er = (AssrtCoreEReceive) a;
			AssrtBoolFormula A = a.getAssertion();
			Map<AssrtDataTypeVar, AssrtSmtFormula<?>> sigma = Collections.emptyMap();
			return ef.newAssrtStpESend(er.peer, er.mid, er.payload, sigma, A);
		}
		else
		{
			throw new RuntimeException("Shouldn't get in here: " + a);
		}
	}
}

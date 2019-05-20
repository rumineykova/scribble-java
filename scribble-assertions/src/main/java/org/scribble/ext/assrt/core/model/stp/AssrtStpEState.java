package org.scribble.ext.assrt.core.model.stp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.scribble.core.model.endpoint.actions.EAction;
import org.scribble.core.type.kind.PayElemKind;
import org.scribble.core.type.name.PayElemType;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAction;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreERecv;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.core.model.stp.action.AssrtStpEAction;
import org.scribble.ext.assrt.core.model.stp.action.AssrtStpEReceive;
import org.scribble.ext.assrt.core.model.stp.action.AssrtStpESend;
import org.scribble.ext.assrt.core.type.formula.AssrtBinBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBinCompFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.core.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.core.type.name.AssrtPayElemType;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;

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
		Set<AssrtStpEState> todo = Stream.of(tmp).collect(Collectors.toSet());  // Invar: not in seen
		Set<AssrtStpEState> seen = new HashSet<>();
		
		Map<Integer, List<AssrtDataTypeVar>> kv = new HashMap<>();  // FIXME: relies on WF disallowed merge -- and invar: not in seen

		while (!todo.isEmpty())
		{
			Iterator<AssrtStpEState> i = todo.iterator();
			AssrtStpEState curr = i.next();
			i.remove();
			seen.add(curr);
			
			AssrtEState orig = m1.get(curr.id);
			for (EAction a : orig.getActions())
			{
				AssrtEState osucc = orig.getDetSucc(a);
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
				curr.addEdge(
						(EAction) foobar(ef, kv.get(curr.id), (AssrtCoreEAction) a), succ);
				if (!todo.contains(succ) && !seen.contains(succ))
				{
					todo.add(succ);
				}

				List<PayElemType<? extends PayElemKind>> elems = a.payload.elems;
				for (PayElemType<?> pet : elems)
				{
					if (pet instanceof AssrtPayElemType<?>)
					{
						AssrtPayElemType<?> apet = (AssrtPayElemType<?>) pet;
						if (apet.isAnnotVarDecl())
						{
							AssrtAnnotDataType adt = (AssrtAnnotDataType) apet;
							if (!adt.var.toString().startsWith("__dum"))
							{
								List<AssrtDataTypeVar> vs = kv.get(succ.id);
								if (vs == null)
								{
									vs = new LinkedList<>();
									kv.put(succ.id, vs);
								}
								vs.add(adt.var);
							}
						}
						else
						{
							throw new RuntimeException("[assrt-core] TODO: " + pet);
						}
					}
				}
			}
		}
		return tmp;
	}
	
	private static AssrtStpEAction foobar(AssrtCoreEModelFactory ef,
			List<AssrtDataTypeVar> vs, AssrtCoreEAction a)
	{
		if (a instanceof AssrtCoreESend)
		{
			AssrtCoreESend es = (AssrtCoreESend) a;
			return (AssrtStpESend) barfoo(ef, vs, (EAction) es);
		}
		else if (a instanceof AssrtCoreERecv)
		{
			AssrtCoreERecv er = (AssrtCoreERecv) a;
			return (AssrtStpEReceive) barfoo(ef, vs, (EAction) er);
		}
		else
		{
			throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);
		}
	}

	private static AssrtStpEAction barfoo(AssrtCoreEModelFactory ef,
			List<AssrtDataTypeVar> vs, EAction ea)
	{
			AssrtBFormula A = ((AssrtCoreEAction) ea).getAssertion();
			Map<AssrtIntVarFormula, AssrtSmtFormula<?>> sigma = new HashMap<>();
			
			/*... add known vars to AssrtStpEState (but don't use in hash) -- or just collect them alongside the build-traversal
					-- WF property: if x is *used* at any state, it must be *necessarily* known at that state
			...  // fill sigma, update A*/
					
			A = A.getCnf();
			List<AssrtBFormula> cs = AssrtBinBFormula.getCnfClauses(A);
			
			List<PayElemType<?>> tmp = new LinkedList<>();
			for (PayElemType<?> pet : ea.payload.elems)
			{
				boolean constructive = false;
				if (pet instanceof AssrtPayElemType<?>)
				{
					// AssrtPayloadElemType<?> apet = (AssrtPayloadElemType<?>) pet;

					for (AssrtBFormula c : cs)
					{
						if (!(c instanceof AssrtBinCompFormula))
						{
							continue;
						}
						AssrtBinCompFormula f = (AssrtBinCompFormula) c;
						System.out.println("aaa: " + f);
						if (f.op == AssrtBinCompFormula.Op.Eq)
						{
							if (vs.stream().anyMatch(v -> v.toString().equals(f.right.toString())))
							{
								sigma.put((AssrtIntVarFormula) f.left, f.right);
								constructive = true;
								cs.remove(c);
								break;
							}
							else if (vs.stream().anyMatch(v -> v.toString().equals(f.left.toString())))
							{
								sigma.put((AssrtIntVarFormula) f.right, f.left);
								constructive = true;
								cs.remove(c);
								break;
							}
						}
					}
				}

				if (constructive)
				{
					if (cs.isEmpty())
					{
						A = AssrtTrueFormula.TRUE;
					}
					else
					{
					A = cs.stream().reduce((f1, f2) -> AssrtFormulaFactory
							.AssrtBinBool(AssrtBinBFormula.Op.And, f1, f2)).get();
					}
				}
				else
				{
					tmp.add(pet);
				}
			}
			if (ea instanceof AssrtCoreESend)
			{
				return ef.newAssrtStpESend(ea.peer, ea.mid, new Payload(tmp), sigma, A);
			}
			else
			{
				return ef.newAssrtStpEReceive(ea.peer, ea.mid, new Payload(tmp), sigma, A);
			}
	}
}

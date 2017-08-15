package org.scribble.ext.assrt.core.model.endpoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scribble.ext.assrt.core.ast.AssrtCoreAction;
import org.scribble.ext.assrt.core.ast.AssrtCoreRecVar;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLActionKind;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLChoice;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLRec;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.ext.assrt.main.AssrtJob;
import org.scribble.ext.assrt.model.endpoint.AssrtEGraphBuilderUtil;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.model.endpoint.EGraph;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.type.Payload;
import org.scribble.type.name.RecVar;
import org.scribble.type.name.Role;

public class AssrtCoreEGraphBuilder
{
	private final AssrtJob job;
	private final AssrtEGraphBuilderUtil util;
	
	public AssrtCoreEGraphBuilder(AssrtJob job)
	{
		this.job = job;
		this.util = (AssrtEGraphBuilderUtil) job.newEGraphBuilderUtil();
	}
	
	public EGraph build(AssrtCoreLType lt)
	{
		this.util.init(((AssrtCoreEModelFactory) this.job.ef).newAssrtEState(Collections.emptySet(), new HashMap<>(), 
				AssrtTrueFormula.TRUE));
		build(lt, this.util.getEntry(), this.util.getExit(), new HashMap<>());
		return this.util.finalise();
	}
	
	private void build(AssrtCoreLType lt, AssrtEState s1, AssrtEState s2, Map<RecVar, AssrtEState> recs)
	{
		if (lt instanceof AssrtCoreLChoice)
		{
			AssrtCoreLChoice lc = (AssrtCoreLChoice) lt;
			AssrtCoreLActionKind k = lc.getKind();
			lc.cases.entrySet().stream().forEach(e ->
				buildEdgeAndContinuation(s1, s2, recs, lc.role, k, e.getKey(), e.getValue())
			);
		}
		else if (lt instanceof AssrtCoreLRec)
		{
			AssrtCoreLRec lr = (AssrtCoreLRec) lt;

			//this.util.addAnnotVarInits(s1, Stream.of(lr.annot).collect(Collectors.toMap(a -> a, a -> lr.init)));
			this.util.addStateVars(s1, lr.annotvars,
					lr.ass);

			Map<RecVar, AssrtEState> tmp = new HashMap<>(recs);
			tmp.put(lr.recvar, s1);
			build(lr.body, s1, s2, tmp);
		}
		else
		{
			throw new RuntimeException("[assrt-core] Shouldn't get in here: " + lt);
		}
	}

	private void buildEdgeAndContinuation(AssrtEState s1, AssrtEState s2, Map<RecVar, AssrtEState> recs, 
			Role r, AssrtCoreLActionKind k, AssrtCoreAction a, AssrtCoreLType cont)
	{
		if (cont instanceof AssrtCoreLEnd)
		{
			this.util.addEdge(s1, toEAction(r, k, a), s2);
		}
		else if (cont instanceof AssrtCoreRecVar)
		{
			AssrtCoreRecVar crv = (AssrtCoreRecVar) cont;
			AssrtEState s = recs.get(((AssrtCoreRecVar) cont).recvar);

			//AssrtArithFormula expr = crv.annotexprs;
			//AssrtDataTypeVar annot = s.getAnnotVars().keySet().iterator().next();
			List<AssrtArithFormula> annotexprs = crv.annotexprs;
			//List<AssrtDataTypeVar> annotvars = s.getAnnotVars().keySet().stream().collect(Collectors.toList());

			this.util.addEdge(s1, toEAction(r, k, a, //annotvars,
					annotexprs), s);
		}
		else
		{
			AssrtEState s = (AssrtEState) ((AssrtCoreEModelFactory) this.util.ef).newEState(Collections.emptySet());  
					// FIXME: call Assrt directly? -- no "vars" here though (intermediate sequence states), only on rec states

			this.util.addEdge(s1, toEAction(r, k, a), s);
			build(cont, s, s2, recs);
		}
	}
	
	private EAction toEAction(Role r, AssrtCoreLActionKind k, AssrtCoreAction a)
	{
		//return toEAction(r, k, a, AssrtCoreESend.DUMMY_VAR, AssrtCoreESend.ZERO);
		return toEAction(r, k, a, Collections.emptyList());
	}

	private EAction toEAction(Role r, AssrtCoreLActionKind k, AssrtCoreAction a,
			//AssrtDataTypeVar annot, AssrtArithFormula expr)
			List<AssrtArithFormula> annotexprs)
	{
		AssrtCoreEModelFactory ef = (AssrtCoreEModelFactory) this.util.ef;  // FIXME: factor out
		if (k.equals(AssrtCoreLActionKind.SEND))
		{
			return ef.newAssrtCoreESend(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass, //annot,
					annotexprs);

		}
		else if (k.equals(AssrtCoreLActionKind.RECEIVE))
		{
			//return ef.newAssrtEReceive(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass);
			return ef.newAssrtCoreEReceive(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass, //annot,
					annotexprs);

			// FIXME: local receive assertions -- why needed exactly?  should WF imply receive assertion always true?

		}
		else if (k.equals(AssrtCoreLActionKind.REQUEST))
		{
			return ef.newAssrtCoreERequest(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass, //annot,
					annotexprs);
		}
		else if (k.equals(AssrtCoreLActionKind.ACCEPT))
		{
			return ef.newAssrtCoreEAccept(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass, //annot,
					annotexprs);
		}
		/*else if (a instanceof AssrtCoreLDisconnect)
		{
			return this.util.ef.newEDisconnect(a.peer);
		}*/
		else
		{
			throw new RuntimeException("[assrt-core] Shouldn't get in here: " + k);
		}
	}
}

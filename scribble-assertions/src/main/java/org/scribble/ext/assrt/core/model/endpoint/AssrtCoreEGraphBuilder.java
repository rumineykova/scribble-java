package org.scribble.ext.assrt.core.model.endpoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.scribble.core.model.endpoint.EGraph;
import org.scribble.core.model.endpoint.actions.EAction;
import org.scribble.core.type.name.PayElemType;
import org.scribble.core.type.name.RecVar;
import org.scribble.core.type.name.Role;
import org.scribble.core.type.session.Payload;
import org.scribble.ext.assrt.core.job.AssrtCore;
import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.core.type.kind.AssrtAnnotDataTypeKind;
import org.scribble.ext.assrt.core.type.session.AssrtCoreMsg;
import org.scribble.ext.assrt.core.type.session.AssrtCoreRecVar;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLActionKind;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLChoice;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLRec;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLRecVar;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLType;
import org.scribble.ext.assrt.main.AssrtJob;
import org.scribble.ext.assrt.model.endpoint.AssrtEGraphBuilderUtil;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;

public class AssrtCoreEGraphBuilder
{
	private final AssrtCore core;
	private final AssrtEGraphBuilderUtil util;  // Not using any features for unguarded choice/recursion/continue (recursion manually tracked here)

	
	public AssrtCoreEGraphBuilder(AssrtJob job)
	{
		this.core = (AssrtCore) job.getCore();
		this.util = (AssrtEGraphBuilderUtil) this.core.config.mf.local
				.EGraphBuilderUtil();
	}
	
	public EGraph build(AssrtCoreLType lt)
	{
		this.util.setEntry(((AssrtCoreEModelFactory) this.core.config.mf.local)
				.newAssrtEState(Collections.emptySet(), new LinkedHashMap<>(),
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
			Role r, AssrtCoreLActionKind k, AssrtCoreMsg a, AssrtCoreLType cont)
	{
		if (cont instanceof AssrtCoreLEnd)
		{
			this.util.addEdge(s1, toEAction(r, k, a), s2);
		}
		else if (cont instanceof AssrtCoreRecVar)
		{
			AssrtCoreLRecVar crv = (AssrtCoreLRecVar) cont;
			AssrtEState s = recs.get(crv.recvar);

			//AssrtArithFormula expr = crv.annotexprs;
			//AssrtDataTypeVar annot = s.getAnnotVars().keySet().iterator().next();
			List<AssrtArithFormula> annotexprs = crv.annotexprs;
			//List<AssrtDataTypeVar> annotvars = s.getAnnotVars().keySet().stream().collect(Collectors.toList());

			this.util.addEdge(s1, toEAction(r, k, a, //annotvars,
					annotexprs), s);
		}
		else
		{
			AssrtEState s = (AssrtEState) ((AssrtCoreEModelFactory) this.util.mf)
					.EState(Collections.emptySet());
					// FIXME: call Assrt directly? -- no "vars" here though (intermediate sequence states), only on rec states

			this.util.addEdge(s1, toEAction(r, k, a), s);
			build(cont, s, s2, recs);
		}
	}
	
	private EAction toEAction(Role r, AssrtCoreLActionKind k, AssrtCoreMsg a)
	{
		//return toEAction(r, k, a, AssrtCoreESend.DUMMY_VAR, AssrtCoreESend.ZERO);
		return toEAction(r, k, a, Collections.emptyList());
	}

	private EAction toEAction(Role r, AssrtCoreLActionKind k, AssrtCoreMsg a,
			//AssrtDataTypeVar annot, AssrtArithFormula expr)
			List<AssrtArithFormula> annotexprs)
	{
		AssrtCoreEModelFactory ef = (AssrtCoreEModelFactory) this.util.mf;  // FIXME: factor out
		if (k.equals(AssrtCoreLActionKind.SEND))
		{
			return ef.newAssrtCoreESend(r, a.op, 
					//new Payload(Arrays.asList(a.pays)),
					new Payload(a.pay.stream().map(p -> (PayElemType<AssrtAnnotDataTypeKind>) p).collect(Collectors.toList())),
					a.ass, //annot,
					annotexprs);

		}
		else if (k.equals(AssrtCoreLActionKind.RECV))
		{
			//return ef.newAssrtEReceive(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass);
			return ef.newAssrtCoreEReceive(r, a.op,
					//new Payload(Arrays.asList(a.pays)),
					new Payload(a.pay.stream().map(p -> (PayElemType<AssrtAnnotDataTypeKind>) p).collect(Collectors.toList())),
					a.ass, //annot,
					annotexprs);

			// FIXME: local receive assertions -- why needed exactly?  should WF imply receive assertion always true?

		}
		else if (k.equals(AssrtCoreLActionKind.REQ))
		{
			return ef.newAssrtCoreERequest(r, a.op,
					//new Payload(Arrays.asList(a.pays)),
					new Payload(a.pay.stream().map(p -> (PayElemType<AssrtAnnotDataTypeKind>) p).collect(Collectors.toList())),
					a.ass, //annot,
					annotexprs);
		}
		else if (k.equals(AssrtCoreLActionKind.ACC))
		{
			return ef.newAssrtCoreEAccept(r, a.op,
					//new Payload(Arrays.asList(a.pays)),
					new Payload(a.pay.stream().map(p -> (PayElemType<AssrtAnnotDataTypeKind>) p).collect(Collectors.toList())),
					a.ass, //annot,
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

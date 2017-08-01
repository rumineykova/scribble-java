package org.scribble.ext.assrt.core.model.endpoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.scribble.ext.assrt.core.ast.AssrtCoreAction;
import org.scribble.ext.assrt.core.ast.AssrtCoreRecVar;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLActionKind;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLChoice;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLRec;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.main.AssrtJob;
import org.scribble.ext.assrt.model.endpoint.AssrtEGraphBuilderUtil;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
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
		this.util.init(((AssrtCoreEModelFactory) this.job.ef).newAssrtEState(Collections.emptySet(), new HashMap<>()));
		build(lt, this.util.getEntry(), this.util.getExit(), new HashMap<>());
		return this.util.finalise();
	}
	
	private void build(AssrtCoreLType lt, AssrtEState s1, AssrtEState s2, Map<RecVar, AssrtEState> f)
	{
		if (lt instanceof AssrtCoreLChoice)
		{
			AssrtCoreLChoice lc = (AssrtCoreLChoice) lt;
			AssrtCoreLActionKind k = lc.getKind();
			lc.cases.entrySet().stream().forEach(e ->
				buildEdgeAndContinuation(s1, s2, f, lc.role, k, e.getKey(), e.getValue())
			);
		}
		else if (lt instanceof AssrtCoreLRec)
		{
			AssrtCoreLRec lr = (AssrtCoreLRec) lt;
			this.util.addAnnotVarInits(Stream.of(lr.annot).collect(Collectors.toMap(a -> a, a -> lr.init)));
			build(lr.body, s1, s2, Stream.of(lr.recvar).collect(Collectors.toMap(v -> v, v -> s1)));
		}
		else
		{
			throw new RuntimeException("[assrt-core] Shouldn't get in here: " + lt);
		}
	}

	private void buildEdgeAndContinuation(AssrtEState s1, AssrtEState s2, Map<RecVar, AssrtEState> f, 
			Role r, AssrtCoreLActionKind k, AssrtCoreAction a, AssrtCoreLType cont)
	{
		if (cont instanceof AssrtCoreLEnd)
		{
			this.util.addEdge(s1, toEAction(r, k, a), s2);
		}
		else if (cont instanceof AssrtCoreRecVar)
		{
			AssrtCoreRecVar crv = (AssrtCoreRecVar) cont;
			AssrtArithFormula expr = crv.expr;
			
			
			AssrtDataTypeVar annot = new AssrtDataTypeVar("x");  // HERE HACK FIXME

			
			this.util.addEdge(s1, toEAction(r, k, a, annot, expr), f.get(((AssrtCoreRecVar) cont).var));
		}
		else
		{
			AssrtEState s = (AssrtEState) ((AssrtCoreEModelFactory) this.util.ef).newEState(Collections.emptySet());  
					// FIXME: call Assrt directly? -- no "vars" here though (intermediate sequence states), only on rec states

			this.util.addEdge(s1, toEAction(r, k, a), s);
			build(cont, s, s2, f);
		}
	}
	
	private EAction toEAction(Role r, AssrtCoreLActionKind k, AssrtCoreAction a)
	{
		return toEAction(r, k, a, AssrtCoreESend.DUMMY_VAR, AssrtCoreESend.ZERO);
	}

	private EAction toEAction(Role r, AssrtCoreLActionKind k, AssrtCoreAction a, AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		AssrtCoreEModelFactory ef = (AssrtCoreEModelFactory) this.util.ef;  // FIXME: factor out
		if (k.equals(AssrtCoreLActionKind.SEND))
		{
			return ef.newAssrtCoreESend(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass, annot, expr);
		}
		else if (k.equals(AssrtCoreLActionKind.RECEIVE))
		{
			//return ef.newAssrtEReceive(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass);
			return ef.newAssrtCoreEReceive(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass, annot, expr);

			// FIXME: local receive assertions -- why needed exactly?  should WF imply receive assertion always true?

		}
		else if (k.equals(AssrtCoreLActionKind.REQUEST))
		{
			if (!annot.equals(AssrtCoreESend.DUMMY_VAR) && !expr.equals(AssrtCoreESend.ZERO))  // FIXME: annot + expr
			{
				throw new RuntimeException("[assrt-core] TODO: " + a);
			}

			return ef.newAssrtERequest(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass);
		}
		else if (k.equals(AssrtCoreLActionKind.ACCEPT))
		{
			if (!annot.equals(AssrtCoreESend.DUMMY_VAR) && !expr.equals(AssrtCoreESend.ZERO))  // FIXME: annot + expr
			{
				throw new RuntimeException("[assrt-core] TODO: " + a);
			}

			return ef.newAssrtEAccept(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass);
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

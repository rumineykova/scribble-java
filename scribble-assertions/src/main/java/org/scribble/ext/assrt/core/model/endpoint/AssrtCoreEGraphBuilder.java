package org.scribble.ext.assrt.core.model.endpoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.scribble.ext.assrt.core.ast.AssrtCoreAction;
import org.scribble.ext.assrt.core.ast.AssrtCoreRecVar;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLActionKind;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLChoice;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLEnd;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLRec;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.ext.assrt.model.endpoint.AssrtEModelFactory;
import org.scribble.model.endpoint.EGraph;
import org.scribble.model.endpoint.EGraphBuilderUtil;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.RecVar;
import org.scribble.sesstype.name.Role;

public class AssrtCoreEGraphBuilder
{
	private final EGraphBuilderUtil util;
	
	public AssrtCoreEGraphBuilder(EModelFactory ef)
	{
		this.util = new EGraphBuilderUtil(ef);
	}
	
	public EGraph build(AssrtCoreLType lt)
	{
		this.util.reset();
		build(lt, this.util.getEntry(), this.util.getExit(), new HashMap<>());
		return this.util.finalise();
	}
	
	private void build(AssrtCoreLType lt, EState s1, EState s2, Map<RecVar, EState> f)
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
			Map<RecVar, EState> tmp = new HashMap<>(f);
			tmp.put(lr.recvar, s1);
			build(lr.body, s1, s2, tmp);
		}
		else
		{
			throw new RuntimeException("[assrt-core] Shouldn't get in here: " + lt);
		}
	}

	private void buildEdgeAndContinuation(EState s1, EState s2, Map<RecVar, EState> f, 
			Role r, AssrtCoreLActionKind k, AssrtCoreAction a, AssrtCoreLType cont)
	{
		if (cont instanceof AssrtCoreLEnd)
		{
			this.util.addEdge(s1, toEAction(r, k, a), s2);
		}
		else if (cont instanceof AssrtCoreRecVar)
		{
			this.util.addEdge(s1, toEAction(r, k, a), f.get(((AssrtCoreRecVar) cont).var));
		}
		else
		{
			EState s = this.util.ef.newEState(Collections.emptySet());
			this.util.addEdge(s1, toEAction(r, k, a), s);
			build(cont, s, s2, f);
		}
	}
	
	private EAction toEAction(Role r, AssrtCoreLActionKind k, AssrtCoreAction a)
	{
		AssrtEModelFactory ef = (AssrtEModelFactory) this.util.ef;  // FIXME: factor out
		if (k.equals(AssrtCoreLActionKind.SEND))
		{
			//AssrtCoreLSend ls = (AssrtCoreLSend) a;
			//return this.util.ef.newESend(r, a.op, new Payload(Arrays.asList(a.pay)));  // FIXME: assertion model actions
			return ef.newAssrtESend(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass);
		}
		else if (k.equals(AssrtCoreLActionKind.RECEIVE))
		{
			//AssrtCoreLReceive lr = (AssrtCoreLReceive) a;
			return ef.newAssrtEReceive(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass);

			// FIXME: local receive assertions -- why needed exactly?  should WF imply receive assertion always true?

		}
		else if (k.equals(AssrtCoreLActionKind.REQUEST))
		{
			return ef.newAssrtERequest(r, a.op, new Payload(Arrays.asList(a.pay)), a.ass);
		}
		else if (k.equals(AssrtCoreLActionKind.ACCEPT))
		{
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

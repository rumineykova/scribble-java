package org.scribble.ext.assrt.core.model.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EFsm;
import org.scribble.core.model.endpoint.EState;
import org.scribble.core.model.endpoint.EStateKind;
import org.scribble.core.model.endpoint.actions.EAction;
import org.scribble.core.model.endpoint.actions.EDisconnect;
import org.scribble.core.model.endpoint.actions.EServerWrap;
import org.scribble.core.model.global.SConfig;
import org.scribble.core.model.global.SSingleBuffers;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.PayElemType;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.job.AssrtCore;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactory;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAcc;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAction;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreERecv;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEReq;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBinBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBinCompFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtUnintPredicateFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataName;
import org.scribble.ext.assrt.core.type.name.AssrtIntVar;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;
import org.scribble.ext.assrt.util.Z3Wrapper;
import org.sosy_lab.java_smt.api.Formula;

			
public class AssrtCoreSConfig extends SConfig  // TODO: not AssrtSConfig
{
	private static int counter = 1;
	
	// CHECKME: fields used for hash/equals -- cf. SState.config

	// N.B. Shadowing supers for convenience (but at least final and immutable)
	private final Map<Role, EFsm> P;          
	private final SSingleBuffers Q;  // null value means connected and empty -- dest -> src -> msg

	public final Map<Role, Set<AssrtIntVar>> K;  // Conflict between having this in the state, and formula building?
	public final Map<Role, Set<AssrtBFormula>> F;  // N.B. because F not in equals/hash, "final" receive in a recursion doesn't get built -- cf., unsat check only for send actions
	public final Map<Role, Map<AssrtIntVar, AssrtAFormula>> V;  
	public final Map<Role, Set<AssrtBFormula>> R;  // F is history for action ass's; R is history for rec ass's ?
	//private final Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename; // combine with K?  // CHECKME: unused?
	
	// *Past* scopes (in the sense of "preceding/outer scope"), so does not include "current" scope -- important to consider for "self-recursions"
	// N.B. not included in equals/hashCode -- used to constrain K/F/etc to syntactic scope to determine the state, but not part of the state itself 
	// (Probably more suitable for the graph builder to manage, but current async/sync methods inconvenient)
	// Reflects lexical scoping -- relies on syntactic WF for var annots
	protected final Map<Role, LinkedHashMap<Integer, Set<AssrtIntVar>>> scopes;  
			// role -> *EFsm* state id -> past "scope", i.e., known vars up to, but excluding, that state

	// Pre: non-aliased "ownership" of all Map contents
	protected AssrtCoreSConfig(ModelFactory mf, Map<Role, EFsm> P,
			SSingleBuffers Q, Map<Role, Set<AssrtIntVar>> K,
			Map<Role, Set<AssrtBFormula>> F,
			Map<Role, Map<AssrtIntVar, AssrtAFormula>> V,
			Map<Role, Set<AssrtBFormula>> R,
			//Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename
			Map<Role, LinkedHashMap<Integer, Set<AssrtIntVar>>> scopes)
	{
		super(mf, P, Q);
		this.P = this.efsms;  // Already unmodifiable'd (as necessary) by super
		this.Q = this.queues;
		this.K = Collections.unmodifiableMap(K);
		this.F = Collections.unmodifiableMap(F);
		this.V = Collections.unmodifiableMap(V);
		this.R = Collections.unmodifiableMap(R);
		//this.rename = Collections.unmodifiableMap(rename);
		this.scopes = Collections.unmodifiableMap(scopes);
	}

	/*// CHECKME: List<AssrtCoreEAction> -- after also doing assert-core request/accept
	@Override
	public Map<Role, Set<EAction>> getFireable()
	{
		Map<Role, Set<EAction>> res = new HashMap<>();
		for (Entry<Role, EFsm> e : this.P.entrySet())
		{
			Role self = e.getKey();
			EState s = e.getValue().curr;
			res.put(self, new LinkedHashSet<>());
			for (EAction a : s.getDetActions())
			{
				if (a.isSend())
				{
					AssrtCoreESend es = (AssrtCoreESend) a;
					getSendFireable(res, self, es);
				}
				else if (a.isReceive())
				{
					AssrtCoreERecv er = (AssrtCoreERecv) a;
					getReceiveFireable(res, self, er);
				}
				else if (a.isRequest())
				{
					AssrtCoreEReq ec = (AssrtCoreEReq) a;  // FIXME: core
					getRequestFireable(res, self, ec);
				}
				else if (a.isAccept())
				{
					AssrtCoreEAcc ea = (AssrtCoreEAcc) a;  // FIXME: core
					getAcceptFireable(res, self, ea);
				}
				else if (a.isDisconnect())
				{
					EDisconnect ld = (EDisconnect) a;
					getDisconnectFireable(res, self, ld);
				}
				else
				{
					throw new RuntimeException("[assrt-core] [TODO]: " + a);
				}
			}
		}
		return res;
	}*/

	/*@Override
	protected Set<EAction> getOutputFireable(Role self, EFsm fsm)
	//private void getSendFireable(Map<Role, List<EAction>> res, Role self, AssrtCoreESend es)
	{
		if (hasPendingRequest(self) || !isInputQueueEstablished(self, es.peer)
				|| hasMsg(es.peer, self))
		{
			return;
		}

		// Check assertion?
		//boolean ok = JavaSmtWrapper.getInstance().isSat(es.assertion.getFormula(), context);
	
		//...can only send if true? (by API gen assertion failure means definitely not sending it) -- unsat as bad terminal state (safety error)?  no: won't catch all assert errors (branches)
		// check assertion satisfiable?  i.e., satisfiability part of operational semantics for model construction? or just record constraints and check later?
		// -- current assertions *imply* additional ones?
				
		// Or: assertion error as special queue token? for error preservation -- Cf. "decoupled" request/accept
		
		boolean ok = true;
//		for (PayloadElemType<?> pt : (Iterable<PayloadElemType<?>>) 
//				es.payload.elems.stream().filter(x -> x instanceof AssrtPayloadElemType<?>)::iterator)
		for (PayElemType<?> pt : es.payload.elems)  // assrt-core is hardcoded to one payload elem (empty source payload is filled in)
		{
			if (pt instanceof AssrtAnnotDataName)
			{
				// OK -- currently not checking K-bound assertion vars (cf. isUnknownVarError) -- nor satisfiability (send ass implies receive ss)
				// FIXME: currently fire requires send and receive assertions (and both hacked to True) to be syntactically equal, which is wrong
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + pt);  // "Encode" pay elem vars by fresh annot data elems for now
			}
		}
		if (ok)
		{	
			res.get(self).add(es);
		}
	}*/

	/*@Override
	protected Set<ERecv> getRecvFireable(Role self, EFsm fsm)
	//private void getReceiveFireable(Map<Role, List<EAction>> res, Role self, ERecv er)
	{
		if (hasPendingRequest(self) || !hasMsg(self, er.peer))
		{
			return;
		}

		AssrtCoreESend m = this.Q.get(self).get(er.peer);
		//if (er.toDual(self).equals(m))  //&& !(m instanceof F17EBot)
		if (((AssrtCoreESend) er.toDual(self)).toTrueAssertion()
				.equals(m.toTrueAssertion()))
				// HACK FIXME: check assertion implication (not just syntactic equals) -- cf. AssrtSConfig::fire
		{
			res.get(self).add(er);
		}
	}*/

	/*private void getRequestFireable(Map<Role, List<EAction>> res, Role self,
			AssrtCoreEReq es)
	{
		if (hasPendingRequest(self) ||
				   // not ( Q(r, r') = Q(r', r) = \bot ) -- i.e., either of them are not \bot
				   isInputQueueEstablished(self, es.peer) || isInputQueueEstablished(es.peer, self)
				|| isPendingRequest(es.peer, self))  // self input queue from es.peer is <a>
						// isPendingConnection(self, es.peer) subsumed by hasPendingConnect(self)
		{
			return;
		}
		
		// FIXME: factor out with send?
		boolean ok = true;
		for (PayElemType<?> pt : es.payload.elems)  // assrt-core is hardcoded to one payload elem (empty source payload is filled in)
		{
			if (pt instanceof AssrtAnnotDataName)
			{
				// OK -- currently not checking K-bound assertion vars (cf. isUnknownVarError) -- nor satisfiability (send ass implies receive ss)
				// FIXME: currently fire requires send and receive assertions (and both hacked to True) to be syntactically equal, which is wrong
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + pt);  // "Encode" pay elem vars by fresh annot data elems for now
			}
		}
		if (ok)
		{	
			res.get(self).add(es);
		}
	}*/

	/*// Based on getReceiveFireable
	@Override
	protected Set<EAcc> getAccFireable(Role self, EFsm fsm)
	//private void getAcceptFireable(Map<Role, List<EAction>> res, Role self, AssrtCoreEAcc ea)
	{
		if (hasPendingRequest(self) || !isPendingRequest(ea.peer, self))
		{
			return;
		}

		AssrtCoreEReq ec = ((AssrtCoreEPendingRequest) this.Q.get(self).get(ea.peer)).getMsg();
		//if (ea.toDual(self).equals(ec))
		if (((AssrtCoreEReq) ea.toDual(self)).toTrueAssertion().equals(ec.toTrueAssertion()))  
				// HACK FIXME: check assertion implication (not just syntactic equals) -- cf. getReceiveFireable
		{
			res.get(self).add(ea);
		}
	}*/

	/*private void getDisconnectFireable(Map<Role, List<EAction>> res, Role self, EDisconnect ld)
	{
		if (!(this.Q.get(self).get(ld.peer) instanceof F17EBot)  // FIXME: isConnected
				&& this.Q.get(self).get(ld.peer) == null)
		{
			res.get(self).add(ld);
		}
	}*/

	@Override
	protected Set<EServerWrap> getSWrapFireable(Role self, EFsm fsm)
	{
		throw new RuntimeException("[TODO] : " + fsm + "@" + self);
	}
	
	// Pre: getFireable().get(self).contains(a)
  // Deterministic
	@Override
	public List<SConfig> async(Role self, EAction a)
	//public AssrtCoreSConfig fire(Role self, EAction a)
	{
		List<SConfig> res = new LinkedList<>();
		List<EFsm> succs = this.efsms.get(self).getSuccs(a);
		if (succs.size() > 1)
		{
			throw new RuntimeException(
					"[assrt-core][TODO] Non-deteterministic actions not supported: " + succs);
		}
		for (EFsm succ : succs)
		{
			Map<Role, EFsm> efsms = new HashMap<>(this.efsms);
			efsms.put(self, succ);
			AssrtCoreSConfig next =  // N.B. queue updates are insensitive to non-det "a"
				  a.isSend()       ? fireSend(self, (AssrtCoreESend) a, succ) //this.queues.send(self, (ESend) a)
				: a.isReceive()    ? fireRecv(self, (AssrtCoreERecv) a, succ) //this.queues.receive(self, (ERecv) a)
				//: a.isDisconnect() ? this.queues.disconnect(self, (EDisconnect) a)
				: null;
			if (next == null)
			{
				throw new RuntimeException("Shouldn't get in here: " + a);
			}
			res.add(next);
		}
		return res;
		//R.get(self).putAll(succ.getAnnotVars());  // Should "overwrite" previous var values -- no, do later (and from action info, not state)
	}
	
	@Override
	public List<SConfig> sync(Role r1, EAction a1, Role r2, EAction a2)
	{
		throw new RuntimeException(
				"[TODO] :\n\t" + r1 + " ,, " + a1 + "\n\t" + r2 + " ,, " + a2);
		/*List<SConfig> res = new LinkedList<>();
		List<EFsm> succs1 = this.efsms.get(r1).getSuccs(a1);
		List<EFsm> succs2 = this.efsms.get(r2).getSuccs(a2);
		if (succs1.size() > 1 || succs2.size() > 1)
		{
			throw new RuntimeException(
					"[assrt-core][TODO] Non-deteterministic actions not supported: "
							+ succs1 + " ,, " + succs2);
		}
		for (EFsm succ1 : succs1)
		{
			for (EFsm succ2 : succs2)
			{
				Map<Role, EFsm> efsms = new HashMap<>(this.efsms);
				// a1 and a2 are a "sync" pair, add all combinations of succ1 and succ2 that may arise
				efsms.put(r1, succ1);  // Overwrite existing r1/r2 entries
				efsms.put(r2, succ2);
				SingleBuffers queues;
				// a1 and a2 definitely "sync", now just determine whether it is a connect or wrap
				if (((a1.isRequest() && a2.isAccept())
						|| (a1.isAccept() && a2.isRequest())))
				{
					queues = this.queues.connect(r1, r2);  // N.B. queue updates are insensitive to non-det "a"
				}
				else if (((a1.isClientWrap() && a2.isServerWrap())
						|| (a1.isServerWrap() && a2.isClientWrap())))
				{
					// Doesn't affect queue state
					queues = this.queues;  // OK, immutable?
				}
				else
				{
					throw new RuntimeException("Shouldn't get in here: " + a1 + ", " + a2);
				}
				res.add(this.mf.global.SConfig(efsms, queues));
			}
		}
		return res;*/
	}

	// Update (in place) P, Q, K, F and R
	private AssrtCoreSConfig fireSend(Role self, AssrtCoreESend a, EFsm succ)
	{
		Map<Role, EFsm> P = new HashMap<>(this.P);
		Map<Role, Set<AssrtIntVar>> K = copyK(this.K);
		Map<Role, Set<AssrtBFormula>> F = copyF(this.F);
		Map<Role, Map<AssrtIntVar, AssrtAFormula>> V = copyR(this.V);
		//R.get(self).putAll(succ.getAnnotVars());  // Should "overwrite" previous var values -- no, do later (and from action info, not state)
		Map<Role, Set<AssrtBFormula>> R = copyRass(this.R);
		/*Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename = copyRename(
				this.rename);*/
		Map<Role, LinkedHashMap<Integer, Set<AssrtIntVar>>> scopes = copyScopes(this.scopes);

		P.put(self, succ);
		/*//Q.get(es.peer).put(self, es.toTrueAssertion());  // HACK FIXME: cf. AssrtSConfig::fire
		//Q.get(es.peer).put(self, es);  // Now doing toTrueAssertion on message at receive side
		Q.get(es.peer).put(self, new AssrtCoreEMsg(es.getModelFactory(), es.peer, es.mid, es.payload, es.ass, 
				//es.annot,
				es.stateexprs,
				rename.get(self)));  // Now doing toTrueAssertion on message at receive side*/

		AssrtCoreEMsg msg = ((AssrtCoreEModelFactory) this.mf.local).AssrtCoreEMsg(
				a.peer, a.mid, a.payload, a.ass, a.sexprs);//, rename.get(self));
		SSingleBuffers Q = this.Q.send(self, msg);

		updateOutput(self, a, succ, K, F, V, R, //rename
				scopes);
		//updateR(R, self, es);

		return ((AssrtCoreSModelFactory) this.mf.global).AssrtCoreSConfig(P, Q, V,
				R, K, F, //rename
				scopes);
	}

  // CHECKME: only need to update self entries of Maps -- almost: except for addAnnotOpensToF, and some renaming via Streams
	private void updateOutput(Role self, AssrtCoreEAction a, EFsm succ,
			Map<Role, Set<AssrtIntVar>> K, 
			Map<Role, Set<AssrtBFormula>> F,
			Map<Role, Map<AssrtIntVar, AssrtAFormula>> V,
			Map<Role, Set<AssrtBFormula>> R,
			//Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename)
			Map<Role, LinkedHashMap<Integer, Set<AssrtIntVar>>> scopes)
	{
		updateKFVR(self, a, a.getAssertion(), succ, K, F, V, R);
		
		/*for (PayElemType<?> e : ((EAction) a).payload.elems)  // CHECKME: EAction closest base type
		{
			if (e instanceof AssrtAnnotDataName)
			{
				AssrtIntVar v = ((AssrtAnnotDataName) e).var;
				//renameOldVarsInF(self, v, F);//, rename);  // CHECKME
				updateForAnnotVar(v, K.get(self));  // Currently, addAnnotVarToK
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);  
						// Regular DataType pay elems have been given fresh annot vars (AssrtCoreGProtoDeclTranslator.parsePayload) -- no other pay elems allowed
			}
		}
		updateForAssertionAndStateExprs(self,
				a.getAssertion(), a.getStateExprs(), succ,  // FIXME: assumes v is the only var (o/w ass/svars repeated) -- ?
				K, F, V, R);//, rename);

		updateScopes(self, a, succ, K.get(self), F.get(self), scopes.get(self));
				// FIXME TODO: V/R and scopes -- scopes records statevardecls ?*/
	}

	// TODO: pass `Kself`, `Fself`, etc. directly (and not `self`)
	// `ass` is the difference between output/input
	private void updateKFVR(Role self, AssrtCoreEAction a, AssrtBFormula ass, EFsm succ,
			Map<Role, Set<AssrtIntVar>> K, 
			Map<Role, Set<AssrtBFormula>> F,
			Map<Role, Map<AssrtIntVar, AssrtAFormula>> V,
			Map<Role, Set<AssrtBFormula>> R)
	{
		//- first K, F
		//-- for each pay elem:
		//--- GC (transitively?) old K, F -- any affected V, R already implicitly GC
		//--- add new K, F
		//
		//- then V, R
		//- for each state var
		//--- if continue
		//---- GC (transitively?) old V, R (also K, F?) -- outer statevars implicitly won't be affected
		//--- add V, R
		//XXX-- if f/w -- already treated by FSM gen? no, just inlined into statevar exprs

		Set<AssrtIntVar> Kself = K.get(self);
		Set<AssrtBFormula> Fself = F.get(self);
		for (PayElemType<?> e : ((EAction) a).payload.elems)  // CHECKME: EAction closest base type
		{
			if (e instanceof AssrtAnnotDataName)
			{
				AssrtIntVar v = ((AssrtAnnotDataName) e).var;
				gcKF(Kself, Fself, v);  // CHECKME: redundant to remove from Kself, then add back
				Kself.add(v);
				Fself.add(ass);  // The difference between output and input
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);  
						// Regular DataType pay elems have been given fresh annot vars (AssrtCoreGProtoDeclTranslator.parsePayload) -- no other pay elems allowed
			}
		}
		compactF(Fself);

		// "forward" recs will have state vars (svars) but no action state-exprs (aexprs)
		AssrtEState s = (AssrtEState) succ.curr;
		LinkedHashMap<AssrtIntVar, AssrtAFormula> svars = s.getStateVars();
		List<AssrtAFormula> aexprs = a.getStateExprs();
				// Following must come after F update (addAnnotBexprToF)
				// Update R from state -- entering a rec "forwards", i.e., not via a continue
		if (!svars.isEmpty() || !aexprs.isEmpty())
		{
			Map<AssrtIntVar, AssrtAFormula> Vself = V.get(self);
			Set<AssrtBFormula> Rself = R.get(self);  // Cf. Fself
			//boolean isEntry = aexprs.isEmpty() && !svars.isEmpty();  
			boolean isContinue = !aexprs.isEmpty();
					// Rec-entry: expr args already inlined into the rec statevars (i.e., by proto inlining) -- CHECKME: means "forwards entry?" robust?  refactor?

			Iterator<AssrtAFormula> i = aexprs.iterator();
			for (Entry<AssrtIntVar, AssrtAFormula> e : svars.entrySet())
			{
				AssrtIntVar svar = e.getKey();
				AssrtAFormula sexpr;
				if (aexprs.isEmpty())
				{
					sexpr = e.getValue();  // "Init" state var expr
				}
				else
				{
					sexpr = (AssrtAFormula) renameFormula(i.next());
				}
				if (isContinue) {  // CHECKME: "shadowing", e.g., forwards statevar has same name as a previous
					gcVR(Vself, Rself, svar);
				}
				Vself.put(svar, sexpr);
				Rself.add(s.getAssertion());
			}
			//compactR(Rself);  // TODO? (see above)
			compactF(Rself);
		}
	}

	private static <T extends Formula> AssrtSmtFormula<T>
			renameFormula(AssrtSmtFormula<T> f)
	{
		for (AssrtIntVar v : f.getIntVars())
		{
			AssrtIntVarFormula old = AssrtFormulaFactory.AssrtIntVar(v.toString());  // N.B. making *Formula*
			AssrtIntVarFormula fresh = AssrtFormulaFactory
					.AssrtIntVar("_" + v.toString());  // HACK
			f = f.subs(old, fresh);  // N.B., works on Formulas
		}
		return f;
	}
	
	private static void compactF(Set<AssrtBFormula> Fself)
	{
		Iterator<AssrtBFormula> i = Fself.iterator();
		while (i.hasNext())
		{
			AssrtBFormula f = i.next();
			if (f.equals(AssrtTrueFormula.TRUE) 
					|| f.getIntVars().stream().anyMatch(x -> x.toString().startsWith("_"))
					) 
							// Pruning if formula contains "old" var renamed by renameOldVarsInF -- FIXME refactor to renameOldVarsInF?  CHECKME: other sources of renaming?
			{
				i.remove();
			}
		}
	}
	
	private void gcKF(Set<AssrtIntVar> Kself, Set<AssrtBFormula> Fself, AssrtIntVar v) 
	{
		Kself.remove(v);
		Iterator<AssrtBFormula> i = Fself.iterator();
		while (i.hasNext())
		{
			if (i.next().getIntVars().contains(v)) 
			{
				i.remove();  // CHECKME: do transitively for vars in removed formula?
			}
		}
	}
	
	private void gcVR(Map<AssrtIntVar, AssrtAFormula> Vself, Set<AssrtBFormula> Rself, AssrtIntVar v)
	{
		Vself.remove(v);
		Iterator<AssrtBFormula> i = Rself.iterator();
		while (i.hasNext())
		{
			if (i.next().getIntVars().contains(v)) 
			{
				i.remove();  // CHECKME: do transitively for vars in removed formula?
			}
		}
	}

	// CHECKME: manage F with receive assertions?
	private AssrtCoreSConfig fireRecv(Role self, AssrtCoreERecv a, EFsm succ)
	{
		Map<Role, EFsm> P = new HashMap<>(this.P);
		Map<Role, Set<AssrtIntVar>> K = copyK(this.K);
		Map<Role, Set<AssrtBFormula>> F = copyF(this.F);
		Map<Role, Map<AssrtIntVar, AssrtAFormula>> V = copyR(this.V);
		//R.get(self).putAll(succ.getAnnotVars());  // Should "overwrite" previous var values -- no, do later (and from action info, not state)
		Map<Role, Set<AssrtBFormula>> R = copyRass(this.R);
		/*Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename = copyRename(
				this.rename);*/
		Map<Role, LinkedHashMap<Integer, Set<AssrtIntVar>>> scopes = copyScopes(this.scopes);

		P.put(self, succ);
		AssrtCoreEMsg msg = (AssrtCoreEMsg) this.Q.getQueue(self).get(a.peer);  // null is \epsilon
		SSingleBuffers Q = this.Q.receive(self, a);

		updateInput(self, a, msg, msg.shadow, succ, K, F, V, R,// rename
				scopes
				);
		//updateR(R, self, es);

		return ((AssrtCoreSModelFactory) this.mf.global).AssrtCoreSConfig(P, Q, V,
				R, K, F,// rename
				scopes
				);
	}

	// "a" is the EFSM input action, which has (hacked) True ass; msg is the dequeued msg, which carries the (actual) ass from the output side
	// CHECKME: factor better with updateOutput ?
	private void updateInput(Role self, AssrtCoreEAction a,
			AssrtCoreEMsg msg, Map<AssrtIntVarFormula, AssrtIntVarFormula> shadow,
			EFsm succ,
			Map<Role, Set<AssrtIntVar>> K, Map<Role, Set<AssrtBFormula>> F,
			Map<Role, Map<AssrtIntVar, AssrtAFormula>> V,
			Map<Role, Set<AssrtBFormula>> R,
			//Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename  // CHECKME: EAction closest base type -- ?
			Map<Role, LinkedHashMap<Integer, Set<AssrtIntVar>>> scopes
			)
	{
		updateKFVR(self, a, msg.getAssertion(), succ, K, F, V, R);

		/*for (PayElemType<?> pt : ((EAction) a).payload.elems)
		{
			if (pt instanceof AssrtAnnotDataName)
			{
				AssrtIntVar v = ((AssrtAnnotDataName) pt).var;
				updateForAnnotVar(v, K.get(self));  // Update K
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);
						// Regular DataName pay elems have been given fresh annot vars (AssrtCoreGProtoDeclTranslator.parsePayload) -- no other pay elems allowed
			}
		}

		// N.B. no "updateRfromF" -- actually, "update V from payload annot" -- leaving V statevars as they are is OK, validation only done from F's and V already incorporated into F (and updates handled by updateFfromV)
		// But would it be more consistent to update V?
		
		/*
		// CHECKME: what is an example?
		Set<AssrtBFormula> H = F.get(self);
		Set<Entry<AssrtIntVarFormula, AssrtIntVarFormula>> reself = rename
				.get(self).entrySet();
		for (Entry<AssrtIntVarFormula, AssrtIntVarFormula> e : shadow.entrySet()
				.stream().filter(x -> !reself.contains(x))
				.collect(Collectors.toList()))
		{
			H = H.stream().map(x -> x.subs(e.getKey(), e.getValue())).collect(Collectors.toSet());
		}
		F.put(self, H);
		rename.get(self).putAll(shadow);
		* /

		updateForAssertionAndStateExprs(self,
				msg.getAssertion(), a.getStateExprs(), succ, 
				K, F, V, R);//, rename);
				// Actual assertion (f) for annotvar (v) added in here
		
		updateScopes(self, a, succ, K.get(self), F.get(self), scopes.get(self));
		*/
	}

	// FIXME: V
	private void fireAcc(Map<Role, EFsm> P, SSingleBuffers Q,
			Map<Role, Map<AssrtIntVar, AssrtAFormula>> V,
			Map<Role, Set<AssrtBFormula>> R,
			Map<Role, Set<AssrtIntVar>> K, Map<Role, Set<AssrtBFormula>> F, 
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename,
			Role self, AssrtCoreEAcc a, EFsm succ)
	{
		throw new RuntimeException("[TODO] : " + a);
		/*P.put(self, succ);
		AssrtCoreEPendingRequest pr = (AssrtCoreEPendingRequest) Q.get(self)
				.put(a.peer, null);
		AssrtCoreEReq msg = pr.getMsg();  // CHECKME
		Q.get(a.peer).put(self, null);

		updateInput(self, a, pr,  // msg?
				pr.shadow, succ, 
				K, F, V, R, rename);*/
	}

	private void fireReq(Map<Role, EFsm> P, SSingleBuffers Q,
			Map<Role, Map<AssrtIntVar, AssrtAFormula>> V,
			Map<Role, Set<AssrtBFormula>> R,
			Map<Role, Set<AssrtIntVar>> K, Map<Role, Set<AssrtBFormula>> F,
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename,
			Role self, AssrtCoreEReq a, EFsm succ)
	{
		throw new RuntimeException("[TODO] : " + a);
		/*P.put(self, succ);

		updateOutput(self, a, succ, K, F, V, R, rename);

		Q.get(a.peer).put(self, new AssrtCoreEPendingRequest(a, rename.get(self)));  // Now doing toTrueAssertion on accept side*/
	}

	/*// Doesn't include pending requests, checks isInputQueueEstablished
	private boolean hasMsg(Role self, Role peer)
	{
		return isInputQueueEstablished(self, peer)  // input queue is established (not \bot and not <a>)
				&& this.Q.get(self).get(peer) != null;  // input queue is not empty
	}
	
	// Direction sensitive (not symmetric) -- isConnected means dest has established input queue from src
	// i.e. "fully" established, not "pending" -- semantics relies on all action firing being guarded on !hasPendingConnect
	private boolean isInputQueueEstablished(Role dest, Role src)  // N.B. is more like the "input buffer" at r1 for r2 -- not the actual "connection from r1 to r2"
	{
		AssrtCoreEMsg m = this.Q.get(dest).get(src);
		return !(m instanceof AssrtCoreEBot)
				&& !(m instanceof AssrtCoreEPendingRequest);
		//return es != null && es.equals(AssrtCoreEBot.ASSSRTCORE_BOT);  // Would be same as above
	}

	// req waiting for acc -- cf. reverse direction to isConnect
	private boolean isPendingRequest(Role req, Role acc)  // FIXME: for open/port annotations
	{
		//return (this.ports.get(r1).get(r2) != null) || (this.ports.get(r2).get(r1) != null);
		AssrtCoreEMsg m = this.Q.get(acc).get(req);  // N.B. reverse direction to isConnected
		return m instanceof AssrtCoreEPendingRequest;
	}

	private boolean hasPendingRequest(Role req)
	{
		return this.Q.keySet().stream().anyMatch(acc -> isPendingRequest(req, acc));
	}*/
	
	// Need to consider hasPendingRequest? -- no: the semantics blocks both sides until connected, so don't need to validate those "intermediate" states
	//public boolean isReceptionError()
	@Override
	public Map<Role, ? extends AssrtCoreERecv> getStuckMessages()
	{
		Map<Role, AssrtCoreERecv> res = new HashMap<>();
		for (Role self : this.efsms.keySet())
		{
			EFsm s = this.efsms.get(self);
			EStateKind k = s.curr.getStateKind();
			if (k == EStateKind.UNARY_RECEIVE || k == EStateKind.POLY_RECIEVE)
			{
				Role peer = s.curr.getActions().get(0).peer;  // Pre: consistent ext choice subj
				AssrtCoreESend msg = (AssrtCoreESend) this.queues.getQueue(self).get(peer);
				if (msg != null)
				{
					AssrtCoreERecv dual = msg//.toTrueAssertion()
							.toDual(peer);  // N.B. toTrueAssertion
					if (!s.curr.hasAction(dual))  // CHECKME: ...map(a -> ((AssrtCoreESend) a.toDual(dst)).toTrueAssertion()) ?
								// FIXME: check assertion implication (not just syntactic equals) -- cf. AssrtSConfig::fire
					{
						res.put(self, msg.toDual(peer));  // "Original" message
					}
				}
			}
		}
		return res;
	}

	/*@Override
	protected Set<Role> getWaitingFor(Role r)
	{
		throw new RuntimeException("[TODO] : " + r);
	}*/

	/*// TODO: orphan pending requests -- maybe shouldn't?  handled by synchronisation error?
	//public boolean isOrphanError(Map<Role, AssrtEState> E0)
	public Map<Role, Set<? extends AssrtCoreESend>> getOrphanMessages()
	{
		Map<Role, Set<? extends AssrtCoreESend>> res = new HashMap<>();
		for (Role r : this.efsms.keySet())
		{
			Set<ESend> orphs = new HashSet<>();
			EFsm fsm = this.efsms.get(r);
			if (fsm.curr.isTerminal())  // Local termination of r, i.e. not necessarily "full deadlock cycle"
			{
				orphs.addAll(this.queues.getQueue(r).values().stream()
						.filter(v -> v != null).collect(Collectors.toSet()));
			}
			else
			{
				this.efsms.keySet().stream()
						.filter(x -> !r.equals(x) && !this.queues.isConnected(r, x))  // !isConnected(r, x), means r considers its side closed
						.map(x -> this.queues.getQueue(r).get(x)).filter(x -> x != null)  // r's side is closed, but remaining message(s) in r's buff
						.forEachOrdered(x -> orphs.add(x));
			}
			if (!orphs.isEmpty())
			{
				res.put(r, orphs);
			}
		}
		return res;
	}*/

	// Request/accept are bad if local queue is established
	// N.B. request/accept when remote queue is established is not an error -- relying on semantics to block until both remote/local queues not established; i.e., error precluded by design of model, not by validation
	// N.B. not considering pending requests, for the same reason as above and as why not considered for, e.g., reception errors -- i.e., not validating those "intermediate" states
	// Deadlock/progress errors that could be related to "decoupled" connection sync still caught by existing checks, e.g., orphan message or unfinished role
	@Deprecated
	public boolean isConnectionError()
	{
		throw new RuntimeException("[TODO] : ");
		/*return this.P.entrySet().stream().anyMatch(e -> 
				e.getValue().getActions().stream().anyMatch(a ->
						(a.isRequest() || a.isAccept()) && isInputQueueEstablished(e.getKey(), a.peer)
		));
				// FIXME: check for pending port, if so then port is used -- need to extend an AnnotEConnect type with ScribAnnot (cf. AnnotPayloadType)*/
	}

	// Send is bad if either local queue or remote queue is not established, and no pending request to the target
	// Receive is bad if local queue is not established and no pending request to the target
	@Deprecated
	public boolean isUnconnectedError()
	{
		throw new RuntimeException("[TODO] : ");
		/*return this.P.entrySet().stream().anyMatch(e -> 
		{
			Role r = e.getKey();
			return 
					e.getValue().getActions().stream().anyMatch(a ->
									(a.isSend() &&
										(!isInputQueueEstablished(r, a.peer) || !isInputQueueEstablished(a.peer, r)) && !isPendingRequest(r, a.peer))
							|| (a.isReceive() && !isInputQueueEstablished(r, a.peer) && !isPendingRequest(r, a.peer)));
			// Don't need to use isPendingRequest(a.peer, r) because only requestor is "at the next state" while request pending 
		});*/
	}

	// "Connection message" reception error
	@Deprecated
	public boolean isSynchronisationError()
	{
		throw new RuntimeException("[TODO] : ");
		/*return this.P.entrySet().stream().anyMatch(e ->  // e: Entry<Role, EState>
		{
			EState s = e.getValue();
			EStateKind k = s.getStateKind();
			if (k != EStateKind.ACCEPT)
			{
				return false;
			}
			Role dest = e.getKey();
			List<EAction> as = s.getActions();
			Role src = as.get(0).peer;
			return isPendingRequest(src, dest)
					&& !as.contains(
								 ((AssrtCoreEPendingRequest) this.Q.get(dest).get(src)).getMsg()
								.toTrueAssertion().toDual(src)
							);
		});*/
	}
	
	// Cf. refactor syntactically?  cf. connection errors
	public Map<Role, Set<AssrtCoreEAction>> getUnknownDataVarErrors(
			AssrtCore core, GProtoName fullname)
	{
		Map<Role, Set<AssrtCoreEAction>> res = new HashMap<>();
		for (Entry<Role, EFsm> e : this.P.entrySet())
		{
			Role self = e.getKey();
			EState curr = e.getValue().curr;
			Set<AssrtIntVar> Kself = this.K.get(self);
			Set<AssrtIntVar> Vself = this.V.get(self).keySet();
			Set<String> rs = core.getContext().getInlined(fullname).roles.stream()
					.map(Object::toString).collect(Collectors.toSet());
			Predicate<EAction> isErr = a ->
			{
				if (a.isSend() || a.isRequest())
				{
					Set<AssrtIntVar> known = a.payload.elems.stream()
							.map(x -> ((AssrtAnnotDataName) x).var)
							.collect(Collectors.toSet());
						// TODO: throw new RuntimeException("[assrt-core] Shouldn't get in here: " + pe);
					known.addAll(Kself);
					known.addAll(Vself);
					return ((AssrtCoreEAction) a).getAssertion().getIntVars().stream()
							//.filter(x -> !rs.contains(x.toString()))  // CHECKME: formula role vars -- what is this for?  what is an example?
							.anyMatch(x -> !known.contains(x));
				}
				else
				{
					return false;  // CHECKME: input-side assertions? currently hardcoded to True
				}
			};
			Set<AssrtCoreEAction> tmp = curr.getDetActions().stream()
					.filter(x -> isErr.test(x)).map(x -> (AssrtCoreEAction) x)
					.collect(Collectors.toSet());
			if (!tmp.isEmpty())
			{
				res.put(self, tmp);
			}
		}
		return res;
	}
	
	// N.B. actually a safety error, not a "progress" error
	// i.e., output state has a "well-asserted" action
	public Map<Role, EState> getAssertProgressErrors(AssrtCore core,
			GProtoName fullname)
			// CHECKME: not actually a "progress" error -- "safety"?
	{
		//return this.P.entrySet().stream().anyMatch(e ->  // anyMatch is on the endpoints (not actions)
		Map<Role, EState> res = new HashMap<>();
		for (Entry<Role, EFsm> e : this.P.entrySet())
		{
			Role self = e.getKey();
			EState curr = e.getValue().curr;
			AssrtBFormula squashed = getAssertProgressCheck(core, fullname, self,
					curr);
			if (squashed.equals(AssrtTrueFormula.TRUE))
			{
				continue;
			}

			core.verbosePrintln("\n[assrt-core] Checking assertion progress for "
					+ self + "(" + curr.id + "):");
			core.verbosePrintln("  squashed = " + squashed.toSmt2Formula());
			if (!core.checkSat(fullname,
					Stream.of(squashed).collect(Collectors.toSet())))
			{
				res.put(self, curr);
			}
			core.verbosePrintln("");
		}
		return res;
	}

	// formula: isAssertionProgressSatisfied (i.e., true = OK)
	private AssrtBFormula getAssertProgressCheck(AssrtCore core,
			GProtoName fullname, Role self, EState curr)
	{
		//if (as.isEmpty() || as.stream().noneMatch(a -> a.isSend() || a.isRequest())) 
		if (curr.isTerminal() || curr.getStateKind() != EStateKind.OUTPUT)  // CHECKME: only output states?
		{
			return AssrtTrueFormula.TRUE;
		}
		List<EAction> as = curr.getActions();  // N.B. getActions includes non-fireable
		if (as.stream().anyMatch(x -> x instanceof EDisconnect))
		{
			throw new RuntimeException("[assrt-core] [TODO] Disconnect actions: " + as);
		}

		// lhs = conjunction of F terms, V eq-terms and R terms -- i.e., what we already know
		Set<AssrtBFormula> Fself = this.F.get(self);
		AssrtBFormula lhs = Fself.isEmpty()
				? AssrtTrueFormula.TRUE
				: Fself.stream().reduce((x1, x2) -> AssrtFormulaFactory
						.AssrtBinBool(AssrtBinBFormula.Op.And, x1, x2)).get();  // Here, conjunction of F terms
		Map<AssrtIntVar, AssrtAFormula> Vself = this.V.get(self);
		if (!Vself.isEmpty())
		{
			AssrtBFormula vconj = Vself.entrySet().stream()
					.map(x -> (AssrtBFormula) AssrtFormulaFactory.AssrtBinComp(  // (Cast needed for reduce)
							AssrtBinCompFormula.Op.Eq,
							AssrtFormulaFactory.AssrtIntVar(x.getKey().toString()),
							x.getValue()))
					.reduce((e1, e2) -> (AssrtBFormula) AssrtFormulaFactory  // Next, conjunction of V eq-terms
							.AssrtBinBool(AssrtBinBFormula.Op.And, e1, e2))
					.get();  // Non-empty
			lhs = AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.And, lhs, vconj);
		}
		Set<AssrtBFormula> Rself = this.R.get(self);
		if (!Rself.isEmpty())
		{
			AssrtBFormula Rconj = Rself.stream().reduce((b1, b2) -> AssrtFormulaFactory  // Next, conjunction of R terms
					.AssrtBinBool(AssrtBinBFormula.Op.And, b1, b2)).get();  // Non-empty
			lhs = AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.And, lhs, Rconj);
		}
		
		// CHECKME: why V/R not also built here? cf. getRecAssertCheck-- lhs building should be uniform for all checks?

		// rhs = disjunction of assertions (ex-qualified by pay annot vars) from each action -- i.e., what we would like to do
		AssrtBFormula rhs = null;
		for (EAction a : as)
		{
			if (!(a instanceof AssrtCoreESend) && !(a instanceof AssrtCoreEReq))
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);
			}
			AssrtBFormula ass = ((AssrtCoreEAction) a).getAssertion();
			if (ass.equals(AssrtTrueFormula.TRUE))
			{
				return AssrtTrueFormula.TRUE;  // If any assertion is True, then assertion-progress trivially satisfied
			}
			Set<AssrtIntVarFormula> assVars = a.payload.elems.stream()
					.map(x -> AssrtFormulaFactory
							.AssrtIntVar(((AssrtAnnotDataName) x).var.toString()))
					.collect(Collectors.toSet());  // ex-qualify pay annot vars, this will be *some* set of values
					// N.B. includes the case for recursion cycles where var is "already" in F
					// CHECKME: Adding even if var not used?
			if (!assVars.isEmpty()) // CHECKME: currently never empty
			{
				ass = AssrtFormulaFactory.AssrtExistsFormula(new LinkedList<>(assVars),
						ass);
			}
			rhs = (rhs == null) 
					? ass
					: AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.Or, rhs, ass);
		}
		
		AssrtBFormula impli = AssrtFormulaFactory
				.AssrtBinBool(AssrtBinBFormula.Op.Imply, lhs, rhs);
		return forallQuantifyFreeVars(core, fullname, impli).squash();  // Finally, fa-quantify all free vars
	}
	
	/* // For batching?
	public Set<AssrtBFormula> getAssertionProgressChecks(Job job, GProtoName simpname)
	{
		return this.P.entrySet().stream().map(e ->  // anyMatch is on the endpoints (not actions)
			getAssertionProgressCheck(job, simpname, e.getKey(), e.getValue())
		).collect(Collectors.toSet());
	}*/

	// i.e., state has an action that is not satisfiable (deadcode)
	public Map<Role, Set<AssrtCoreEAction>> getAssertUnsatErrors(AssrtCore core,
			GProtoName fullname)
	{
		Map<Role, Set<AssrtCoreEAction>> res = new HashMap<>();
		for (Entry<Role, EFsm> e : this.P.entrySet())
		{
			Role self = e.getKey();
			EState curr = e.getValue().curr;
			if (curr.getStateKind() != EStateKind.OUTPUT)
			{
				continue;
			}
			List<EAction> as = curr.getActions(); // N.B. getActions includes non-fireable
			if (as.size() <= 1)  
					// Only doing on non-unary choices -- for unary, assrt-prog implies assrt-sat
					// Note: this means "downstream" assrt-unsat errors for unary-choice continuations will not be caught (i.e., false => false for assrt-prog)
			{
				continue;  // No: for state-vars and state-assertions? Is it even definitely skippable without those? -- CHECKME
			}
			if (as.stream().anyMatch(x -> x instanceof EDisconnect))
			{
				throw new RuntimeException(
						"[assrt-core] [TODO] Disconnect actions: " + as);
			}
			
			for (EAction a : as)
			{
				AssrtCoreEAction cast = (AssrtCoreEAction) a;
				AssrtBFormula squashed = getAssertSatCheck(core, fullname, self, cast);
				if (squashed.equals(AssrtTrueFormula.TRUE))  // OK to skip? i.e., no need to check existing F (impli LHS) is true?
				{
					continue; 
				}

				core.verbosePrintln(
						"\n[assrt-core] Checking assertion satisfiability for " + self
								+ "(" + curr.id + "):");
				core.verbosePrintln("  squashed = " + squashed.toSmt2Formula());
				if (!core.checkSat(fullname,
						Stream.of(squashed).collect(Collectors.toSet())))
				{
					Set<AssrtCoreEAction> tmp = res.get(self);
					if (tmp == null)
					{
						tmp = new HashSet<>();
						res.put(self, tmp);
					}
					tmp.add(cast);
				}
				core.verbosePrintln("");
			}
		}
		return res;
	}

	// formula: isSatisfiable (i.e., true = OK)
	private AssrtBFormula getAssertSatCheck(AssrtCore core, GProtoName fullname,
			Role self, AssrtCoreEAction a)
	{
		AssrtBFormula ass = a.getAssertion();
		if (ass.equals(AssrtTrueFormula.TRUE))  // OK to skip? i.e., no need to check existing F (impli LHS) is true?
		{
			return AssrtTrueFormula.TRUE; 
		}

		// Here, action assertion, ex-quantified by pay annot vars
		AssrtBFormula res = ass;
		Set<AssrtIntVarFormula> varsA = ((EAction) a).payload.elems.stream()
				.map(x -> AssrtFormulaFactory
						.AssrtIntVar(((AssrtAnnotDataName) x).var.toString()))
				.collect(Collectors.toSet());
				// N.B. includes the case for recursion cycles where var is "already" in F
				// Adding even if var not used
		if (!varsA.isEmpty()) // CHECKME: currently never empty
		{
			res = AssrtFormulaFactory.AssrtExistsFormula(new LinkedList<>(varsA),
					res);
		}
		
		// Next, conjunction of F terms -- CHECKME: always non-empty?
		res = this.F.get(self).stream().reduce(res, (b1, b2) -> AssrtFormulaFactory
				.AssrtBinBool(AssrtBinBFormula.Op.And, b1, b2));
		
		// Next, conjunction of V eq-terms
		// Include Vself and Rself, to check lhs(?) is sat for assrt-prog (o/w false => any)
		Map<AssrtIntVar, AssrtAFormula> Vself = this.V.get(self);
		if (!Vself.isEmpty())
		{  // Cast needed for reduce
			AssrtBFormula Vconj = Vself.entrySet().stream()
					.map(x -> (AssrtBFormula) AssrtFormulaFactory.AssrtBinComp(
							AssrtBinCompFormula.Op.Eq,
							AssrtFormulaFactory.AssrtIntVar(x.getKey().toString()),
							x.getValue()))
					.reduce((e1, e2) -> (AssrtBFormula) AssrtFormulaFactory
							.AssrtBinBool(AssrtBinBFormula.Op.And, e1, e2))
					.get();  // non-empty
			res = AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.And,
					res, Vconj);
		}

		// Next, conjunction of R terms
		Set<AssrtBFormula> Rself = this.R.get(self);
		if (!Rself.isEmpty())
		{
			AssrtBFormula Rconj = Rself.stream()
					.reduce((b1, b2) -> AssrtFormulaFactory
							.AssrtBinBool(AssrtBinBFormula.Op.And, b1, b2))
					.get();  // Non-empty
			res = AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.And, res,
					Rconj);
		}

		// Finally, *ex*-quanitfy all free vars -- cf. forallQuantifyFreeVars
		Set<String> rs = core.getContext().getInlined(fullname).roles.stream()
				.map(Object::toString).collect(Collectors.toSet());
		Set<AssrtIntVar> free = res.getIntVars().stream()
				//.filter(x -> !rs.contains(x.toString()))  // CHECKME: formula role vars -- cf. getUnknownDataVarErrors  // CHECKME: what is the example?
				.collect(Collectors.toSet());
		if (!free.isEmpty())
		{
			res = AssrtFormulaFactory.AssrtExistsFormula(
					free.stream().map(v -> AssrtFormulaFactory.AssrtIntVar(v.toString()))
							.collect(Collectors.toList()),
					res);  // Cf. assrt-prog -- here, don't need action to be sat *forall* prev, just sat for *some* prev
		}

		return res.squash();
	}
	
	/*public Set<AssrtBFormula> getSatisfiableChecks(Job job, GProtoName simpname)
	{
		return this.P.entrySet().stream().flatMap(e ->  // anyMatch is on the endpoints (not actions)
		e.getValue().getActions().stream().map(a -> getSatisfiableCheck(job,
				simpname, e.getKey(), (AssrtCoreEAction) a))
		).collect(Collectors.toSet());
	}*/

  // Otherwise initial assertions not checked, since no incoming action (cf. below)
	public Map<Role, AssrtEState> getInitRecAssertErrors(AssrtCore core,
			GProtoName fullname)
	{
		Map<Role, AssrtEState> res = new HashMap<>();
		for (Entry<Role, EFsm> e : this.P.entrySet())
		{
			Role self = e.getKey();
			AssrtEState curr = (AssrtEState) e.getValue().curr;
			AssrtBFormula toCheck = getInitRecAssertCheck(core, fullname, self, curr);
			if (toCheck.equals(AssrtTrueFormula.TRUE))
			{
				continue;
			}

			core.verbosePrintln(
					"\n[assrt-core] Checking initial recursion assertion for " + self
							+ "(" + curr.id + "):");
			core.verbosePrintln("  squashed = " + toCheck.toSmt2Formula());
			if (!core.checkSat(fullname,
					Stream.of(toCheck).collect(Collectors.toSet())))
			{
				res.put(self, curr);
			}
			core.verbosePrintln("");
		}
		return res;
	}

	// formula: isNotRecursionAssertionSatisfied (i.e., true = OK)
	private AssrtBFormula getInitRecAssertCheck(AssrtCore core,
			GProtoName fullname, Role self, AssrtEState curr)
	{
		AssrtBFormula toCheck = curr.getAssertion();
		if (toCheck.equals(AssrtTrueFormula.TRUE))
		{
			return AssrtTrueFormula.TRUE;
		}
		
		Map<AssrtIntVar, AssrtAFormula> Vself = this.V.get(self);
		if (!Vself.isEmpty())
		{
			AssrtBFormula Vconj = Vself.entrySet().stream().map(x -> (AssrtBFormula)  // Cast needed
					AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq,
							AssrtFormulaFactory.AssrtIntVar(x.getKey().toString()), x.getValue()))
							// Currently allowing recurison-assertion without any statevardecls (i.e., cannot use any vars), but pointless?
					.reduce((b1, b2) -> AssrtFormulaFactory
							.AssrtBinBool(AssrtBinBFormula.Op.And, b1, b2))
							// do-statevar expr args for "forwards" rec already inlined into rec-statevars
					.get();
			toCheck = AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.Imply,
					Vconj, toCheck);
		}

		return forallQuantifyFreeVars(core, fullname, toCheck).squash();
	}			

	/*public Set<AssrtBFormula> getRecursionAssertionChecks(Job job, GProtoName simpname, AssrtCoreSConfig init)
	{
		if (this.id == init.id)
		{
			return this.P.entrySet().stream().map(e ->  // anyMatch is on the endpoints (not actions)
					getInitRecursionAssertionCheck(job, simpname, e.getKey(), e.getValue())
			).collect(Collectors.toSet());
		}
		else
		{
			return this.P.entrySet().stream().flatMap(e ->  // anyMatch is on the endpoints (not actions)
					e.getValue().getActions().stream().map(a -> getNonInitRecursionAssertionCheck(job, simpname, e.getKey(), e.getValue(), a))
			).collect(Collectors.toSet());
		}
	}*/

	// Pre: stuckMessages checked
	// CHECKME: equivalent of assertion progress for rec-assertions?  unsat not needed for recs? (because for "unary-choice" they coincide?)
	// Excluding "init" rec
	public Map<Role, Set<AssrtCoreEAction>> getRecAssertErrors(AssrtCore core,
			GProtoName fullname)
	{
		Map<Role, Set<AssrtCoreEAction>> res = new HashMap<>();
		for (Entry<Role, EFsm> e : this.P.entrySet())
		{
			Role self = e.getKey();
			AssrtEState curr = (AssrtEState) e.getValue().curr;
			Predicate<EAction> isSat = a ->  // false = error
			{
				AssrtCoreEAction cast = (AssrtCoreEAction) a;
				AssrtBFormula toCheck = getRecAssertCheck(core,
						fullname, self, curr, cast);
				if (toCheck.equals(AssrtTrueFormula.TRUE))
				{
					return true;
				}
					
				core.verbosePrintln("\n[assrt-core] Checking recursion assertion for "
						+ self + "(" + curr.id + "):");
				core.verbosePrintln("  squashed = " + toCheck.toSmt2Formula());
				boolean b = core.checkSat(fullname,
						Stream.of(toCheck).collect(Collectors.toSet()));
				core.verbosePrintln("");
				return b;
			};
			EStateKind kind = curr.getStateKind();
			if (kind == EStateKind.OUTPUT)
			{
				Set<AssrtCoreEAction> tmp = curr.getActions().stream()
						.filter(x -> !isSat.test(x)).map(x -> (AssrtCoreEAction) x)
						.collect(Collectors.toSet());
				if (!tmp.isEmpty())
				{
					res.put(self, tmp);
				}
			}
			else if (kind == EStateKind.UNARY_RECEIVE
					|| kind == EStateKind.POLY_RECIEVE)
			{
				Role peer = curr.getActions().iterator().next().peer;
				if (!this.Q.isConnected(self, peer)
						|| this.Q.getQueue(self).get(peer) == null)
				{
					continue;
				}
				AssrtCoreEMsg m = (AssrtCoreEMsg) this.Q.getQueue(self).get(peer);
				EAction a = m.toDual(peer);  // Stuck messages already checked -- CHECKME: currently includes check between msg-assertion and action-assertion?
				if (!isSat.test(a))
				{
					res.put(self,
							Stream.of((AssrtCoreEAction) a).collect(Collectors.toSet()));
				}
			}
			else if (kind != EStateKind.TERMINAL)
			{
				throw new RuntimeException(
						"\n[assrt-core] Shouldn't get in here: " + kind);
			}
		}
		return res;
	}

	// Pre: 'curr/a' is output kind, or 'curr' is input kind and 'a' is toDual of enqueued message
	// formula: isNotRecursionAssertionSatisfied (i.e., true = OK)
	private AssrtBFormula getRecAssertCheck(AssrtCore core, GProtoName fullname,
			Role self, AssrtEState curr, AssrtCoreEAction a)
	{
		//EStateKind kind = curr.getStateKind();
		EAction cast = (EAction) a;
		if (cast.isReceive() || cast.isAccept())
		{
			/*// Now redundant, checked by getRecAssertErrors
			// CHECKME: "skip" if no msg avail (because assertion carried by msg)? -- what is an example?
			if (!this.Q.isConnected(self, ((EAction) a).peer)
					|| this.Q.getQueue(self).get(cast.peer) == null)
				 // !isPendingRequest(a.peer, self))  // TODO: open/port annots
			{
				return AssrtTrueFormula.TRUE;
			}*/
		}
		else if (!(cast.isSend() || cast.isRequest()))
		{
			throw new RuntimeException(
					"[assrt-core] [TODO] " + cast.getClass() + ":\n\t" + cast);
		}
		AssrtEState succ = curr.getDetSucc(cast);
		AssrtBFormula sass = succ.getAssertion();	
		if (sass.equals(AssrtTrueFormula.TRUE))
		{
			return AssrtTrueFormula.TRUE;
		}

		// lhs = ...  // TODO: factor out lhs building with others above -- CHECKME lhs should be same for all?
		// Here, conjunction of F terms
		AssrtBFormula lhs = null;
		Set<AssrtBFormula> Fself = this.F.get(self);
		if (!Fself.isEmpty())
		{
			lhs = Fself.stream().reduce((x1, x2) -> AssrtFormulaFactory
					.AssrtBinBool(AssrtBinBFormula.Op.And, x1, x2)).get();
		}
		
		// Next, conjunction of V eq-terms
		Map<AssrtIntVar, AssrtAFormula> Vself = this.V.get(self);
		if (!Vself.isEmpty())
		{
			AssrtBFormula Vconj = Vself.entrySet().stream()
					.map(x -> (AssrtBFormula) AssrtFormulaFactory.AssrtBinComp(  // Cast needed for reduce
							AssrtBinCompFormula.Op.Eq,
							AssrtFormulaFactory.AssrtIntVar(x.getKey().toString()),
							x.getValue()))
					.reduce(
							(x1, x2) -> (AssrtBFormula) AssrtFormulaFactory
									.AssrtBinBool(AssrtBinBFormula.Op.And, x1, x2)
				).get();
			lhs = (lhs == null) 
					? Vconj
					: AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.And, lhs,
							Vconj);
		}
		
		// Next, assertion from action (carried by msg for input actions)
		AssrtBFormula aass = /*(cast.isSend() || cast.isRequest())  // CHECKME: AssrtEAction doesn't have those methods, refactor?
				? a.getAssertion()
				: //(a.isReceive() || a.isAccept())  // Has msg/req already checked at top
					((AssrtCoreEMsg) this.Q.getQueue(self).get(cast.peer)).getAssertion();
					// Cf. updateInput, msg.getAssertion()*/
				a.getAssertion();  // Input-kind action already derived (toDual) from enqueued message by getRecAssertErrors
		if (!aass.equals(AssrtTrueFormula.TRUE))
		{
			lhs = (lhs == null) 
					? aass
					: AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.And, lhs, aass);
		}
			
		List<AssrtAFormula> aexprs = a.getStateExprs();
		if (aexprs.isEmpty())  // "Forwards" rec entry -- cf. updateForAssertionAndStateExprs, updateRecEntry/Continue
		{
			// Next for lhs, state var eq-terms for initial rec entry
			LinkedHashMap<AssrtIntVar, AssrtAFormula> svars = succ.getStateVars();
			if (!svars.isEmpty())
			{
				AssrtBFormula svarsConj = svars.entrySet().stream()
						.map(x -> (AssrtBFormula) AssrtFormulaFactory.AssrtBinComp(  // Cast needed
								AssrtBinCompFormula.Op.Eq,
								AssrtFormulaFactory.AssrtIntVar(x.getKey().toString()),
								x.getValue()))  // do-statevar expr args for "forwards" rec already inlined into rec-statevars
						.reduce((x1, x2) -> AssrtFormulaFactory
								.AssrtBinBool(AssrtBinBFormula.Op.And, x1, x2))
						.get();
				lhs = (lhs == null) 
						? svarsConj
						: AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.And, lhs,
								svarsConj);
			}

			// rhs = target state (succ) assertion
			AssrtBFormula rhs = sass;
			
			// CHECKME: factor out with below
			AssrtBFormula impli = (lhs == null) 
					? rhs
					: AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.Imply, lhs,
							rhs);

			return forallQuantifyFreeVars(core, fullname, impli).squash();
		}
		else  // Rec-continue
		{
			// Next for lhs, rec ass (on original vars)  // e.g., lhs, ...x > 0... (for existing x) => exists ...x' > 0... (for new x')
			if (!sass.equals(AssrtTrueFormula.TRUE))
			{
				lhs = AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.And, lhs,
						sass);
			}

			// rhs = existing R assertions -- CHECKME: why not just target rec ass again? (like entry)  or why entry does not check this.R and new entry ass -- i.e., why rec entry/continue not uniform?
					// ^FIXME: should not be existing or just entry -- should be all existing up to entry ?
			Set<AssrtBFormula> Rself = this.R.get(self);   
					// Can use this.R because recursing, should already have all the terms to check (R added on f/w rec-entry updateRecEntry)
					// CHECKME: should it be *all* the terms so far? yes, because treating recursion assertions as invariants?
			//if (!Rself.isEmpty())
			AssrtBFormula rhs = Rself.stream().reduce(AssrtTrueFormula.TRUE,  // "identity-reduce" variant convenient here for below test
					(x1, x2) -> AssrtFormulaFactory
							.AssrtBinBool(AssrtBinBFormula.Op.And, x1, x2));
					// CHECKME: do check, even if AA is True?  To check state var update isn't a contradiction?
					// FIXME: that won't be checked by this, lhs just becomes false -- this should be checked by unsat? (but that is only poly choices)
			if (rhs.equals(AssrtTrueFormula.TRUE))
			{
				return AssrtTrueFormula.TRUE;  // CHECKME: move "shortcircuit" to top?
			}

			// Next for rhs, rename target rec state vars
			List<AssrtIntVar> old = new LinkedList<>(succ.getStateVars().keySet());  // FIXME state var ordering w.r.t. exprs
			List<AssrtIntVarFormula> fresh = old.stream().map(v -> makeFreshIntVar(v))
					.collect(Collectors.toList());
			Iterator<AssrtIntVarFormula> ifresh = fresh.iterator();
			for (AssrtIntVar v : old)
			{
				rhs = rhs.subs(AssrtFormulaFactory.AssrtIntVar(v.toString()),
						ifresh.next());
			}
			
			// Next for rhs, conjunction of eq-terms between renamed succ state vars and action state exprs -- and renamed vars ex-quantified 
			Iterator<AssrtAFormula> iaexprs = aexprs.iterator();
			AssrtBFormula svarsConj = fresh.stream().map(x -> (AssrtBFormula)  // Cast needed
			AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq,
					AssrtFormulaFactory.AssrtIntVar(x.toString()), iaexprs.next()))
					.reduce((x1, x2) -> AssrtFormulaFactory
							.AssrtBinBool(AssrtBinBFormula.Op.And, x1, x2))
					.get();
			rhs = AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.And, rhs,
					svarsConj);
			rhs = AssrtFormulaFactory.AssrtExistsFormula(
					fresh.stream().map(x -> AssrtFormulaFactory.AssrtIntVar(x.toString()))
							.collect(Collectors.toList()),
					rhs);

			AssrtBFormula impli = AssrtFormulaFactory
					.AssrtBinBool(AssrtBinBFormula.Op.Imply, lhs, rhs);
			return forallQuantifyFreeVars(core, fullname, impli).squash();
		}
	}
	
	@Override
	public String toString()
	{
		return "(P=" + this.P + ",\nQ=" 
			+ this.Q.toString().replaceAll("\\\"", "\\\\\"")  // Because of @"..." syntax, need to escape the quotes
			+ ",\nK=" + this.K + ",\nF=" + this.F
			+ ",\nV=" + this.V
			+ ",\nR=" + this.R
			//+ ",\nrename=" + this.rename
			+ ",\nscopes=" + this.scopes  // N.B. this.scopes not included in equals/hashCode
			+ ")";
	}
	
	@Override
	public final int hashCode()
	{
		int hash = 22279;
		hash = 31 * hash + super.hashCode();
		hash = 31 * hash + this.K.hashCode();
		hash = 31 * hash + this.F.hashCode();
		hash = 31 * hash + this.V.hashCode();
		hash = 31 * hash + this.R.hashCode();
		// this.scopes not included
		return hash;
	}

	// Not using id, cf. ModelState -- FIXME? use a factory pattern that associates unique states and ids? -- use id for hash, and make a separate "semantic equals"
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtCoreSConfig))
		{
			return false;
		}
		AssrtCoreSConfig them = (AssrtCoreSConfig) o;
		return super.equals(o) && this.K.equals(them.K) && this.F.equals(them.F)
				&& this.V.equals(them.V) && this.R.equals(them.R);
	}

	@Override
	protected boolean canEquals(Object o)
	{
		return o instanceof AssrtCoreSConfig;
	}
	
	// isActive(SState, Role) becomes isActive(EState)
	public static boolean isActive(EState s, int init)
	{
		return !isInactive(s, init);
	}
	
	private static boolean isInactive(EState s, int init)
	{
		return s.isTerminal()
				|| (s.id == init && s.getStateKind() == EStateKind.ACCEPT);
				// s.isTerminal means non-empty actions (i.e., edges) -- i.e., non-end (cf., fireable)
	}
	

	// CHECKME: factor out into separate classes?  c.f., SingleBuffers for Q

	/*private static void putR(Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R, Role r, AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		/*Set<AssrtDataTypeVar> vs = expr.getVars();
		if (vs.contains(annot))
		{
			//expr = AssrtFormulaFactory.AssrtExistsFormula(Arrays.asList(AssrtFormulaFactory.AssrtIntVar(annot.toString())), expr);
			
			// Substitute var in expr by fresh -- will get forall quantified in sat check -- which is conservative (previous var refinement lost)
			expr = expr.subs(AssrtFormulaFactory.AssrtIntVar(annot.toString()), makeFreshIntVar(annot));
		}* /

		R.get(r).put(annot, expr);
	}*/

	/*private static Map<Role, Map<Role, AssrtCoreEMsg>> copyQ(
			Map<Role, Map<Role, AssrtCoreEMsg>> Q)
	{
		Map<Role, Map<Role, AssrtCoreEMsg>> copy = new HashMap<>();
		for (Role r : Q.keySet())
		{
			copy.put(r, new HashMap<>(Q.get(r)));
		}
		return copy;
	}*/

	private static Map<Role, Set<AssrtIntVar>> copyK(
			Map<Role, Set<AssrtIntVar>> K)
	{
		return K.entrySet().stream().collect(
				Collectors.toMap(Entry::getKey, e -> new HashSet<>(e.getValue())));
	}

	private static Map<Role, Set<AssrtBFormula>> copyF(
			Map<Role, Set<AssrtBFormula>> F)
	{
		return F.entrySet().stream().collect(
				Collectors.toMap(Entry::getKey, e -> new HashSet<>(e.getValue())));
	}

	private static Map<Role, Map<AssrtIntVar, AssrtAFormula>> copyR(
			Map<Role, Map<AssrtIntVar, AssrtAFormula>> R)
	{
		return R.entrySet().stream().collect(
				Collectors.toMap(Entry::getKey, e -> new HashMap<>(e.getValue())));
	}

	private static Map<Role, Set<AssrtBFormula>> copyRass(
			Map<Role, Set<AssrtBFormula>> Rass)
	{
		return Rass.entrySet().stream().collect(
				Collectors.toMap(Entry::getKey, e -> new HashSet<>(e.getValue())));
	}

	private static Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> copyRename(
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename)
	{
		return rename.entrySet().stream().collect(
				Collectors.toMap(Entry::getKey, e -> new HashMap<>(e.getValue())));
	}

	private static Map<Role, LinkedHashMap<Integer, Set<AssrtIntVar>>> copyScopes(
			Map<Role, LinkedHashMap<Integer, Set<AssrtIntVar>>> scopes)
	{
		return scopes.entrySet().stream().collect(Collectors.toMap(Entry::getKey,
				x -> new LinkedHashMap<>(x.getValue())));
	}


	private static AssrtIntVarFormula makeFreshIntVar(AssrtIntVar var)
	{
		return AssrtFormulaFactory.AssrtIntVar("_" + var.toString() + counter++);  // HACK
	}

	private static AssrtBFormula forallQuantifyFreeVars(AssrtCore core,
			GProtoName fullname, AssrtBFormula bform)
	{
		Set<String> rs = core.getContext().getInlined(fullname).roles.stream()
				.map(Object::toString).collect(Collectors.toSet());
		Set<AssrtIntVar> free = bform.getIntVars().stream()
				//.filter(x -> !rs.contains(x.toString()))  // CHECKME: formula role vars -- cf. getUnknownDataVarErrors  // CHECKME: what is an example?
				.collect(Collectors.toSet());
		if (!free.isEmpty())
		{
			bform = AssrtFormulaFactory.AssrtForallFormula(
					free.stream().map(v -> AssrtFormulaFactory.AssrtIntVar(v.toString()))
							.collect(Collectors.toList()),
					bform);
		}
		return bform;
	}





























































	/*private void updateScopes(Role self, AssrtCoreEAction a, EFsm succ, 
			Set<AssrtIntVar> Kself, Set<AssrtBFormula> Fself, 
			LinkedHashMap<Integer, Set<AssrtIntVar>> scopesSelf)
	{
		int curr = this.P.get(self).curr.id;
		/* // Would be an optimisation
		if (succ.curr.id == this.P.get(self).graph.init.id)  // Includes, e.g., mu X.A->B.X
		{
			Kself.clear();
			Fself.clear();  // FIXME TODO: V/R -- e.g., rec assertion
			scopesSelf.clear();
		}
		else* / if (curr == succ.curr.id  // Includes, e.g., mu X.A->B.X (scope always empty), but also A->B.mu X.A->B.X
				|| scopesSelf.keySet().contains(succ.curr.id))
		{
			Set<AssrtIntVar> keep = new HashSet<>();
			Iterator<Entry<Integer, Set<AssrtIntVar>>> es = scopesSelf.entrySet()
					.iterator();
			while (es.hasNext())  // Only keep scope entries up to (and excluding) succ recursion
			{
				Entry<Integer, Set<AssrtIntVar>> next = es.next();
				if (next.getKey() == succ.curr.id)
				{
					es.remove();
					break;
				}
				keep.addAll(next.getValue());
			}
			while (es.hasNext())
			{
				es.next();
				es.remove();
			}
							
			// Up to here, K/F/etc updated for "a" regardless of continue-edge or not -- now treat continue-edges based on scopes
			Kself.retainAll(keep);
			Iterator<AssrtBFormula> i = Fself.iterator();
			while (i.hasNext())
			{
				if (i.next().getIntVars().stream().anyMatch(x -> !keep.contains(x))) 
				{
					i.remove();
				}
			}

			// FIXME TODO: V/R
		}
		else
		{
			scopesSelf.put(curr, ((EAction) a).payload.elems.stream()
					.map(x -> ((AssrtAnnotDataName) x).var).collect(Collectors.toSet()));
			return;
		}
	}
	//*/

	// Mutating 'F' and 'rename'
	// Rename existing vars that have the same name as 'v' -- renaming is basically an implementation of exist-quant (final sat checks implicitly quant over free names)
	// E.g., rec X . A->B: 1(x:int) . X -- loop renames existing x to some fresh name
	// N.B. no "updateRfromF" -- actually, "update R from payload annot" -- leaving R statevars as they are is OK, validation only done from F's and R already incorporated into F (and updates handled by updateFfromR)
	// But would it be more consistent to update R?
	private static void renameOldVarsInF(Role self, AssrtIntVar v, 
			Map<Role, Set<AssrtBFormula>> F)
			//Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename)
	{
		Set<AssrtBFormula> H = F.get(self);
		if (H.stream().anyMatch(x -> x.getIntVars().contains(v)))
		{
			AssrtIntVarFormula old = AssrtFormulaFactory
					.AssrtIntVar(v.toString());
			AssrtIntVarFormula fresh = makeFreshIntVar(v);
			//rename.get(self).put(old, fresh);
			H = H.stream().map(x -> x.subs(old, fresh)).collect(Collectors.toSet());
			F.put(self, H);
		}
	}

	private static void updateForAnnotVar(AssrtIntVar v, Set<AssrtIntVar> Kself)
	{
		addAnnotVarToK(v, Kself);  // Update K
	}

  // CHECKME: only need to update self entries of Maps -- almost: except for addAnnotOpensToF, and some renaming via Streams
	private static void updateForAssertionAndStateExprs(Role self,  // CHECKME: EAction closest base type -- ?
			AssrtBFormula ass, List<AssrtAFormula> aexprs, EFsm succ,  // From an AssrtAnnotDataName pay elem
			Map<Role, Set<AssrtIntVar>> K, 
			Map<Role, Set<AssrtBFormula>> F, 
			Map<Role, Map<AssrtIntVar, AssrtAFormula>> V, 
			Map<Role, Set<AssrtBFormula>> R)
			//Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename)  
	{
		addAnnotOpensToF(ass, F);  // CHECKME HACK?  for port forwarding
		addAssertionToF(ass, F.get(self));

		Map<AssrtIntVar, AssrtAFormula> Vself = V.get(self);  
				// Rename old R vars -- must come before adding new F and R clauses  // CHECKME: not done?

		// "forward" recs will have state vars (svars) but no action state-exprs (aexprs)
		AssrtEState s = (AssrtEState) succ.curr;
		LinkedHashMap<AssrtIntVar, AssrtAFormula> svars = s.getStateVars();
				// aforms = action update exprs for state vars  // CHECKME: svars.size() == aforms.size() ?

		// Following must come after F update (addAnnotBexprToF)
		// Update R from state -- entering a rec "forwards", i.e., not via a continue
		if (aexprs.isEmpty())  // Rec-entry: statevar expr args already inlined into the rec statevars (i.e., by inlining) -- CHECKME
				// CHECKME: means "forwards entry?" robust?  refactor?
		{
			if (!svars.isEmpty())
			{
				updateRecEntry(self, svars, s.getAssertion(), F, Vself, R.get(self));
			}
		}
		else //if (!aforms.isEmpty())
		{
			if (svars.size() != aexprs.size())
			{
				throw new RuntimeException(
						"[assrt-core] Shouldn't get here: " + svars + ", " + aexprs); 
						// CHECKME: not actually syntactically checked yet
			}
			updateRecContinue(self,
					svars.keySet().stream().collect(Collectors.toList()),  // Ordered because LinkedHashMap
					aexprs, 
					F,  Vself);
		}

		compactF(F.get(self));
		//return rename;
	}

	private static void addAnnotOpensToF(AssrtBFormula bform,
			Map<Role, Set<AssrtBFormula>> F)
	{
		Set<AssrtUnintPredicateFormula> preds = Z3Wrapper.getUnintPreds.func
				.apply(bform);  // CHECKME: refactor out of Z3Wrapper
		// CHECKME: unint-pref currrently has to be a top-level clause (assuming CNF), but should generalise
		// CHECKME: factor out API for unint-funs properly
		List<AssrtUnintPredicateFormula> opens = preds.stream()
				.filter(x -> x.name.toString().equals("open"))
				.collect(Collectors.toList());
		for (AssrtUnintPredicateFormula p : opens)
		{
			if (p.args.size() != 2)
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + p);
			}
			//String port = ((AssrtIntVarFormula) i.next()).name;
			Role client = new Role(((AssrtIntVarFormula) p.args.get(1)).name);  // FIXME: port/role values hacked as int var formulas

			appendToF(p, F.get(client));
		}
	}

	private static void addAnnotVarToK(AssrtIntVar v, Set<AssrtIntVar> Kself)
	{
		Kself.add(v);
	}

	private static void addAssertionToF(AssrtBFormula ass,
			Set<AssrtBFormula> Fself)
	{
		appendToF(ass, Fself);  //...record assertions so far -- later error checking: *for all* values that satisify those, it should imply the next assertion
			// CHECKME: filter open from f -- i.e., don't add to sender K
			// Maybe make f CNF? -- https://stackoverflow.com/questions/10992531/convert-formula-to-cnf 
	}

	// Must come after initial F update (addAnnotBexprToF)
	private static void updateRecEntry(Role self,
			LinkedHashMap<AssrtIntVar, AssrtAFormula> svars, AssrtBFormula ass,
			Map<Role, Set<AssrtBFormula>> F,
			Map<AssrtIntVar, AssrtAFormula> Vself, 
			Set<AssrtBFormula> Rself)
	{
		for (Entry<AssrtIntVar, AssrtAFormula> e : svars.entrySet())
		{
			AssrtIntVar svar = e.getKey();
			AssrtAFormula sexpr = e.getValue();  // "Init" state var expr
			
			// CHECKME: record statevar mapping for "direct substitution" modelling special case? (i.e., no "old var" renaming)...
			// ...e.g., x -> x, or x -> y -> x -- i.e. treat subproto statevars more like formal params
			// Anyway, need to check something about new vs. shadowed vs. udapted vs. etc state vars -- currently nothing is checked syntactically
			// "forwards" entry rec should also be handled by action statevar update?
			
			if (!Vself.containsKey(svar))// || !Vself.get(svar).equals(sexpr))  // Optimisation only?
					// CHECKME: need to treat statevars more like roles? i.e., statevar must be explicitly declared/passed to stay "in scope" in the subproto?
			{
				updateVAndFFromStateVar(self, svar, sexpr, F, Vself, true);
				//putK(K, self, k);
			}
		}

		// Cf. addAssertionToF, history recording
		if (!ass.equals(AssrtTrueFormula.TRUE))
		{
			appendToR(ass, Rself);
		}
	}

	// Must come after initial F update (addAnnotBexprToF)
	private static void updateRecContinue(Role self,
			List<AssrtIntVar> svars,
			List<AssrtAFormula> aexprs,			
			Map<Role, Set<AssrtBFormula>> F,
			Map<AssrtIntVar, AssrtAFormula> Vself)
	{
		Iterator<AssrtAFormula> iaexprs = aexprs.iterator();
		for (AssrtIntVar svar : svars)  // FIXME: statevar ordering
		{
			AssrtAFormula aexpr = iaexprs.next();

			/*// CHECKME
			if (expr.getIntVars().contains(svar))  // CHECKME: what is the example?
			{
				// CHECKME: renaming like this OK? -- basically all V vars are being left open for top-level forall
				expr = expr.subs(AssrtFormulaFactory.AssrtIntVar(svar.toString()), 
						//fresh  // No: don't need to "link" V vars and F vars -- only F matters for direct formula checking
						//makeFreshIntVar(annot)  // Makes model construction non-terminating, e.g., mu X(x:=..) ... X<x> -- makes unbounded fresh in x = fresh(x)
						AssrtFormulaFactory.AssrtIntVar("_" + svar.toString())  // CHECKME: is this OK?
				);	
			}*/

			// Update V from action -- recursion back to a rec, via a continue
			AssrtAFormula curr = Vself.get(svar);
			if (!curr.equals(aexpr)  // CHECKME: "syntactic" check is what we want here?
					&& !((aexpr instanceof AssrtIntVarFormula)
							&& ((AssrtIntVarFormula) aexpr).name
									.equals("_" + svar.toString())))
						// Hacky? if expr is just the var occurrence, then value doesn't change
						// FIXME: generalise -- occurences of other vars can be first substituted, before "old var renaming"? -- also for rec-state updates?
			{
				updateVAndFFromStateVar(self, svar, aexpr, F, Vself, false);
			}
		}
	}

	// F part is only a renaming(?)
	private static void updateVAndFFromStateVar(Role self, AssrtIntVar svar,
			AssrtAFormula aform,
			Map<Role, Set<AssrtBFormula>> F,  // Currently renaming creates new Set, so need to replace the entry in F (cf. mutate Fself)
			Map<AssrtIntVar, AssrtAFormula> Vself, 
			boolean forwards)
	{
		if (!forwards)
		{
			/* // CHECKME: what is an example?
			for (AssrtDataVar v : aform.getIntVars())
			{
				AssrtIntVarFormula fresh = AssrtFormulaFactory
						.AssrtIntVar("__" + v.toString());
				aform = aform.subs(AssrtFormulaFactory.AssrtIntVar(v.toString()),
						fresh);
			}*/
		}

		// Must come after initial F update (addAnnotBexprToF)
		Vself.put(svar, aform);   // "Overwrite" (if already known)

		/*
		// CHECKME: what is an example? -- old/new vars due to looping?  __ renaming above?  // CHECKME: if (!forwards)?
		AssrtIntVarFormula old = AssrtFormulaFactory.AssrtIntVar(svar.toString());
		AssrtIntVarFormula fresh = makeFreshIntVar(svar);
		Set<AssrtBFormula> H = F.get(self);
		H = H.stream().map(x -> x.subs(old, fresh)).collect(Collectors.toSet());
		F.put(self, H);*/
	}

	private static void appendToF(AssrtBFormula bform, Set<AssrtBFormula> Fself)
	{
		Fself.add(bform);
	}

	private static void appendToR(AssrtBFormula bform, Set<AssrtBFormula> Rself)
	{
		Rself.add(bform);
	}






}









































/*// \bot
class AssrtCoreEBot extends AssrtCoreEMsg
{
	// N.B. must be initialised *before* ASSSRTCORE_BOT
	private static final Payload ASSRTCORE_EMPTY_PAYLOAD =
			new Payload(
					Arrays.asList(new AssrtAnnotDataName(new AssrtIntVar("_BOT"),
							AssrtCoreGProtoDeclTranslator.UNIT_DATATYPE)));
			// Cf. Payload.EMPTY_PAYLOAD

	public static final AssrtCoreEBot ASSSRTCORE_BOT = new AssrtCoreEBot();

	//public AssrtCoreEBot(EModelFactory ef)
	private AssrtCoreEBot()
	{
		super(null, Role.EMPTY_ROLE, Op.EMPTY_OP, ASSRTCORE_EMPTY_PAYLOAD, AssrtTrueFormula.TRUE,  // null ef OK?
				//AssrtCoreEAction.DUMMY_VAR, AssrtIntValFormula.ZERO);
				Collections.emptyList());
	}
	
	@Override
	public boolean isSend()
	{
		return false;
	}
	
	@Override
	public AssrtCoreERecv toDual(Role self)
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}

	@Override
	public AssrtCoreSSend toGlobal(Role self)
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}
	
	@Override
	public String toString()
	{
		return "BOT";
	} 

	@Override
	public int hashCode()
	{
		int hash = 2273;
		hash = 31 * hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreEBot))
		{
			return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public boolean canEquals(Object o)  // FIXME: rename canEquals
	{
		return o instanceof AssrtCoreEBot;
	}
}

// <a> -- TODO: for open/port annotations
class AssrtCoreEPendingRequest extends AssrtCoreEMsg
{
	public static final Payload ASSRTCORE_EMPTY_PAYLOAD =
			new Payload(
					Arrays.asList(new AssrtAnnotDataName(new AssrtIntVar("_BOT"),
							AssrtCoreGProtoDeclTranslator.UNIT_DATATYPE)));
			// Cf. Payload.EMPTY_PAYLOAD
	
	private final AssrtCoreEReq msg;  // Not included in equals/hashCode

	//public AssrtCoreEPendingConnection(AssrtEModelFactory ef, Role r, MsgId<?> op, Payload pay, AssrtBoolFormula ass)
	public AssrtCoreEPendingRequest(AssrtCoreEReq msg,
			Map<AssrtIntVarFormula, AssrtIntVarFormula> shadow)
	{
		super(null, msg.peer, msg.mid, msg.payload, msg.ass,  // HACK: null ef OK?  cannot access es.ef
				//AssrtCoreEAction.DUMMY_VAR, AssrtIntValFormula.ZERO,
				Collections.emptyList(),
				shadow);
		this.msg = msg;
	}
	
	public AssrtCoreEReq getMsg()
	{
		return this.msg;
	}
	
	@Override
	public boolean isSend()
	{
		return false;
	}
	
	@Override
	public AssrtCoreERecv toDual(Role self)
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}

	@Override
	public AssrtCoreSSend toGlobal(Role self)
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}
	
	@Override
	public String toString()
	{
		return "<" + super.toString() + ">";
	} 

	@Override
	public int hashCode()
	{
		int hash = 6091;
		hash = 31 * hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof AssrtCoreEPendingRequest))
		{
			return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public boolean canEquals(Object o)  // FIXME: rename canEquals
	{
		return o instanceof AssrtCoreEPendingRequest;
	}
}
//*/

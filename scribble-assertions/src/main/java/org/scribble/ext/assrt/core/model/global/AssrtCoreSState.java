package org.scribble.ext.assrt.core.model.global;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.core.ast.global.AssrtCoreGProtocolDeclTranslator;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEReceive;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEAccept;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEAction;
import org.scribble.ext.assrt.model.endpoint.action.AssrtERequest;
import org.scribble.ext.assrt.model.endpoint.action.AssrtESend;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBinBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtBinCompFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.scribble.main.Job;
import org.scribble.model.MPrettyState;
import org.scribble.model.MState;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.EStateKind;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.global.SModelFactory;
import org.scribble.model.global.actions.SAction;
import org.scribble.type.Payload;
import org.scribble.type.kind.Global;
import org.scribble.type.name.Op;
import org.scribble.type.name.PayloadElemType;
import org.scribble.type.name.Role;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

public class AssrtCoreSState extends MPrettyState<Void, SAction, AssrtCoreSState, Global>
{
	private final Set<Role> subjs = new HashSet<>();  // Hacky: mostly because EState has no self -- for progress checking
	
	// In hash/equals -- cf. SState.config
	private final Map<Role, AssrtEState> P;          
	private final Map<Role, Map<Role, AssrtCoreESend>> Q;  // null value means connected and empty -- dest -> src -> msg
	private final Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R;  

	// *Not* in hash/equals -- HACK too hacky?
	private final Map<Role, Set<AssrtDataTypeVar>> K;
	private final Map<Role, AssrtBoolFormula> F; 

	public AssrtCoreSState(Map<Role, AssrtEState> P, boolean explicit)
	{
		this(P, makeQ(P.keySet(), explicit),
				//new HashSet<>(),
				makeR(P),
				makeK(P.keySet()),
				makeF(P.keySet()));
	}

	// Pre: non-aliased "ownership" of all Map contents
	protected AssrtCoreSState(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreESend>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, AssrtBoolFormula> F)
	{
		super(Collections.emptySet());
		this.P = Collections.unmodifiableMap(P);
		this.Q = Collections.unmodifiableMap(Q);  // Don't need copyQ, etc. -- should already be fully "owned"
		this.R = Collections.unmodifiableMap(R);

		this.K = Collections.unmodifiableMap(K);
		//this.F = Collections.unmodifiableSet(F);
		this.F = Collections.unmodifiableMap(F);
	}

	// Need to consider hasPendingRequest? -- no: the semantics blocks both sides until connected, so don't need to validate those "intermediate" states
	public boolean isReceptionError()
	{
		return this.P.entrySet().stream().anyMatch(e ->  // e: Entry<Role, EState>
				{
					EState s = e.getValue();
					EStateKind k = s.getStateKind();
					if (k != EStateKind.UNARY_INPUT && k != EStateKind.POLY_INPUT)
					{
						return false;
					}
					Role dest = e.getKey();
					List<EAction> as = s.getActions();
					Role src = as.get(0).peer;
					return hasMessage(dest, src) && 
							//!as.contains(this.Q.get(dest).get(src).toDual(src));
							as.stream()
								.map(a -> ((AssrtESend) a.toDual(dest)).toTrueAssertion())
								.noneMatch(a -> a.equals(this.Q.get(dest).get(src).toTrueAssertion()));   // cf. toTrueAssertion done now only on receiver side
										// HACK FIXME: check assertion implication (not just syntactic equals) -- cf. AssrtSConfig::fire
				}
		);
	}

	// Includes orphan pending requests -- maybe shouldn't?  handled by synchronisation error?
	public boolean isOrphanError(Map<Role, EState> E0)
	{
		return this.P.entrySet().stream().anyMatch(e ->
				{
					Role r1 = e.getKey();
					EState s = e.getValue();
					return
							   isInactive(s, E0.get(r1).id)
							&& (this.P.keySet().stream().anyMatch(r2 -> 
									   hasMessage(r1, r2)
									 
									// FIXME: factor out as "pending request reception error"? -- actually, already checked as synchronisation error?
									|| (  isPendingRequest(r2, r1)  // N.B. pending request *to* inactive r1 
											 
									   // Otherwise all initial request messages considered as bad
									   && s.getActions().stream()
											   .map(a -> ((AssrtERequest) a.toDual(r1)).toTrueAssertion())
											   .noneMatch(a -> a.equals(((AssrtCoreEPendingRequest) this.Q.get(r1).get(r2)).getMessage()))  
													 
									   )
								 ));  
								////|| !this.owned.get(e.getKey()).isEmpty()  
								
					// FIXME: need AnnotEConnect to consume owned properly
				}
			);
	}

	public boolean isUnfinishedRoleError(Map<Role, EState> E0)
	{
		return this.isTerminal() &&
				this.P.entrySet().stream().anyMatch(e -> isActive(e.getValue(), E0.get(e.getKey()).id));
	}

	// Request/accept are bad if local queue is established
	// N.B. request/accept when remote queue is established is not an error -- relying on semantics to block until both remote/local queues not established; i.e., error precluded by design of model, not by validation
	// N.B. not considering pending requests, for the same reason as above and as why not considered for, e.g., reception errors -- i.e., not validating those "intermediate" states
	// Deadlock/progress errors that could be related to "decoupled" connection sync still caught by existing checks, e.g., orphan message or unfinished role
	public boolean isConnectionError()
	{
		return this.P.entrySet().stream().anyMatch(e -> 
				e.getValue().getActions().stream().anyMatch(a ->
						(a.isRequest() || a.isAccept()) && isInputQueueEstablished(e.getKey(), a.peer)
		));
				// FIXME: check for pending port, if so then port is used -- need to extend an AnnotEConnect type with ScribAnnot (cf. AnnotPayloadType)
	}

	// Send is bad if either local queue or remote queue is not established, and no pending request to the target
	// Receive is bad if local queue is not established and no pending request to the target
	public boolean isUnconnectedError()
	{
		return this.P.entrySet().stream().anyMatch(e -> 
				{
					Role r = e.getKey();
					return 
							e.getValue().getActions().stream().anyMatch(a ->
											(a.isSend() &&
												(!isInputQueueEstablished(r, a.peer) || !isInputQueueEstablished(a.peer, r)) && !isPendingRequest(r, a.peer))
									|| (a.isReceive() && !isInputQueueEstablished(r, a.peer) && !isPendingRequest(r, a.peer)));
					// Don't need to use isPendingRequest(a.peer, r) because only requestor is "at the next state" while request pending 
				});
	}

	// "Connection message" reception error
	public boolean isSynchronisationError()
	{
		return this.P.entrySet().stream().anyMatch(e ->  // e: Entry<Role, EState>
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
										 ((AssrtCoreEPendingRequest) this.Q.get(dest).get(src)).getMessage()
										.toTrueAssertion().toDual(src)
									);
				}
		);
	}
	
	public boolean isUnknownDataTypeVarError()
	{
		return this.P.entrySet().stream().anyMatch(e ->
				e.getValue().getAllActions().stream().anyMatch(a -> 
				{
					if (a instanceof AssrtESend)
					{
						Role src = e.getKey();

						Set<AssrtDataTypeVar> known = new HashSet<>(this.K.get(src));
						((AssrtESend) a).payload.elems.forEach(pe ->  // Currently exactly one elem
						{
							if (pe instanceof AssrtAnnotDataType)
							{
								known.add(((AssrtAnnotDataType) pe).var);
							}
							else
							{
								System.err.println("[assrt-core] Shouldn't get in here: " + pe);  // FIXME: runtime exception
							}
						});

						known.addAll(this.R.get(src).keySet());

						return ((AssrtESend) a).ass.getVars().stream().anyMatch(v -> !known.contains(v));
					}
					else
					{
						// FIXME: receive assertions
					}
					return false;
				}));
	}
	
	// i.e., has an action with an unsatisfiable assertion given existing assertions
	public boolean isUnsatisfiableError(Job job)
	{
		return this.P.entrySet().stream().anyMatch(e ->
				e.getValue().getAllActions().stream().anyMatch(a ->  // N.B. getAllActions includes non-fireable
				{
					if (a instanceof AssrtESend)
					{
						Role src = e.getKey();

						JavaSmtWrapper jsmt = JavaSmtWrapper.getInstance();
						AssrtBoolFormula ass = ((AssrtESend) a).ass;
						if (ass.equals(AssrtTrueFormula.TRUE))
						{
							return false;
						}

						/*AssrtBoolFormula tmp = this.F.get(src).stream().reduce(
									AssrtTrueFormula.TRUE,  // F emptyset at start
									(b1, b2) -> AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, b1, b2)
								);*/
						AssrtBoolFormula tmp = this.F.get(src);
						BooleanFormula PP = tmp.getJavaSmtFormula();
						
						BooleanFormula AA = ass.getJavaSmtFormula();
						Set<IntegerFormula> varsA = new HashSet<>();
						varsA.add(jsmt.ifm.makeVariable(((AssrtAnnotDataType) a.payload.elems.get(0)).var.toString()));  
								// Adding even if var not used
								// N.B. includes the case for recursion cycles where var is "already" in F
						if (!varsA.isEmpty())  // FIXME: now never empty
						{
							AA = jsmt.qfm.exists(new LinkedList<>(varsA), AA);
						}
						
						Set<Entry<AssrtDataTypeVar, AssrtArithFormula>> bar = new HashSet<>();

						/*for (Role r : this.R.keySet())
						{
							bar.addAll(this.R.get(r).entrySet());  // Big conjunction -- including different exprs for the same var between roles?
						}*/
						bar.addAll(this.R.get(src).entrySet());

						Set<AssrtBoolFormula> foo = bar.stream().map(b -> AssrtFormulaFactory.AssrtBinComp(
									AssrtBinCompFormula.Op.Eq, 
									AssrtFormulaFactory.AssrtIntVar(b.getKey().toString()),
									b.getValue()
								)).collect(Collectors.toSet());
						BooleanFormula RR = foo.stream().reduce(
									AssrtTrueFormula.TRUE,  // F emptyset at start
									(b1, b2) -> AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, b1, b2)
								).getJavaSmtFormula();
						
						Set<AssrtDataTypeVar> r1 = new HashSet<>(this.R.get(src).keySet());
						Set<AssrtDataTypeVar> f1 = new HashSet<>(this.F.get(src).getVars());
						f1.retainAll(r1);
						if (!f1.isEmpty())
						{
							// Simply exists quantifying all vars in F that are also in R -- is this OK?  just means we always use the "latest" R? (could be the same as before)
							PP = jsmt.qfm.exists(f1.stream().map(v -> jsmt.ifm.makeVariable(v.toString())).collect(Collectors.toList()), PP);
						}

						BooleanFormula impli = jsmt.bfm.implication(jsmt.bfm.and(PP, RR), AA);
						//BooleanFormula impli = jsmt.bfm.and(jsmt.bfm.and(PP, RR), AA);  // HACK FIXME

						/*Set<IntegerFormula> varsF = this.F.get(src).stream().flatMap(f -> f.getVars().stream()
								.map(v -> jsmt.ifm.makeVariable(v.toString()))).collect(Collectors.toSet());*/
						Set<IntegerFormula> varsF = this.F.get(src).getVars().stream()
								.map(v -> jsmt.ifm.makeVariable(v.toString())).collect(Collectors.toSet());
						/*Set<IntegerFormula> varsA = ass.getVars().stream()
								.map(v -> jsmt.ifm.makeVariable(v.toString())).collect(Collectors.toSet());
						varsA.removeAll(varsF);*/  // No: the only difference should be single action pay var, and always want to exists quantify it (not only if not F, e.g., recursion)
						Set<IntegerFormula> varsR = this.R.values().stream().flatMap(m -> m.keySet().stream()).map(v -> 
									jsmt.ifm.makeVariable(v.toString())).collect(Collectors.toSet()
								);
						varsR.addAll(
								this.R.values().stream().flatMap(m -> m.values().stream()).flatMap(v -> 
									v.getVars().stream()).map(v -> jsmt.ifm.makeVariable(v.toString())).collect(Collectors.toSet())
								);
						
						Set<IntegerFormula> varsK = this.K.values().stream().flatMap(s -> s.stream())
								.map(v -> jsmt.ifm.makeVariable(v.toString())).collect(Collectors.toSet());
						
						Set<IntegerFormula> free = new HashSet<>();
						free.addAll(varsF);
						free.addAll(varsR);
						free.addAll(varsK);
						
						//System.out.println("aaa: " + PP + ", " + RR);
						//System.out.println("bbb: " + varsF + ", " + varsR + ", " + ", " + varsK + free);
						if (!free.isEmpty())
						{
							impli = jsmt.qfm.forall(new LinkedList<>(free), impli);  // AA already exists-quantified
						}
						
						job.debugPrintln("\n[assrt-core] Checking satisfiability for " + src + " at " + e.getValue() + "(" + this.id + "): " + impli);
							
						if (!jsmt.isSat(impli))
						{
							return true;
						}
					}
					else
					{
						// FIXME: receive assertions
					}
					return false;
				}));
	}

	// FIXME: List<AssrtCoreEAction> -- after also doing assert-core request/accept
	public Map<Role, List<EAction>> getFireable()
	{
		Map<Role, List<EAction>> res = new HashMap<>();
		for (Entry<Role, AssrtEState> e : this.P.entrySet())
		{
			Role self = e.getKey();
			EState s = e.getValue();
			res.put(self, new LinkedList<>());
			for (EAction a : s.getActions())
			{
				if (a.isSend())
				{
					AssrtCoreESend es = (AssrtCoreESend) a;
					getSendFireable(res, self, es);
				}
				else if (a.isReceive())
				{
					AssrtCoreEReceive er = (AssrtCoreEReceive) a;
					getReceiveFireable(res, self, er);
				}
				else if (a.isRequest())
				{
					AssrtERequest ec = (AssrtERequest) a;  // FIXME: core
					getRequestFireable(res, self, ec);
				}
				else if (a.isAccept())
				{
					AssrtEAccept ea = (AssrtEAccept) a;  // FIXME: core
					getAcceptFireable(res, self, ea);
				}
				/*else if (a.isDisconnect())
				{
					EDisconnect ld = (EDisconnect) a;
					getDisconnectFireable(res, self, ld);
				}*/
				else
				{
					throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);
				}
			}
		}
		return res;
	}

	private void getSendFireable(Map<Role, List<EAction>> res, Role self, AssrtESend es)
	{
		if (hasPendingRequest(self) || !isInputQueueEstablished(self, es.peer) || hasMessage(es.peer, self))
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
		/*for (PayloadElemType<?> pt : (Iterable<PayloadElemType<?>>) 
				es.payload.elems.stream().filter(x -> x instanceof AssrtPayloadElemType<?>)::iterator)*/
		PayloadElemType<?> pt = es.payload.elems.get(0);  // assrt-core is hardcoded to one payload elem (empty source payload is filled in)
		{
			if (pt instanceof AssrtAnnotDataType)
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
	}

	private void getReceiveFireable(Map<Role, List<EAction>> res, Role self, EReceive er)
	{
		if (hasPendingRequest(self) || !hasMessage(self, er.peer))
		{
			return;
		}

		AssrtESend m = this.Q.get(self).get(er.peer);
		//if (er.toDual(self).equals(m))  //&& !(m instanceof F17EBot)
		if (((AssrtESend) er.toDual(self)).toTrueAssertion().equals(m.toTrueAssertion()))  
				// HACK FIXME: check assertion implication (not just syntactic equals) -- cf. AssrtSConfig::fire
		{
			res.get(self).add(er);
		}
	}

	private void getRequestFireable(Map<Role, List<EAction>> res, Role self, AssrtERequest es)
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
		PayloadElemType<?> pt = es.payload.elems.get(0);  // assrt-core is hardcoded to one payload elem (empty source payload is filled in)
		{
			if (pt instanceof AssrtAnnotDataType)
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
	}

	// Based on getReceiveFireable
	private void getAcceptFireable(Map<Role, List<EAction>> res, Role self, AssrtEAccept ea)
	{
		if (hasPendingRequest(self) || !isPendingRequest(ea.peer, self))
		{
			return;
		}

		AssrtERequest ec = ((AssrtCoreEPendingRequest) this.Q.get(self).get(ea.peer)).getMessage();
		//if (ea.toDual(self).equals(ec))
		if (((AssrtERequest) ea.toDual(self)).toTrueAssertion().equals(ec.toTrueAssertion()))  
				// HACK FIXME: check assertion implication (not just syntactic equals) -- cf. getReceiveFireable
		{
			res.get(self).add(ea);
		}
	}

	/*private void getDisconnectFireable(Map<Role, List<EAction>> res, Role self, EDisconnect ld)
	{
		if (!(this.Q.get(self).get(ld.peer) instanceof F17EBot)  // FIXME: isConnected
				&& this.Q.get(self).get(ld.peer) == null)
		{
			res.get(self).add(ld);
		}
	}*/
	
	// Pre: getFireable().get(self).contains(a)
	public AssrtCoreSState fire(Role self, EAction a)  // Deterministic
	{
		Map<Role, AssrtEState> P = new HashMap<>(this.P);
		Map<Role, Map<Role, AssrtCoreESend>> Q = AssrtCoreSState.copyQ(this.Q);
		Map<Role, Set<AssrtDataTypeVar>> K = AssrtCoreSState.copyK(this.K);
		Map<Role, AssrtBoolFormula> F = AssrtCoreSState.copyF(this.F);
		//Set<AssrtBoolFormula> F = new HashSet<>(this.F);
		Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R = AssrtCoreSState.copyR(this.R);

		AssrtEState succ = P.get(self).getSuccessor(a);
		R.get(self).putAll(succ.getAnnotVars());  // Should "overwrite" previous var values

		if (a.isSend())
		{
			fireSend(P, Q, R, K, F, self, (AssrtCoreESend) a, succ);
		}
		else if (a.isReceive())
		{
			fireReceive(P, Q, R, K, F, self, (AssrtCoreEReceive) a, succ);
		}
		else if (a.isRequest())
		{
			fireRequest(P, Q, K, F, self, (AssrtERequest) a, succ);  // FIXME: core
		}
		else if (a.isAccept())
		{
			fireAccept(P, Q, K, F, self, (AssrtEAccept) a, succ);  // FIXME: core
		}
		/*else if (a.isDisconnect())
		{
			EDisconnect ld = (EDisconnect) a;
			P.put(self, succ);
			Q.get(self).put(ld.peer, BOT);
		}*/
		else
		{
			throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);
		}
		return new AssrtCoreSState(P, Q, R, K, F);
	}

	// Update (in place) P, Q, R, K and F
	private void fireSend(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreESend>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, AssrtBoolFormula> F,
			Role self, AssrtCoreESend es, AssrtEState succ)
	{
		P.put(self, succ);
		//Q.get(es.peer).put(self, es.toTrueAssertion());  // HACK FIXME: cf. AssrtSConfig::fire
		Q.get(es.peer).put(self, es);  // Now doing toTrueAssertion on message at receive side
		//putR(R, self, es.annot, es.expr);

		outputUpdateKF(R, K, F, self, es);

		// Must come after F update
		if (!es.annot.equals(AssrtCoreESend.DUMMY_VAR))  // FIXME
		{
			R.get(self).put(es.annot, es.expr);
			
			/*AssrtBoolFormula tmp = F.get(self);
			Set<AssrtDataTypeVar> varsF = tmp.getVars();
			if (varsF.contains(es.annot))
			{
				//F.put(self, AssrtFormulaFactory.AssrtExistsFormula(Arrays.asList(AssrtFormulaFactory.AssrtIntVar(es.annot.toString())), tmp));
				//putF(F, self, AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq, AssrtFormulaFactory.AssrtIntVar(es.annot.toString()), es.expr));
			}*/
		}

		/*Map<AssrtDataTypeVar, AssrtArithFormula> m = this.R.get(self);
		AssrtDataTypeVar next = m.keySet().iterator().next();
		if (!next.equals(AssrtCoreESend.DUMMY_VAR))
		{
			putF(F, self, AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq, 
					AssrtFormulaFactory.AssrtIntVar(next.toString()), m.values().iterator().next()));
		}*/
	}

	private static void fireReceive(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreESend>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R, 
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, AssrtBoolFormula> F,   // FIXME: manage F with receive assertions?
			Role self, AssrtCoreEReceive er, AssrtEState succ)
	{
		P.put(self, succ);
		AssrtESend m = Q.get(self).put(er.peer, null);  // null is \epsilon
		
		//inputUpdateK(K,  self, er);
		inputUpdateKF(R, K, F, self, er, m);

		// Must come after F update
		if (!er.annot.equals(AssrtCoreESend.DUMMY_VAR))  // FIXME
		{
			//putR(R, self, er.annot, er.expr);
			R.get(self).put(er.annot, er.expr);

			/*AssrtBoolFormula tmp = F.get(self);
			Set<AssrtDataTypeVar> varsF = tmp.getVars();
			if (varsF.contains(er.annot))* /
			{
				//F.put(self, AssrtFormulaFactory.AssrtExistsFormula(Arrays.asList(AssrtFormulaFactory.AssrtIntVar(er.annot.toString())), tmp));
				//putF(F, self, AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq, AssrtFormulaFactory.AssrtIntVar(er.annot.toString()), er.expr));
			}*/
		}

		/*Map<AssrtDataTypeVar, AssrtArithFormula> foo = this.R.get(self);
		AssrtDataTypeVar next = foo.keySet().iterator().next();
		if (!next.equals(AssrtCoreESend.DUMMY_VAR))
		{
			putF(F, self, AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq, 
					AssrtFormulaFactory.AssrtIntVar(next.toString()), foo.values().iterator().next()));
		}*/
	}

	// FIXME: R
	private static void fireRequest(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreESend>> Q,
			//Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, AssrtBoolFormula> F,
			Role self, AssrtERequest es, AssrtEState succ)
	{
		P.put(self, succ);
		//Q.get(es.peer).put(self, new AssrtCoreEPendingRequest(es.toTrueAssertion()));  // HACK FIXME: cf. AssrtSConfig::fire
		Q.get(es.peer).put(self, new AssrtCoreEPendingRequest(es));  // Now doing toTrueAssertion on accept side

		outputUpdateKF(null, K, F, self, es);
	}

	// FIXME: R
	private static void fireAccept(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreESend>> Q,
			//Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, AssrtBoolFormula> F, 
			Role self, AssrtEAccept ea, AssrtEState succ)
	{
		P.put(self, succ);
		Q.get(self).put(ea.peer, null);

		AssrtERequest m = ((AssrtCoreEPendingRequest) Q.get(ea.peer).put(self, null)).getMessage();
		inputUpdateKF(null, K, F, self, ea, m);
		//outputUpdateKF(K, F, self, ea);
	}

	private static void outputUpdateKF(Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R, Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, AssrtBoolFormula> F, Role self, AssrtEAction a)  // FIXME: EAction closest base type
	{
		/*for (PayloadElemType<?> pt : (Iterable<PayloadElemType<?>>) 
					(a.payload.elems.stream().filter(x -> x instanceof AssrtPayloadElemType<?>))::iterator)*/
		PayloadElemType<?> pt = ((EAction) a).payload.elems.get(0);
		{
			if (pt instanceof AssrtAnnotDataType)
			{
				// Update K
				AssrtDataTypeVar v = ((AssrtAnnotDataType) pt).var;
				putK(K, self, v);
				
				//...record assertions so far -- later error checking: *for all* values that satisify those, it should imply the next assertion
				//putF(F, v, es.bf);
				putF(R, F, self, a.getAssertion());  // Recorded "globally", cf. async K updates -- not any more
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);  
						// Regular DataType pay elems have been given fresh annot vars (AssrtCoreGProtocolDeclTranslator.parsePayload) -- no other pay elems allowed
			}
		}
	}

	// a is the EFSM input action, which has True ass; m is the dequeued msg, which carries the output ass
	private static void inputUpdateKF(Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R, Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, AssrtBoolFormula> F, Role self, AssrtEAction a, AssrtEAction m)  // FIXME: EAction closest base type
	{
		/*for (PayloadElemType<?> pt : (Iterable<PayloadElemType<?>>) 
					(a.payload.elems.stream().filter(x -> x instanceof AssrtPayloadElemType<?>))::iterator)*/
		PayloadElemType<?> pt = ((EAction) a).payload.elems.get(0);
		{
			if (pt instanceof AssrtAnnotDataType)
			{
				// Update K
				AssrtDataTypeVar v = ((AssrtAnnotDataType) pt).var;
				putK(K, self, v);

				putF(R, F, self, m.getAssertion());
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);  
						// Regular DataType pay elems have been given fresh annot vars (AssrtCoreGProtocolDeclTranslator.parsePayload) -- no other pay elems allowed
			}
		}
	}

	private boolean hasMessage(Role self, Role peer)
	{
		return isInputQueueEstablished(self, peer)  // input queue is established (not \bot and not <a>)
				&& this.Q.get(self).get(peer) != null;  // input queue is not empty
	}
	
	// Direction sensitive (not symmetric) -- isConnected means dest has established input queue from src
	// i.e. "fully" established, not "pending" -- semantics relies on all action firing being guarded on !hasPendingConnect
	private boolean isInputQueueEstablished(Role dest, Role src)  // N.B. is more like the "input buffer" at r1 for r2 -- not the actual "connection from r1 to r2"
	{
		AssrtESend es = this.Q.get(dest).get(src);
		return !(es instanceof AssrtCoreEBot) && !(es instanceof AssrtCoreEPendingRequest);
		//return es != null && es.equals(AssrtCoreEBot.ASSSRTCORE_BOT);  // Would be same as above
	}

	// req waiting for acc -- cf. reverse direction to isConnect
	private boolean isPendingRequest(Role req, Role acc)  // FIXME: for open/port annotations
	{
		//return (this.ports.get(r1).get(r2) != null) || (this.ports.get(r2).get(r1) != null);
		AssrtESend es = this.Q.get(acc).get(req);  // N.B. reverse direction to isConnected
		return es instanceof AssrtCoreEPendingRequest;
	}

	private boolean hasPendingRequest(Role req)
	{
		return this.Q.keySet().stream().anyMatch(acc -> isPendingRequest(req, acc));
	}

	public Map<Role, AssrtEState> getP()
	{
		return this.P;
	}
	
	public Map<Role, Map<Role, AssrtCoreESend>> getQ()
	{
		return this.Q;
	}
	
	public Set<Role> getSubjects()
	{
		return Collections.unmodifiableSet(this.subjs);
	}
	
	public void addSubject(Role subj)
	{
		this.subjs.add(subj);
	}
	
	@Override
	protected String getNodeLabel()
	{
		String lab = "(P=" + this.P + ",\nQ=" + this.Q + ",\nR=" + this.R + ",\nK=" + this.K + ",\nF=" + this.F + ")";
		//return "label=\"" + this.id + ":" + lab.substring(1, lab.length() - 1) + "\"";
		return "label=\"" + this.id + ":" + lab + "\"";
	}

	@Override
	public void addEdge(SAction a, AssrtCoreSState s)  // Visibility hack (for AssrtCoreSModelBuilder::build)
	{
		super.addEdge(a, s);
	}
	
	@Override
	public final int hashCode()
	{
		int hash = 79;
		hash = 31 * hash + this.P.hashCode();
		hash = 31 * hash + this.Q.hashCode();
		hash = 31 * hash + this.R.hashCode();
		/*hash = 31 * hash + this.K.hashCode();  // HACK
		hash = 31 * hash + this.F.hashCode();*/
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
		if (!(o instanceof AssrtCoreSState))
		{
			return false;
		}
		AssrtCoreSState them = (AssrtCoreSState) o;
		return them.canEquals(this) && this.P.equals(them.P) && this.Q.equals(them.Q) && this.R.equals(them.R);
				//&& this.K.equals(them.K) && this.F.equals(them.F);
	}

	@Override
	protected boolean canEquals(MState<?, ?, ?, ?> s)
	{
		return s instanceof AssrtCoreSState;
	}
	
	// isActive(SState, Role) becomes isActive(EState)
	public static boolean isActive(EState s, int init)
	{
		return !isInactive(s, init);
	}
	
	private static boolean isInactive(EState s, int init)
	{
		return s.isTerminal() || (s.id == init && s.getStateKind() == EStateKind.ACCEPT);
				// s.isTerminal means non-empty actions (i.e., edges) -- i.e., non-end (cf., fireable)
	}
	
	
	// FIXME: factor out into separate classes
	
	private static Map<Role, Map<Role, AssrtCoreESend>> makeQ(Set<Role> rs, boolean explicit)
	{
		AssrtCoreESend init = explicit ? AssrtCoreEBot.ASSSRTCORE_BOT : null;
		Map<Role, Map<Role, AssrtCoreESend>> res = new HashMap<>();
		for (Role r1 : rs)
		{
			HashMap<Role, AssrtCoreESend> tmp = new HashMap<>();
			for (Role r2 : rs)
			{
				if (!r2.equals(r1))
				{
					tmp.put(r2, init);
				}
			}
			res.put(r1, tmp);
		}
		return res;
	}
	
	private static Map<Role, Map<Role, AssrtCoreESend>> copyQ(Map<Role, Map<Role, AssrtCoreESend>> Q)
	{
		Map<Role, Map<Role, AssrtCoreESend>> copy = new HashMap<>();
		for (Role r : Q.keySet())
		{
			copy.put(r, new HashMap<>(Q.get(r)));
		}
		return copy;
	}

	private static Map<Role, Set<AssrtDataTypeVar>> makeK(Set<Role> rs)
	{
		return rs.stream().collect(Collectors.toMap(r -> r, r -> new HashSet<>()));
	}

	private static Map<Role, Set<AssrtDataTypeVar>> copyK(Map<Role, Set<AssrtDataTypeVar>> K)
	{
		return K.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> new HashSet<>(e.getValue())));
	}
	
	private static void putK(Map<Role, Set<AssrtDataTypeVar>> K, Role r, AssrtDataTypeVar v)
	{
		Set<AssrtDataTypeVar> tmp = K.get(r);
		/*if (tmp == null)  // No: makeK already made all Sets -- cf. makeQ, and no putQ
		{
			tmp = new HashSet<>();
			K.put(r, tmp);
		}*/
		tmp.add(v);
	}

	private static Map<Role, AssrtBoolFormula> makeF(Set<Role> rs)
	{
		return rs.stream().collect(Collectors.toMap(r -> r, r -> AssrtTrueFormula.TRUE));
	}

	private static Map<Role, AssrtBoolFormula> copyF(Map<Role, AssrtBoolFormula> F)
	{
		return F.entrySet().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	private static void putF(Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R, Map<Role, AssrtBoolFormula> F, Role r, AssrtBoolFormula f)
	{
		AssrtBoolFormula curr = F.get(r);
		
		Set<AssrtDataTypeVar> foo = f.getVars();
		Map<AssrtDataTypeVar, AssrtArithFormula> bar = R.get(r);
		if (bar.keySet().stream().anyMatch(v -> foo.contains(v)))
		{
			// FIXME: conservative
			curr = AssrtFormulaFactory.AssrtExistsFormula(
					bar.keySet().stream().map(v -> AssrtFormulaFactory.AssrtIntVar(v.toString())).collect(Collectors.toList()),
					curr);
			
			// FIXME: conservative
			f = AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, f, 
						bar.entrySet().stream()
							.map(e ->
									AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq,
										AssrtFormulaFactory.AssrtIntVar(e.getKey().toString()),
										e.getValue()))
							.reduce((AssrtBoolFormula) AssrtTrueFormula.TRUE,  // Cast needed?
									(b, c) -> AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, b, c),
									(b1, b2) -> AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, b1, b2))
					);
		}
		
		AssrtBinBoolFormula next = AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, curr, f);

		// HACK FIXME
		JavaSmtWrapper jsmt = JavaSmtWrapper.getInstance();
		BooleanFormula impli = jsmt.bfm.implication(curr.getJavaSmtFormula(), next.getJavaSmtFormula());
		Set<AssrtDataTypeVar> vars = new HashSet<>();
		vars.addAll(curr.getVars());
		vars.addAll(next.getVars());
		List<IntegerFormula> vs = vars.stream().map(v -> jsmt.ifm.makeVariable(v.toString())).collect(Collectors.toList());
		if (!vs.isEmpty())
		{
			impli = jsmt.qfm.forall(vs, impli);  // FIXME: just exist quantify every "step"? -- cf. DbC? -- no: only on receives and state updates
		}
		
		System.out.println("\n[assrt-core] F update checking: " + impli);
		
		if (jsmt.isSat(impli))// && !tmp.equals(AssrtTrueFormula.TRUE))
		{
			F.put(r,  curr);
		}
		else
		{
			F.put(r, next);
		}
	}

	private static Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> makeR(Map<Role, AssrtEState> P)
	{
		/*Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R = P.keySet().stream().collect(Collectors.toMap(r -> r, r -> new HashMap<>()));
		P.entrySet().stream().forEach(e -> R.get(e.getKey()).putAll(e.getValue().getAnnotVars()));*/
		Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R = P.entrySet().stream().collect(Collectors.toMap(
				e -> e.getKey(),
				e -> new HashMap<>(e.getValue().getAnnotVars())
		));
		return R;
	}
	
	private static Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> copyR(Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R)
	{
		return R.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> new HashMap<>(e.getValue())));
	}
	
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
	}
	
	/*private static AssrtIntVarFormula makeFreshIntVar(AssrtDataTypeVar var)
	{
		return AssrtFormulaFactory.AssrtIntVar("_" + var.toString());  // HACK
	}*/
}


// \bot
class AssrtCoreEBot extends AssrtCoreESend
{
	// N.B. must be initialised *before* ASSSRTCORE_BOT
	private static final Payload ASSRTCORE_EMPTY_PAYLOAD =
			new Payload(Arrays.asList(new AssrtAnnotDataType(new AssrtDataTypeVar("_BOT"), AssrtCoreGProtocolDeclTranslator.UNIT_DATATYPE)));
			// Cf. Payload.EMPTY_PAYLOAD

	public static final AssrtCoreEBot ASSSRTCORE_BOT = new AssrtCoreEBot();


	//public AssrtCoreEBot(EModelFactory ef)
	private AssrtCoreEBot()
	{
		super(null, Role.EMPTY_ROLE, Op.EMPTY_OPERATOR, ASSRTCORE_EMPTY_PAYLOAD, AssrtTrueFormula.TRUE,  // null ef OK?
				AssrtCoreESend.DUMMY_VAR, AssrtCoreESend.ZERO);
	}
	
	@Override
	public boolean isSend()
	{
		return false;
	}
	
	@Override
	public AssrtCoreEReceive toDual(Role self)
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}

	@Override
	public AssrtCoreSSend toGlobal(SModelFactory sf, Role self)
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
	public boolean canEqual(Object o)  // FIXME: rename canEquals
	{
		return o instanceof AssrtCoreEBot;
	}
}

// <a>
class AssrtCoreEPendingRequest extends AssrtCoreESend  // Q stores ESends (not EConnect)
{
	public static final Payload ASSRTCORE_EMPTY_PAYLOAD =
			new Payload(Arrays.asList(new AssrtAnnotDataType(new AssrtDataTypeVar("_BOT"), AssrtCoreGProtocolDeclTranslator.UNIT_DATATYPE)));
			// Cf. Payload.EMPTY_PAYLOAD
	
	private final AssrtERequest msg;  // Not included in equals/hashCode

	//public AssrtCoreEPendingConnection(AssrtEModelFactory ef, Role r, MessageId<?> op, Payload pay, AssrtBoolFormula ass)
	public AssrtCoreEPendingRequest(AssrtERequest msg)
	{
		super(null, msg.peer, msg.mid, msg.payload, msg.ass,  // HACK: null ef OK?  cannot access es.ef
				AssrtCoreESend.DUMMY_VAR, AssrtCoreESend.ZERO);
		this.msg = msg;
	}
	
	public AssrtERequest getMessage()
	{
		return this.msg;
	}
	
	@Override
	public boolean isSend()
	{
		return false;
	}
	
	@Override
	public AssrtCoreEReceive toDual(Role self)
	{
		throw new RuntimeException("[assrt-core] Shouldn't get in here: " + this);
	}

	@Override
	public AssrtCoreSSend toGlobal(SModelFactory sf, Role self)
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
	public boolean canEqual(Object o)  // FIXME: rename canEquals
	{
		return o instanceof AssrtCoreEPendingRequest;
	}
}
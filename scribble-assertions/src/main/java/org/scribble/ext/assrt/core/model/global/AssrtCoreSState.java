package org.scribble.ext.assrt.core.model.global;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.core.ast.global.AssrtCoreGProtocolDeclTranslator;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAction;
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
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.scribble.ext.assrt.util.Z3Wrapper;
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

			
//.. do we really need receive-exists?  i.e., "local" vs. "global" TS? -- is global TS really justified/used? -- local TS vs coherence?
					
//.. for scribble, need a property connecting "unrefined" safety and "refined"...


public class AssrtCoreSState extends MPrettyState<Void, SAction, AssrtCoreSState, Global>
{
	// FIXME
	enum SmtConfig { NATIVE_Z3, JAVA_SMT_Z3, NONE }
	
	private static final SmtConfig SMT_CONFIG = SmtConfig.NATIVE_Z3;
	
	private final Set<Role> subjs = new HashSet<>();  // Hacky: mostly because EState has no self -- for progress checking
	
	// In hash/equals -- cf. SState.config
	private final Map<Role, AssrtEState> P;          
	private final Map<Role, Map<Role, AssrtCoreESend>> Q;  // null value means connected and empty -- dest -> src -> msg
	private final Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R;  

	private final Map<Role, Set<AssrtDataTypeVar>> K;  // Conflict between having this in the state, and formula building?
	private final Map<Role, Set<AssrtBoolFormula>> F;   // N.B. because F not in equals/hash, "final" receive in a recursion doesn't get built -- cf., unsat check only for send actions

	public AssrtCoreSState(Map<Role, AssrtEState> P, boolean explicit)
	{
		this(P, makeQ(P.keySet(), explicit), makeR(P), makeK(P.keySet()), makeF(P));
	}

	// Pre: non-aliased "ownership" of all Map contents
	protected AssrtCoreSState(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreESend>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F
	)
	{
		super(Collections.emptySet());
		this.P = Collections.unmodifiableMap(P);
		this.Q = Collections.unmodifiableMap(Q);  // Don't need copyQ, etc. -- should already be fully "owned"
		this.R = Collections.unmodifiableMap(R);

		this.K = Collections.unmodifiableMap(K);
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
		});
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
		});
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
	
	// i.e., output state has a "well-asserted" action
	public boolean isAssertionProgressError(Job job)  // FIXME: not actuall a "progress" error
	{
		return this.P.entrySet().stream().anyMatch(e ->
		{
			List<EAction> as = e.getValue().getAllActions(); // N.B. getAllActions includes non-fireable
			return as.stream().anyMatch(a -> a.isSend() || a.isRequest()) && as.stream().noneMatch(a ->
			{
				if (a instanceof AssrtCoreESend)
				{
					Role src = e.getKey();
					AssrtBoolFormula ass = ((AssrtESend) a).ass;
					if (ass.equals(AssrtTrueFormula.TRUE))
					{
						return true;
					}

					AssrtBoolFormula AA = ass;
					Set<AssrtIntVarFormula> varsA = new HashSet<>();
					varsA.add(AssrtFormulaFactory
							.AssrtIntVar(((AssrtAnnotDataType) a.payload.elems.get(0)).var.toString()));
					// Adding even if var not used
					// N.B. includes the case for recursion cycles where var is "already"
					// in F
					if (!varsA.isEmpty()) // FIXME: currently never empty
					{
						AA = AssrtFormulaFactory.AssrtExistsFormula(new LinkedList<>(varsA), AA);
					}

					AssrtBoolFormula lhs = this.F.get(src).stream().reduce(
							AssrtTrueFormula.TRUE,
							(b1, b2) -> AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, b1, b2));
					AssrtBoolFormula impli = AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.Imply, lhs, AA);

					Set<AssrtDataTypeVar> free = new HashSet<>();
					free.addAll(impli.getVars());
					if (!free.isEmpty())
					{
						impli = AssrtFormulaFactory.AssrtForallFormula(
								free.stream().map(v -> AssrtFormulaFactory.AssrtIntVar(v.toString()))
										.collect(Collectors.toList()),
								impli);
					}
					
					job.debugPrintln("\n[assrt-core] Checking assertion progress for " + src + " at " + e.getValue() + "(" + this.id + "):");
					String str = impli.toSmt2Formula();
					job.debugPrintln("  formula  = " + str);

					AssrtBoolFormula squashed = impli.squash();
					String squashedstr = squashed.toSmt2Formula();

					job.debugPrintln("  squashed = " + squashedstr);

					switch (SMT_CONFIG)
					{
						case JAVA_SMT_Z3:
						{
							JavaSmtWrapper jsmt = JavaSmtWrapper.getInstance();
							return jsmt.isSat(squashed.getJavaSmtFormula());
						}
						case NATIVE_Z3:
							return Z3Wrapper.isSat(Z3Wrapper.toSmt2(squashed.toSmt2Formula()), job.getContext().main.toString());
						case NONE:
						{
							job.debugPrintln("\n[assrt-core] WARNING: assertion progress check skipped.");

							return true;
						}
						default:
							throw new RuntimeException("[assrt-core] Shouldn't get in here: " + SMT_CONFIG);
					}
				}
				else if (a instanceof AssrtERequest)
				{
					return true; // TODO: request
				}
				/*else if (a instanceof AssrtCoreEReceive || a instanceof AssrtEAccept)
				{
					return true;  // FIXME: check receive assertions? -- currently receive assertions all set to True
				}*/
				else
				{
					System.err.println("[assrt-core] Shouldn't get in here: " + a);
					System.exit(1); // FIXME
					return false;
				}
			});
		});
	}

	// i.e., state has an action that is not satisfiable (deadcode)
	public boolean isUnsatisfiableError(Job job)  // FIXME: not actuall a "progress" error
	{
		return this.P.entrySet().stream().anyMatch(e ->
		{
			List<EAction> as = e.getValue().getAllActions(); // N.B. getAllActions includes non-fireable
			return as.stream().anyMatch(a -> a.isSend() || a.isRequest()) && as.stream().anyMatch(a ->
			{
				if (a instanceof AssrtCoreESend)  // FIXME: factor out with isAssertionProgressError
				{
					Role src = e.getKey();
					AssrtBoolFormula ass = ((AssrtESend) a).ass;
					if (ass.equals(AssrtTrueFormula.TRUE))  // OK to skip? i.e., no need to check existing F (impli LHS) is true?
					{
						return false; 
					}

					AssrtBoolFormula AA = ass;
					Set<AssrtIntVarFormula> varsA = new HashSet<>();
					AssrtIntVarFormula vvv = AssrtFormulaFactory.AssrtIntVar(((AssrtAnnotDataType) a.payload.elems.get(0)).var.toString());
					varsA.add(vvv); // Adding even if var not used
							// N.B. includes the case for recursion cycles where var is "already" in F
					if (!varsA.isEmpty()) // FIXME: currently never empty
					{
						AA = AssrtFormulaFactory.AssrtExistsFormula(new LinkedList<>(varsA), AA);
					}

					AssrtBoolFormula tocheck = this.F.get(src).stream().reduce(
							AA,
							(b1, b2) -> AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, b1, b2));
					Set<AssrtDataTypeVar> free = new HashSet<>();
					free.addAll(tocheck.getVars());
					if (!free.isEmpty())
					{
						tocheck = AssrtFormulaFactory.AssrtExistsFormula(
								free.stream().map(v -> AssrtFormulaFactory.AssrtIntVar(v.toString()))
										.collect(Collectors.toList()),
								tocheck);
					}
					
					job.debugPrintln("\n[assrt-core] Checking satisfiability for " + src + " at " + e.getValue() + "(" + this.id + "):");
					String str = tocheck.toSmt2Formula();
					job.debugPrintln("  formula  = " + str);

					AssrtBoolFormula squashed = tocheck.squash();
					String squashedstr = squashed.toSmt2Formula();

					job.debugPrintln("  squashed = " + squashedstr);

					switch (SMT_CONFIG)
					{
						case JAVA_SMT_Z3:
						{
							JavaSmtWrapper jsmt = JavaSmtWrapper.getInstance();
							return !jsmt.isSat(squashed.getJavaSmtFormula());
						}
						case NATIVE_Z3:
							return !Z3Wrapper.isSat(Z3Wrapper.toSmt2(squashed.toSmt2Formula()), job.getContext().main.toString());
						case NONE:
						{
							job.debugPrintln("\n[assrt-core] WARNING: satisfiability check skipped.");

							return false;
						}
						default:
							throw new RuntimeException("[assrt-core] Shouldn't get in here: " + SMT_CONFIG);
					}
				}
				else if (a instanceof AssrtERequest)
				{
					return true; // TODO: request
				}
				/*else if (a instanceof AssrtCoreEReceive || a instanceof AssrtEAccept)
				{
					return true;  // FIXME: check receive assertions? -- currently receive assertions all set to True
				}*/
				else
				{
					System.err.println("[assrt-core] Shouldn't get in here: " + a);
					System.exit(1); // FIXME
					return false;
				}
			});
		});
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
		Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R = AssrtCoreSState.copyR(this.R);

		//R.get(self).putAll(succ.getAnnotVars());  // Should "overwrite" previous var values -- no, do later (and from action info, not state)

		Map<Role, Set<AssrtDataTypeVar>> K = AssrtCoreSState.copyK(this.K);
		Map<Role, Set<AssrtBoolFormula>> F = AssrtCoreSState.copyF(this.F);

		AssrtEState succ = P.get(self).getSuccessor(a);

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
	private static void fireSend(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreESend>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,
			Role self, AssrtCoreESend es, AssrtEState succ)
	{
		P.put(self, succ);
		//Q.get(es.peer).put(self, es.toTrueAssertion());  // HACK FIXME: cf. AssrtSConfig::fire
		Q.get(es.peer).put(self, es);  // Now doing toTrueAssertion on message at receive side

		outputUpdateKF(R, K, F, self, es, succ);

		//updateR(R, self, es);
	}

	private static void fireReceive(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreESend>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R, 
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,   // FIXME: manage F with receive assertions?
			Role self, AssrtCoreEReceive er, AssrtEState succ)
	{
		P.put(self, succ);
		AssrtESend m = Q.get(self).put(er.peer, null);  // null is \epsilon
		
		//inputUpdateK(K,  self, er);
		inputUpdateKF(R, K, F, self, er, m, succ);
				
		// Must come after F update
		//updateR(R, self, er);
	}

	// FIXME: R
	private //static
	void fireRequest(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreESend>> Q,
			//Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,
			Role self, AssrtERequest es, AssrtEState succ)
	{
		P.put(self, succ);
		//Q.get(es.peer).put(self, new AssrtCoreEPendingRequest(es.toTrueAssertion()));  // HACK FIXME: cf. AssrtSConfig::fire
		Q.get(es.peer).put(self, new AssrtCoreEPendingRequest(es));  // Now doing toTrueAssertion on accept side

		outputUpdateKF(null, K, F, self, (AssrtCoreEAction) es, succ);  // FIXME: core
	}

	// FIXME: R
	private static void fireAccept(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreESend>> Q,
			//Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F, 
			Role self, AssrtEAccept ea, AssrtEState succ)
	{
		P.put(self, succ);
		Q.get(self).put(ea.peer, null);

		AssrtERequest m = ((AssrtCoreEPendingRequest) Q.get(ea.peer).put(self, null)).getMessage();
		inputUpdateKF(null, K, F, self, (AssrtCoreEAction) ea, m, succ);  // FIXME: core
	}

	private static void outputUpdateKF(Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,
			Role self, AssrtCoreEAction a, AssrtEState succ)  // FIXME: EAction closest base type
	{
		/*for (PayloadElemType<?> pt : (Iterable<PayloadElemType<?>>) 
					(a.payload.elems.stream().filter(x -> x instanceof AssrtPayloadElemType<?>))::iterator)*/
		PayloadElemType<?> pt = ((EAction) a).payload.elems.get(0);
		{
			if (pt instanceof AssrtAnnotDataType)
			{
				AssrtDataTypeVar v = ((AssrtAnnotDataType) pt).var;

				// N.B. no "updateRfromF" -- actually, "update R from payload annot" -- leaving R statevars as they are is OK, validation only done from F's and R already incorporated into F (and updates handled by updateFfromR)
				// But would it be more consistent to update R?

				updateKFR(R, K, F, self, a, v, a.getAssertion(), succ);
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);  
						// Regular DataType pay elems have been given fresh annot vars (AssrtCoreGProtocolDeclTranslator.parsePayload) -- no other pay elems allowed
			}
		}
	}

	// a is the EFSM input action, which has (hacked) True ass; m is the dequeued msg, which carries the (actual) ass from the output side
	// FIXME: factor better with outputUpdateKF
	private static void inputUpdateKF(Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,
			Role self, AssrtCoreEAction a, AssrtEAction m, AssrtEState succ)  // FIXME: EAction closest base type
	{
		/*for (PayloadElemType<?> pt : (Iterable<PayloadElemType<?>>) 
					(a.payload.elems.stream().filter(x -> x instanceof AssrtPayloadElemType<?>))::iterator)*/
		PayloadElemType<?> pt = ((EAction) a).payload.elems.get(0);
		{
			if (pt instanceof AssrtAnnotDataType)
			{
				// Update K
				AssrtDataTypeVar v = ((AssrtAnnotDataType) pt).var;

				AssrtBoolFormula f = m.getAssertion();
				/*AssrtExistsFormulaHolder h =
						new AssrtExistsFormulaHolder(Arrays.asList(AssrtFormulaFactory.AssrtIntVar(v.toString())), Arrays.asList(f));*/
				
				// N.B. no "updateRfromF" -- actually, "update R from payload annot" -- leaving R statevars as they are is OK, validation only done from F's and R already incorporated into F (and updates handled by updateFfromR)
				// But would it be more consistent to update R?

				updateKFR(R, K, F, self, a, v, f, succ);  // Actual assertion (f) for annotvar (v) added in here
				
				//AssrtFormulaHolder 
				//h = F.get(self);  // FIXME: needed because updateRKF modifies F again
				////h = new AssrtForallFormulaHolder(Arrays.asList(AssrtFormulaFactory.AssrtIntVar(v.toString())), Arrays.asList(h));
				//F.put(self, h);

				/*putK(K, self, v);
				putF(R, F, self, h);
				
				/*AssrtExistsFormulaHolder h = F.get(self);
				AssrtExistsFormulaHolder hh = new AssrtExistsFormulaHolder(Arrays.asList(AssrtFormulaFactory.AssrtIntVar(v.toString())), Arrays.asList(AssrtTrueFormula.TRUE));
				h.body.add(hh);*/
				//F.put(self, h);
			}
			else
			{
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);  
						// Regular DataType pay elems have been given fresh annot vars (AssrtCoreGProtocolDeclTranslator.parsePayload) -- no other pay elems allowed
			}
		}
	}

	private static void updateKFR(Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,
			Role self, AssrtCoreEAction a, AssrtDataTypeVar v, AssrtBoolFormula f, AssrtEState succ)  // FIXME: EAction closest base type
	{
		// Update K
		putK(K, self, v);

		// Rename existing vars with same name
		Set<AssrtBoolFormula> hh = F.get(self);
		AssrtIntVarFormula old = AssrtFormulaFactory.AssrtIntVar(v.toString());
		AssrtIntVarFormula fresh = makeFreshIntVar(v);
		hh = hh.stream().map(b -> b.subs(old, fresh)).collect(Collectors.toSet());
		F.put(self, hh);

		//...record assertions so far -- later error checking: *for all* values that satisify those, it should imply the next assertion
		appendF(F, self, f);

		// Following must come after F update
		Map<AssrtDataTypeVar, AssrtArithFormula> tmp = R.get(self);
				
		succ.getAnnotVars().entrySet().forEach(e ->
		{
			AssrtDataTypeVar k = e.getKey();
			AssrtArithFormula af = e.getValue();
			if (!tmp.containsKey(k) || !tmp.get(k).equals(af))
			{
				updateRandFfromR(F, self, tmp, k, af);
				putK(K, self, k);
			}
		});

		AssrtDataTypeVar annot = a.getAnnotVar();
		AssrtArithFormula expr = a.getArithExpr();
		if (!annot.equals(AssrtCoreESend.DUMMY_VAR))  // FIXME
		{
			AssrtArithFormula curr = tmp.get(annot);
			if (!curr.equals(expr))  // CHECKME: "syntactic" check is what we want here?
			{
				updateRandFfromR(F, self, tmp, annot, expr);
			}
		}

		//Map<Role, Set<AssrtBoolFormula>> test = copyF(F);
		compactF(F);
	}
	
	private static void compactF(Map<Role, Set<AssrtBoolFormula>> F)
	{
		Map<Role, Set<AssrtBoolFormula>> copy = copyF(F);
		
		for (Role r : copy.keySet())
		{
			System.out.println("111: " + r + ", " + F.get(r));

			Set<AssrtBoolFormula> dead = new HashSet<>();
			
			Set<AssrtBoolFormula> fs = copy.get(r);
			Set<AssrtDataTypeVar> vs = fs.stream().flatMap(f -> f.getVars().stream()).collect(Collectors.toSet());
			
			Map<AssrtDataTypeVar, Set<AssrtBoolFormula>> map = vs.stream().collect(Collectors.toMap(v -> v, v -> new HashSet<>()));
			for (AssrtBoolFormula f : fs)
			{
				for (AssrtDataTypeVar v : f.getVars())
				{
					map.get(v).add(f);
				}
			}
			
			Set<AssrtBoolFormula> rem = new HashSet<>(fs);
			while (!rem.isEmpty())
			{
				Iterator<AssrtBoolFormula> foo = rem.iterator();
				AssrtBoolFormula bar = foo.next();
				foo.remove();
				
				Set<AssrtDataTypeVar> todo = bar.getVars();
				Set<AssrtDataTypeVar> seen = new HashSet<>();
				while (todo.stream().anyMatch(vv -> !seen.contains(vv)))
				{
					Set<AssrtDataTypeVar> tmp = todo.stream().filter(vv -> !seen.contains(vv)).collect(Collectors.toSet());
					todo.addAll(tmp.stream().flatMap(vvv -> map.get(vvv).stream().flatMap(fff -> fff.getVars().stream())).collect(Collectors.toSet()));
					seen.addAll(tmp);
				}
			
				System.out.println("aaa: " + r + ", " + F.get(r) + ", " + todo);
				
				if (todo.size() == 1 && todo.iterator().next().toString().startsWith("_"))
				{
					F.get(r).remove(bar);
					
					System.out.println("bbb1: " + bar);
				}
				else if (!todo.isEmpty() && todo.stream().allMatch(v -> v.toString().startsWith("_")))
				{
					todo.stream().forEach(vvv -> dead.addAll(map.get(vvv)));
					rem.removeAll(dead);
					F.get(r).removeAll(dead);
					
					System.out.println("bbb2: " + dead);
				}
			}

			System.out.println("222: " + copy.get(r) + "\n");

		}
	}

	private static void updateRandFfromR(Map<Role, Set<AssrtBoolFormula>> F, Role self,
			Map<AssrtDataTypeVar, AssrtArithFormula> Rself, AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		// Must come after initial F update
		Rself.put(annot, expr);   // "Overwrite" (if already known)

		AssrtIntVarFormula iv = AssrtFormulaFactory.AssrtIntVar(annot.toString());
		
		Set<AssrtBoolFormula> hh = F.get(self);
				
		AssrtIntVarFormula fresh = makeFreshIntVar(annot);

		if (expr.getVars().contains(annot))  // CHECKME: renaming like this OK? -- basically all R vars are being left open for top-level forall
		{
			expr = expr.subs(AssrtFormulaFactory.AssrtIntVar(annot.toString()), 
					//fresh);  // No: don't need to "link" R vars and F vars -- only F matters for direct formula checking
					makeFreshIntVar(annot));
		}
		
		hh = hh.stream().map(b -> b.subs(iv, fresh)).collect(Collectors.toSet());
		hh.add(AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq, iv, expr));
		
		F.put(self, hh);
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

		hash = 31 * hash + this.K.hashCode();
		hash = 31 * hash + this.F.hashCode();

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
		return them.canEquals(this) && this.P.equals(them.P) && this.Q.equals(them.Q) && this.R.equals(them.R)
				&& this.K.equals(them.K) && this.F.equals(them.F);
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

	//private static Map<Role, Set<AssrtBoolFormula>> makeF(Set<Role> rs)
	private static Map<Role, Set<AssrtBoolFormula>> makeF(Map<Role, AssrtEState> P)
	{
		//return rs.stream().collect(Collectors.toMap(r -> r, r -> new HashSet<>()));
		return P.entrySet().stream().collect(Collectors.toMap(
				Entry::getKey,
				e -> e.getValue().getAnnotVars().entrySet().stream()
						.map(b -> AssrtFormulaFactory.AssrtBinComp(
								AssrtBinCompFormula.Op.Eq, 
								AssrtFormulaFactory.AssrtIntVar(b.getKey().toString()),
								b.getValue()))
						.collect(Collectors.toSet())
		));
	}

	private static Map<Role, Set<AssrtBoolFormula>> copyF(Map<Role, Set<AssrtBoolFormula>> F)
	{
		return F.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> new HashSet<>(e.getValue())));
	}

	private static void appendF(Map<Role, Set<AssrtBoolFormula>> F, Role r, AssrtBoolFormula f)
	{
		F.get(r).add(f);
	}

	private static Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> makeR(Map<Role, AssrtEState> P)
	{
		Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R = P.entrySet().stream().collect(Collectors.toMap(
				e -> e.getKey(),
				e -> new HashMap<>(e.getValue().getAnnotVars())
		));
		/*Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R = P.keySet().stream().collect(Collectors.toMap(r -> r, r ->
				Stream.of(false).collect(Collectors.toMap(
						x -> AssrtCoreESend.DUMMY_VAR,
						x -> AssrtCoreESend.ZERO))
			));*/
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
	}*/
	
	private static int counter = 1;
	
	private static AssrtIntVarFormula makeFreshIntVar(AssrtDataTypeVar var)
	{
		return AssrtFormulaFactory.AssrtIntVar("_" + var.toString() + counter++);  // HACK
	}
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
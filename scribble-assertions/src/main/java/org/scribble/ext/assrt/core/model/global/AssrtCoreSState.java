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
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAccept;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAction;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEReceive;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreERequest;
import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreESend;
import org.scribble.ext.assrt.core.model.global.action.AssrtCoreSSend;
import org.scribble.ext.assrt.main.AssrtJob;
import org.scribble.ext.assrt.model.endpoint.AssrtEState;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEAction;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.formula.AssrtBinBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtBinCompFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.main.Job;
import org.scribble.model.MPrettyState;
import org.scribble.model.MState;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.EStateKind;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.global.SModelFactory;
import org.scribble.model.global.actions.SAction;
import org.scribble.type.Payload;
import org.scribble.type.kind.Global;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.Op;
import org.scribble.type.name.PayloadElemType;
import org.scribble.type.name.Role;

			
//.. do we really need receive-exists?  i.e., "local" vs. "global" TS? -- is global TS really justified/used? -- local TS vs coherence?
					
//.. for scribble, need a property connecting "unrefined" safety and "refined"...


public class AssrtCoreSState extends MPrettyState<Void, SAction, AssrtCoreSState, Global>
{
	private static int counter = 1;
	
	private static AssrtIntVarFormula makeFreshIntVar(AssrtDataTypeVar var)
	{
		return AssrtFormulaFactory.AssrtIntVar("_" + var.toString() + counter++);  // HACK
		//return AssrtFormulaFactory.AssrtIntVar("_" + var.toString());  // HACK
	}

	private final Set<Role> subjs = new HashSet<>();  // Hacky: mostly because EState has no self -- for progress checking
	
	// In hash/equals -- cf. SState.config
	private final Map<Role, AssrtEState> P;          
	private final Map<Role, Map<Role, AssrtCoreEMessage>> Q;  // null value means connected and empty -- dest -> src -> msg
	private final Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R;  

	private final Map<Role, Set<AssrtDataTypeVar>> K;  // Conflict between having this in the state, and formula building?
	private final Map<Role, Set<AssrtBoolFormula>> F;   // N.B. because F not in equals/hash, "final" receive in a recursion doesn't get built -- cf., unsat check only for send actions
	
	private final Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename; // combine with K?

	public AssrtCoreSState(Map<Role, AssrtEState> P, boolean explicit)
	{
		this(P, makeQ(P.keySet(), explicit), makeR(P), makeK(P.keySet()), makeF(P), 
				P.keySet().stream().collect(Collectors.toMap(r -> r, r -> new HashMap<>())));
	}

	// Pre: non-aliased "ownership" of all Map contents
	protected AssrtCoreSState(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreEMessage>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename
	)
	{
		super(Collections.emptySet());
		this.P = Collections.unmodifiableMap(P);
		this.Q = Collections.unmodifiableMap(Q);  // Don't need copyQ, etc. -- should already be fully "owned"
		this.R = Collections.unmodifiableMap(R);

		this.K = Collections.unmodifiableMap(K);
		this.F = Collections.unmodifiableMap(F);
		
		this.rename = Collections.unmodifiableMap(rename);
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
						.map(a -> ((AssrtCoreESend) a.toDual(dest)).toTrueAssertion())
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
										 .map(a -> ((AssrtCoreERequest) a.toDual(r1)).toTrueAssertion())
										 .noneMatch(a -> a.equals(((AssrtCoreEPendingRequest) this.Q.get(r1).get(r2)).getMessage().toTrueAssertion()))  
											 
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
	
	public boolean isUnknownDataTypeVarError(Job job, GProtocolName simpname)
	{
		return this.P.entrySet().stream().anyMatch(e ->
				e.getValue().getAllActions().stream().anyMatch(a -> 
				{
					if (a.isSend() || a.isRequest())
					{
						Role src = e.getKey();

						Set<AssrtDataTypeVar> known = new HashSet<>(this.K.get(src));
						a.payload.elems.forEach(pe ->  // Currently exactly one elem
						{
							if (pe instanceof AssrtAnnotDataType)
							{
								known.add(((AssrtAnnotDataType) pe).var);
							}
							else
							{
								throw new RuntimeException("[assrt-core] Shouldn't get in here: " + pe);
							}
						});

						known.addAll(this.R.get(src).keySet());

						Set<String> rs = job.getContext().getMainModule().getProtocolDecl(simpname).header.roledecls
								.getRoles().stream().map(Object::toString).collect(Collectors.toSet());
						return ((AssrtCoreEAction) a).getAssertion().getIntVars().stream()
								.filter(v -> !rs.contains(v.toString()))
								.anyMatch(v -> !known.contains(v));
					}
					else
					{
						// FIXME: receive assertions? currently hardcoded to True

						return false;
					}
				}));
	}
	
	// i.e., output state has a "well-asserted" action
	public boolean isAssertionProgressError(Job job, GProtocolName simpname)  // FIXME: not actually a "progress" error
			//throws ScribbleException
	{
		/*return new StreamExceptionCaller<Boolean, ScribbleException>()
			{
				@Override
				public Boolean trybody() throws ScribbleStreamException
				{*/
		return this.P.entrySet().stream().anyMatch(e ->  // anyMatch is on the endpoints (not actions)
		{
			List<EAction> as = e.getValue().getAllActions(); // N.B. getAllActions includes non-fireable
			if (as.isEmpty() || as.stream().noneMatch(a -> a.isSend() || a.isRequest())) 
			{
				return false;
			}
					
			/*as.stream().noneMatch(a ->
			{
				if (a instanceof AssrtCoreESend || a instanceof AssrtCoreERequest)
				{*/
					Role src = e.getKey();
					/*AssrtBoolFormula ass = ((AssrtCoreEAction) a).getAssertion();
					if (ass.equals(AssrtTrueFormula.TRUE))
					{
						return true;
					}*/

					AssrtBoolFormula AA = null;// = ass;
					for (EAction a : as)
					{
						if (!(a instanceof AssrtCoreESend) && !(a instanceof AssrtCoreERequest))
						{
							throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);
						}
						AssrtBoolFormula ass = ((AssrtCoreEAction) a).getAssertion();
						if (ass.equals(AssrtTrueFormula.TRUE))
						{
							return false;
						}

						Set<AssrtIntVarFormula> varsA = new HashSet<>();
						varsA.add(AssrtFormulaFactory
								.AssrtIntVar(((AssrtAnnotDataType) a.payload.elems.get(0)).var.toString()));
						// Adding even if var not used
						// N.B. includes the case for recursion cycles where var is "already"
						// in F
						if (!varsA.isEmpty()) // FIXME: currently never empty
						{
							//AA = AssrtFormulaFactory.AssrtExistsFormula(new LinkedList<>(varsA), AA);
							ass = AssrtFormulaFactory.AssrtExistsFormula(new LinkedList<>(varsA), ass);
						}
						
						AA = (AA == null) ? ass : AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.Or, AA, ass);
					}

					AssrtBoolFormula lhs = this.F.get(src).stream().reduce(
							AssrtTrueFormula.TRUE,
							(b1, b2) -> AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.And, b1, b2));
					AssrtBoolFormula impli = AssrtFormulaFactory.AssrtBinBool(AssrtBinBoolFormula.Op.Imply, lhs, AA);

					Set<AssrtDataTypeVar> free = new HashSet<>();
					free.addAll(impli.getIntVars());
					if (!free.isEmpty())
					{
						impli = AssrtFormulaFactory.AssrtForallFormula(
								free.stream().map(v -> AssrtFormulaFactory.AssrtIntVar(v.toString()))
										.collect(Collectors.toList()),
								impli);
					}
					
					job.debugPrintln("\n[assrt-core] Checking assertion progress for " + src + " at " + e.getValue() + "(" + this.id + "):");
					String str = impli.toSmt2Formula();
					job.debugPrintln("  raw      = " + str);

					AssrtBoolFormula squashed = impli.squash();

					job.debugPrintln("  squashed = " + squashed.toSmt2Formula());

					return !((AssrtJob) job).checkSat(simpname, squashed);
				/*}
				/*else if (a instanceof AssrtCoreEReceive || a instanceof AssrtEAccept)
				{
					return true;  // FIXME: check receive assertions? -- currently receive assertions all set to True
				}* /
				else
				{
					throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);
				}
			});*/
		}
		);
				/*}
			}.dotry();*/
	}

	// i.e., state has an action that is not satisfiable (deadcode)
	public boolean isUnsatisfiableError(Job job, GProtocolName simpname)
	{
		return this.P.entrySet().stream().anyMatch(e ->
		{
			List<EAction> as = e.getValue().getAllActions(); // N.B. getAllActions includes non-fireable
			if (as.size() <= 1)  
					// Only doing on non-unary choices -- for unary, assrt-prog implies sat
					// Note: this means "downstream" unsat errors for unary-choice continuations will not be caught (i.e., false => false for assrt-prog)
			{
				return false;
			}
			return as.stream().anyMatch(a -> a.isSend() || a.isRequest()) && as.stream().anyMatch(a ->
			{
				if (a instanceof AssrtCoreESend || a instanceof AssrtCoreERequest)  // FIXME: factor out with isAssertionProgressError
				{
					Role src = e.getKey();
					AssrtBoolFormula ass = ((AssrtCoreEAction) a).getAssertion();
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
					free.addAll(tocheck.getIntVars());
					if (!free.isEmpty())
					{
						tocheck = AssrtFormulaFactory.AssrtExistsFormula(
								free.stream().map(v -> AssrtFormulaFactory.AssrtIntVar(v.toString()))
										.collect(Collectors.toList()),
								tocheck);
					}
					
					job.debugPrintln("\n[assrt-core] Checking satisfiability for " + src + " at " + e.getValue() + "(" + this.id + "):");
					job.debugPrintln("  formula  = " + tocheck.toSmt2Formula());

					AssrtBoolFormula squashed = tocheck.squash();

					job.debugPrintln("  squashed = " + squashed.toSmt2Formula());

					return !((AssrtJob) job).checkSat(simpname, squashed);
				}
				else if (a instanceof AssrtCoreERequest)
				{
					return false; // TODO: request
				}
				/*else if (a instanceof AssrtCoreEReceive || a instanceof AssrtCoreEAccept)
				{
					return true;  // FIXME: check receive assertions? -- currently receive assertions all set to True
				}*/
				else
				{
					throw new RuntimeException("[assrt-core] Shouldn't get in here: " + a);
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
					AssrtCoreERequest ec = (AssrtCoreERequest) a;  // FIXME: core
					getRequestFireable(res, self, ec);
				}
				else if (a.isAccept())
				{
					AssrtCoreEAccept ea = (AssrtCoreEAccept) a;  // FIXME: core
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

	private void getSendFireable(Map<Role, List<EAction>> res, Role self, AssrtCoreESend es)
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

		AssrtCoreESend m = this.Q.get(self).get(er.peer);
		//if (er.toDual(self).equals(m))  //&& !(m instanceof F17EBot)
		if (((AssrtCoreESend) er.toDual(self)).toTrueAssertion().equals(m.toTrueAssertion()))  
				// HACK FIXME: check assertion implication (not just syntactic equals) -- cf. AssrtSConfig::fire
		{
			res.get(self).add(er);
		}
	}

	private void getRequestFireable(Map<Role, List<EAction>> res, Role self, AssrtCoreERequest es)
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
	private void getAcceptFireable(Map<Role, List<EAction>> res, Role self, AssrtCoreEAccept ea)
	{
		if (hasPendingRequest(self) || !isPendingRequest(ea.peer, self))
		{
			return;
		}

		AssrtCoreERequest ec = ((AssrtCoreEPendingRequest) this.Q.get(self).get(ea.peer)).getMessage();
		//if (ea.toDual(self).equals(ec))
		if (((AssrtCoreERequest) ea.toDual(self)).toTrueAssertion().equals(ec.toTrueAssertion()))  
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
		Map<Role, Map<Role, AssrtCoreEMessage>> Q = AssrtCoreSState.copyQ(this.Q);
		Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R = AssrtCoreSState.copyR(this.R);

		//R.get(self).putAll(succ.getAnnotVars());  // Should "overwrite" previous var values -- no, do later (and from action info, not state)

		Map<Role, Set<AssrtDataTypeVar>> K = AssrtCoreSState.copyK(this.K);
		Map<Role, Set<AssrtBoolFormula>> F = AssrtCoreSState.copyF(this.F);

		Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename = AssrtCoreSState.copyRename(this.rename);

		AssrtEState succ = P.get(self).getSuccessor(a);

		if (a.isSend())
		{
			fireSend(P, Q, R, K, F, rename, self, (AssrtCoreESend) a, succ);
		}
		else if (a.isReceive())
		{
			fireReceive(P, Q, R, K, F, rename, self, (AssrtCoreEReceive) a, succ);
		}
		else if (a.isRequest())
		{
			fireRequest(P, Q, R, K, F, rename, self, (AssrtCoreERequest) a, succ);  // FIXME: core
		}
		else if (a.isAccept())
		{
			fireAccept(P, Q, R, K, F, rename, self, (AssrtCoreEAccept) a, succ);  // FIXME: core
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
		
		AssrtCoreSState tmp = new AssrtCoreSState(P, Q, R, K, F, rename);

		//System.out.println("\n444: " + tmp.toNodeDot() + "\n"); if (F.get(self).size() > 5) throw new RuntimeException("stop");

		return tmp;
	}


	// Update (in place) P, Q, R, K and F
	private static void fireSend(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreEMessage>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename,
			Role self, AssrtCoreESend es, AssrtEState succ)
	{
		P.put(self, succ);

		outputUpdateKF(R, K, F, rename, self, es, succ);

		//updateR(R, self, es);

		//Q.get(es.peer).put(self, es.toTrueAssertion());  // HACK FIXME: cf. AssrtSConfig::fire
		//Q.get(es.peer).put(self, es);  // Now doing toTrueAssertion on message at receive side
		Q.get(es.peer).put(self, new AssrtCoreEMessage(es.getEModelFactory(), es.peer, es.mid, es.payload, es.ass, 
				//es.annot,
				es.stateexprs,
				rename.get(self)));  // Now doing toTrueAssertion on message at receive side
	}

	private static void fireReceive(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreEMessage>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R, 
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,   // FIXME: manage F with receive assertions?
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename,
			Role self, AssrtCoreEReceive er, AssrtEState succ)
	{
		P.put(self, succ);
		AssrtCoreEMessage m = Q.get(self).put(er.peer, null);  // null is \epsilon
		
		//inputUpdateK(K,  self, er);
		inputUpdateKF(R, K, F, rename, self, er, m, succ, m.shadow);
				
		// Must come after F update
		//updateR(R, self, er);
	}

	private static void fireRequest(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreEMessage>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename,
			Role self, AssrtCoreERequest er, AssrtEState succ)
	{
		P.put(self, succ);

		outputUpdateKF(R, K, F, rename, self, er, succ);

		//Q.get(es.peer).put(self, new AssrtCoreEPendingRequest(es.toTrueAssertion()));  // HACK FIXME: cf. AssrtSConfig::fire
		Q.get(er.peer).put(self, new AssrtCoreEPendingRequest(er, rename.get(self)));  // Now doing toTrueAssertion on accept side
	}

	// FIXME: R
	private static void fireAccept(Map<Role, AssrtEState> P, Map<Role, Map<Role, AssrtCoreEMessage>> Q,
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F, 
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename,
			Role self, AssrtCoreEAccept ea, AssrtEState succ)
	{
		P.put(self, succ);
		//AssrtCoreERequest m = ((AssrtCoreEPendingRequest) Q.get(ea.peer).put(self, null)).getMessage();
		AssrtCoreEPendingRequest pr = (AssrtCoreEPendingRequest) Q.get(self).put(ea.peer, null);
		AssrtCoreERequest m = pr.getMessage();
		//Q.get(self).put(ea.peer, null);
		Q.get(ea.peer).put(self, null);

		inputUpdateKF(R, K, F, rename, self, (AssrtCoreEAction) ea, m, succ, pr.shadow);  // FIXME: core
	}

	private static void outputUpdateKF(Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename,
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

				// Rename existing vars with same name
				Set<AssrtBoolFormula> hh = F.get(self);
				if (hh.stream().anyMatch(hhh -> hhh.getIntVars().contains(v)))
				{
					AssrtIntVarFormula old = AssrtFormulaFactory.AssrtIntVar(v.toString());
					AssrtIntVarFormula fresh = makeFreshIntVar(v);
					//Map<AssrtIntVarFormula, AssrtIntVarFormula> rename = Stream.of(old).collect(Collectors.toMap(x -> x, x -> fresh));
					rename.get(self).put(old, fresh);
					hh = hh.stream().map(b -> b.subs(old, fresh)).collect(Collectors.toSet());
					F.put(self, hh);
				}

				updateKFR(R, K, F, rename, self, a, v, a.getAssertion(), succ);
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
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename,
			Role self, AssrtCoreEAction a, AssrtEAction m, AssrtEState succ,
			Map<AssrtIntVarFormula, AssrtIntVarFormula> shadow)  // FIXME: EAction closest base type
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
				

				//Map<AssrtIntVarFormula, AssrtIntVarFormula> shadow = ((AssrtCoreEMessage) m).shadow;
				Set<Entry<AssrtIntVarFormula, AssrtIntVarFormula>> ren = rename.get(self).entrySet();
				Set<AssrtBoolFormula> hh = F.get(self);
				for (Entry<AssrtIntVarFormula, AssrtIntVarFormula> e : shadow.entrySet().stream().filter(e -> !ren.contains(e)).collect(Collectors.toList()))
				{
					hh = hh.stream().map(b -> b.subs(e.getKey(), e.getValue())).collect(Collectors.toSet());
				}
				F.put(self, hh);
				rename.get(self).putAll(shadow);


				updateKFR(R, K, F, rename, self, a, v, f, succ);  // Actual assertion (f) for annotvar (v) added in here
				
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

	private static //Map<AssrtIntVarFormula, AssrtIntVarFormula>
			void updateKFR(
			Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<Role, Set<AssrtBoolFormula>> F,
			Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename,
			Role self, AssrtCoreEAction a, AssrtDataTypeVar v, AssrtBoolFormula f, AssrtEState succ)  // FIXME: EAction closest base type
	{
		// Update K
		putK(K, self, v);

		/*// Rename existing vars with same name
		Set<AssrtBoolFormula> hh = F.get(self);
		if (hh.stream().anyMatch(hhh -> hhh.getVars().contains(v)))
		{
			AssrtIntVarFormula old = AssrtFormulaFactory.AssrtIntVar(v.toString());
			AssrtIntVarFormula fresh = makeFreshIntVar(v);
			//Map<AssrtIntVarFormula, AssrtIntVarFormula> rename = Stream.of(old).collect(Collectors.toMap(x -> x, x -> fresh));
			rename.get(self).put(old, fresh);
			hh = hh.stream().map(b -> b.subs(old, fresh)).collect(Collectors.toSet());
			F.put(self, hh);
		}*/

		//...record assertions so far -- later error checking: *for all* values that satisify those, it should imply the next assertion
		appendF(F, self, f);
		

		// Rename old R vars -- must come before adding new F and R clauses
		Map<AssrtDataTypeVar, AssrtArithFormula> Rself = R.get(self);
		//AssrtDataTypeVar annot = a.getAnnotVar();

		// "forward" recs will have annotvars but no stateexprs
		Map<AssrtDataTypeVar, AssrtArithFormula> annotvars = succ.getStateVars();
		List<AssrtArithFormula> stateexprs = a.getStateExprs();
		

		// Following must come after F update

		// Update R from state -- entering a rec "forwards", i.e., not via a continue
		//if (annot.equals(AssrtCoreEAction.DUMMY_VAR))  // HACK CHECKME
		if (stateexprs.isEmpty())
		{
			annotvars.entrySet().forEach(e ->
			{
				AssrtDataTypeVar k = e.getKey();
				AssrtArithFormula af = e.getValue();
				
				
				// FIXME: record statevar mapping for "direct substitution" modelling special case? (i.e., no "old var" renaming), e.g., x -> x, or x -> y -> x -- i.e. treat subproto statevars more like formal params
				// Anyway, need to check something about new vs shadowed vs udapted vs etc state vars -- currently nothing is checked syntactically
				// "forwards" rec should also be handled by action statevar update?
				
				
				if (!Rself.containsKey(k) || !Rself.get(k).equals(af))  // FIXME: need to treat statevars more like roles? i.e., statevar must be explicitly declared/passed to stay "in scope" in the subproto?
				{
					updateRandFfromR(F, self, Rself, k, af);
					//putK(K, self, k);
				}
			});
		}
		

		//AssrtArithFormula expr = a.getArithExpr();

		//if (!annot.equals(AssrtCoreEAction.DUMMY_VAR))
		if (!stateexprs.isEmpty())
		{
			if (annotvars.size() != stateexprs.size())
			{
				throw new RuntimeException("[assrt-core] Shouldn't get here: " + annotvars + ", " + stateexprs);  // FIXME: not actually syntactically checked yet
			}

			Iterator<AssrtArithFormula> afs = stateexprs.iterator();
			for (AssrtDataTypeVar annot : annotvars.keySet())
			{
				//AssrtArithFormula expr = a.getStateExprs().iterator().next();
				AssrtArithFormula expr = afs.next();

				if (expr.getIntVars().contains(annot))  // CHECKME: renaming like this OK? -- basically all R vars are being left open for top-level forall
				{
					expr = expr.subs(AssrtFormulaFactory.AssrtIntVar(annot.toString()), 
							//fresh  // No: don't need to "link" R vars and F vars -- only F matters for direct formula checking
							//makeFreshIntVar(annot)  // Makes model construction non-terminating, e.g., mu X(x:=..) ... X<x> -- makes unbounded fresh in x = fresh(x)
							AssrtFormulaFactory.AssrtIntVar("_" + annot.toString())  // FIXME: is this OK?
					);	
				}

				// Update R from action -- recursion back to a rec, via a continue

				AssrtArithFormula curr = Rself.get(annot);
				if (!curr.equals(expr)  // CHECKME: "syntactic" check is what we want here?
						&& !((expr instanceof AssrtIntVarFormula) && ((AssrtIntVarFormula) expr).name.equals("_" + annot.toString())))  // Hacky? if expr is just the var occurrence, then value doesn't change
								// FIXME: generalise -- occurences of other vars can be first substituted, before "old var renaming"? -- also for rec-state updates?
				{
					updateRandFfromR(F, self, Rself, annot, expr);
				}
			}
		}

		//Map<Role, Set<AssrtBoolFormula>> test = copyF(F);
		compactF(F, self);
		//return rename;
	}

	private static void updateRandFfromR(Map<Role, Set<AssrtBoolFormula>> F, Role self,
			Map<AssrtDataTypeVar, AssrtArithFormula> Rself, AssrtDataTypeVar annot, AssrtArithFormula expr)
	{
		// Must come after initial F update
		Rself.put(annot, expr);   // "Overwrite" (if already known)

		AssrtIntVarFormula iv = AssrtFormulaFactory.AssrtIntVar(annot.toString());
		AssrtIntVarFormula fresh = makeFreshIntVar(annot);
		Set<AssrtBoolFormula> hh = F.get(self);
		hh = hh.stream().map(b -> b.subs(iv, fresh)).collect(Collectors.toSet());
		hh.add(AssrtFormulaFactory.AssrtBinComp(AssrtBinCompFormula.Op.Eq, iv, expr));
		F.put(self, hh);
	}
	
	private static void compactF(Map<Role, Set<AssrtBoolFormula>> F, Role self)//, Map<Role, Map<AssrtDataTypeVar, AssrtArithFormula>> R)
	{
		//for (Set<AssrtBoolFormula> s : F.values())
		{
			Set<AssrtBoolFormula> s = F.get(self);
			Iterator<AssrtBoolFormula> i = s.iterator();
			while (i.hasNext())
			{
				AssrtBoolFormula f = i.next();
				if (f.equals(AssrtTrueFormula.TRUE) || f.getIntVars().stream().anyMatch(v -> v.toString().startsWith("_")))  // FIXME
				{
					i.remove();
				}
			}
		}

		/*Map<Role, Set<AssrtBoolFormula>> copy = copyF(F);
		
		for (Role r : copy.keySet())
		{
			//System.out.println("111: " + r + ", " + F.get(r));

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
			
				//System.out.println("aaa: " + r + ", " + F.get(r) + ", " + todo);
				
				if (todo.size() == 1 && todo.iterator().next().toString().startsWith("_"))
				{
					F.get(r).remove(bar);
					
					//System.out.println("bbb1: " + bar);
				}
				else if (!todo.isEmpty() && todo.stream().filter(v -> !R.keySet().contains(v)).allMatch(v -> v.toString().startsWith("_")))
				{
					todo.stream().filter(v -> v.toString().startsWith("_")).forEach(vvv -> dead.addAll(map.get(vvv)));
					rem.removeAll(dead);
					F.get(r).removeAll(dead);
					
					//System.out.println("bbb2: " + dead);
				}
			}
			
			Set<AssrtTrueFormula> tru = Stream.of(AssrtTrueFormula.TRUE).collect(Collectors.toSet());
			F.values().forEach(sss -> sss.removeAll(tru));

			//System.out.println("222: " + F.get(r) + "\n");

		}*/
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
		AssrtCoreESend es = this.Q.get(dest).get(src);
		return !(es instanceof AssrtCoreEBot) && !(es instanceof AssrtCoreEPendingRequest);
		//return es != null && es.equals(AssrtCoreEBot.ASSSRTCORE_BOT);  // Would be same as above
	}

	// req waiting for acc -- cf. reverse direction to isConnect
	private boolean isPendingRequest(Role req, Role acc)  // FIXME: for open/port annotations
	{
		//return (this.ports.get(r1).get(r2) != null) || (this.ports.get(r2).get(r1) != null);
		AssrtCoreESend es = this.Q.get(acc).get(req);  // N.B. reverse direction to isConnected
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
	
	public Map<Role, Map<Role, AssrtCoreEMessage>> getQ()
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
		String lab = "(P=" + this.P + ",\nQ=" + this.Q + ",\nR=" + this.R + ",\nK=" + this.K + ",\nF=" + this.F + ",\nrename=" + this.rename + ")";
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
	
	private static Map<Role, Map<Role, AssrtCoreEMessage>> makeQ(Set<Role> rs, boolean explicit)
	{
		AssrtCoreEMessage init = explicit ? AssrtCoreEBot.ASSSRTCORE_BOT : null;
		Map<Role, Map<Role, AssrtCoreEMessage>> res = new HashMap<>();
		for (Role r1 : rs)
		{
			HashMap<Role, AssrtCoreEMessage> tmp = new HashMap<>();
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
	
	private static Map<Role, Map<Role, AssrtCoreEMessage>> copyQ(Map<Role, Map<Role, AssrtCoreEMessage>> Q)
	{
		Map<Role, Map<Role, AssrtCoreEMessage>> copy = new HashMap<>();
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
				e -> e.getValue().getStateVars().entrySet().stream()
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
				e -> new HashMap<>(e.getValue().getStateVars())
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
	
	private static Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> copyRename(Map<Role, Map<AssrtIntVarFormula, AssrtIntVarFormula>> rename)
	{
		return rename.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> new HashMap<>(e.getValue())));
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
}


// Enqueued message
class AssrtCoreEMessage extends AssrtCoreESend
{
	public final Map<AssrtIntVarFormula, AssrtIntVarFormula> shadow;  // N.B. not in equals/hash
	
	/*public AssrtCoreEMessage(EModelFactory ef, AssrtCoreESend es)
	{
		this(ef, es.peer, es.mid, es.payload, es.ass, es.annot, es.expr);
	}*/

	public AssrtCoreEMessage(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload,
			AssrtBoolFormula ass, //AssrtDataTypeVar annot, AssrtArithFormula expr
			List<AssrtArithFormula> stateexprs)
	{
		this(ef, peer, mid, payload, ass, 
				//annot, expr,
				stateexprs,
				Collections.emptyMap());
	}

	public AssrtCoreEMessage(EModelFactory ef, Role peer, MessageId<?> mid, Payload payload,
			AssrtBoolFormula ass, //AssrtDataTypeVar annot, AssrtArithFormula expr,
			List<AssrtArithFormula> stateexprs,
			Map<AssrtIntVarFormula, AssrtIntVarFormula> shadow)
	{
		super(ef, peer, mid, payload, ass, //annot,
				stateexprs);
		this.shadow = Collections.unmodifiableMap(shadow);
	}

	@Override
	public String toString()
	{
		return super.toString() + this.shadow;
	} 

	@Override
	public int hashCode()
	{
		int hash = 6827;
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
		if (!(obj instanceof AssrtCoreEMessage))
		{
			return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public boolean canEqual(Object o)  // FIXME: rename canEquals
	{
		return o instanceof AssrtCoreEMessage;
	}
}

// \bot
class AssrtCoreEBot extends AssrtCoreEMessage
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
				//AssrtCoreEAction.DUMMY_VAR, AssrtIntValFormula.ZERO);
				Collections.emptyList());
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
class AssrtCoreEPendingRequest extends AssrtCoreEMessage  // Q stores ESends (not EConnect)
{
	public static final Payload ASSRTCORE_EMPTY_PAYLOAD =
			new Payload(Arrays.asList(new AssrtAnnotDataType(new AssrtDataTypeVar("_BOT"), AssrtCoreGProtocolDeclTranslator.UNIT_DATATYPE)));
			// Cf. Payload.EMPTY_PAYLOAD
	
	private final AssrtCoreERequest msg;  // Not included in equals/hashCode

	//public AssrtCoreEPendingConnection(AssrtEModelFactory ef, Role r, MessageId<?> op, Payload pay, AssrtBoolFormula ass)
	public AssrtCoreEPendingRequest(AssrtCoreERequest msg, Map<AssrtIntVarFormula, AssrtIntVarFormula> shadow)
	{
		super(null, msg.peer, msg.mid, msg.payload, msg.ass,  // HACK: null ef OK?  cannot access es.ef
				//AssrtCoreEAction.DUMMY_VAR, AssrtIntValFormula.ZERO,
				Collections.emptyList(),
				shadow);
		this.msg = msg;
	}
	
	public AssrtCoreERequest getMessage()
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
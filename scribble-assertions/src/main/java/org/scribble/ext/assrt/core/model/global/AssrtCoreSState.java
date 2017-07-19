package org.scribble.ext.assrt.core.model.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.ast.formula.BoolFormula;
import org.scribble.ext.assrt.model.endpoint.AssrtESend;
import org.scribble.ext.assrt.parser.assertions.ast.formula.AssrtFormulaFactoryImpl;
import org.scribble.ext.assrt.sesstype.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.model.MPrettyState;
import org.scribble.model.MState;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.EStateKind;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.global.actions.SAction;
import org.scribble.sesstype.kind.Global;
import org.scribble.sesstype.name.PayloadElemType;
import org.scribble.sesstype.name.Role;

public class AssrtCoreSState extends MPrettyState<Void, SAction, AssrtCoreSState, Global>
{
	//private static final F17EBot BOT = new F17EBot();
	
	// Cf. SState.config
	private final Map<Role, EState> P;
	private final Map<Role, Map<Role, AssrtESend>> Q;  // null value means connected and empty -- dest -> src -> msg
	
	/*private final Map<Role, Map<Role, PayloadVar>> ports;  // Server -> Client -> port
	private final Map<Role, Set<PayloadVar>> owned;*/
	
	// Cf. history sensitivity
	// FIXME: for now, assume globally distinct annot vars?  but conflicts with unfolding recursion annot vars
	private final Map<Role, Set<AssrtDataTypeVar>> K;  // "Knowledge" of annotation vars (payloads and recursions)
			// Currently assuming unique annot var declarations -- otherwise need to consider, e.g., A->B(x).C->B(x)
	private final Map<AssrtDataTypeVar, BoolFormula> F;  // For Set, need to do equals/hashCode

	//private final Map<AnnotRecVar, ...>  // For recursion anntoation var state?
	
	// Cf. temporal satisfiability?
	//private final Set<Set<BoolFormula>> F;  // FIXME: shouldn't be part of state?  i.e., at least, shouldn't "syntactically" distinguish states
	
	private final Set<Role> subjs = new HashSet<>();  // Hacky: mostly because EState has no self

	public AssrtCoreSState(Map<Role, EState> P, boolean explicit)
	{
		this(P, makeQ(P.keySet(), null), //explicit ? BOT : null),
				makeK(P.keySet()), new HashMap<>());
		
		if (explicit)
		{
			throw new RuntimeException("[assrt-core] TODO: explicit connections");
		}
	}

	// Pre: non-aliased "ownership" of all Map contents
	protected AssrtCoreSState(Map<Role, EState> P, Map<Role, Map<Role, AssrtESend>> Q,
			Map<Role, Set<AssrtDataTypeVar>> K, Map<AssrtDataTypeVar, BoolFormula> F)
	{
		super(Collections.emptySet());
		this.P = Collections.unmodifiableMap(P);
		this.Q = Collections.unmodifiableMap(Q);  // Don't need copyQ, etc. -- should already be fully "owned"
		this.K = Collections.unmodifiableMap(K);
		this.F = Collections.unmodifiableMap(F);
	}
	
	public void addSubject(Role subj)
	{
		this.subjs.add(subj);
	}
	
	public Set<Role> getSubjects()
	{
		return Collections.unmodifiableSet(this.subjs);
	}

	public Map<Role, EState> getP()
	{
		return this.P;
	}
	
	public Map<Role, Map<Role, AssrtESend>> getQ()
	{
		return this.Q;
	}

	/*// Error of opening a port when already connected or another port is still open
	public boolean isPortOpenError()
	{
		for (Entry<Role, EState> e : this.P.entrySet())
		{
			for (EAction a : e.getValue().getActions())
			{
				if (a.isSend())
				{
					for (PayloadType<?> pt : (Iterable<PayloadType<?>>) a.payload.elems.stream()
							.filter(x -> x instanceof AnnotType)::iterator)
					{
						if (pt instanceof AnnotPayloadType<?>)
						{
							// FIXME: factor out annot parsing
							AnnotPayloadType<?> apt = (AnnotPayloadType<?>) pt;
							String annot = ((AnnotString) apt.annot).val;
							String key = annot.substring(0, annot.indexOf("="));
							String val = annot.substring(annot.indexOf("=")+1,annot.length());
							if (key.equals("open"))
							{
								Role portRole = new Role(val);
								// FIXME: generalise
								if (isConnected(e.getKey(), portRole) || isPendingConnected(e.getKey(), portRole))
								{
									return true;
								}
							}
							else
							{
								throw new RuntimeException("[f17] TODO: " + a);
							}
						}
						else if (pt instanceof PayloadVar)
						{
							
						}
						else
						{
							throw new RuntimeException("[f17] TODO: " + a);
						}
					}
				}
			}
		}
		return false;
	}

	// Error of trying to send a PayloadVar that is not "owned" by the sender
	public boolean isPortOwnershipError()
	{
		for (Entry<Role, EState> e : this.P.entrySet())
		{
			for (EAction a : e.getValue().getActions())
			{
				if (a.isSend())
				{
					for (PayloadType<?> pt : (Iterable<PayloadType<?>>) a.payload.elems.stream()
							.filter(x -> x instanceof AnnotType)::iterator)
					{
						if (pt instanceof AnnotPayloadType<?>)
						{
							
						}
						else if (pt instanceof PayloadVar)
						{
							if (!this.owned.get(e.getKey()).contains((PayloadVar) pt))
							{
								return true;
							}
						}
						else
						{
							throw new RuntimeException("[f17] TODO: " + a);
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isConnectionError()
	{
		return this.P.entrySet().stream().anyMatch(e -> 
			e.getValue().getActions().stream().anyMatch(a ->
				(a.isConnect() || a.isAccept()) && isConnected(e.getKey(), a.peer) 

						// FIXME: check for pending port, if so then port is used -- need to extend an AnnotEConnect type with ScribAnnot (cf. AnnotPayloadType)

		));
	}

	public boolean isDisconnectedError()
	{
		return this.P.entrySet().stream().anyMatch((e) -> 
			e.getValue().getActions().stream().anyMatch((a) ->
				a.isDisconnect() && this.Q.get(e.getKey()).get(a.peer) != null
		));
	}

	public boolean isUnconnectedError()
	{
		return this.P.entrySet().stream().anyMatch((e) -> 
			e.getValue().getActions().stream().anyMatch((a) ->
				(a.isSend() || a.isReceive()) && !isConnected(e.getKey(), a.peer)
		));
	}

	public boolean isSynchronisationError()
	{
		return this.P.entrySet().stream().anyMatch((e) -> 
			e.getValue().getActions().stream().anyMatch((a) ->
				{
					EState peer;
					return a.isConnect() && (peer = this.P.get(a.peer)).getStateKind() == EStateKind.ACCEPT
							&& (peer.getActions().iterator().next().peer.equals(e.getKey()))  // E.g. A->>B.B->>C.A->>C
							&& !(peer.getActions().contains(a.toDual(e.getKey())));
				}
		));
	}*/

	public boolean isReceptionError()
	{
		return this.Q.entrySet().stream().anyMatch((e1) ->
				e1.getValue().entrySet().stream().anyMatch((e2) ->
					{
						EState s;
						return hasMessage(e1.getKey(), e2.getKey())
								&& ((s = this.P.get(e1.getKey())).getStateKind() == EStateKind.UNARY_INPUT
										|| s.getStateKind() == EStateKind.POLY_INPUT)
								&& (s.getActions().iterator().next().peer.equals(e2.getKey()))  // E.g. A->B.B->C.A->C
								&& !s.getActions().contains(e2.getValue().toDual(e2.getKey()));
					}
				));
	}

	public boolean isUnfinishedRoleError(Map<Role, EState> E0)
	{
		return this.isTerminal() &&
				this.P.entrySet().stream().anyMatch(e -> isActive(e.getValue(), E0.get(e.getKey()).id));
	}

	public boolean isOrphanError(Map<Role, EState> E0)
	{
		/*return this.P.entrySet().stream().anyMatch((e) -> isInactive(e.getValue(), E0.get(e.getKey()).id)
				&& (this.P.keySet().stream().anyMatch((r) -> hasMessage(e.getKey(), r))));*/
		return this.P.entrySet().stream().anyMatch((e) -> isInactive(e.getValue(), E0.get(e.getKey()).id)
				&& (this.P.keySet().stream().anyMatch((r) -> hasMessage(e.getKey(), r))
						//|| !this.owned.get(e.getKey()).isEmpty()  
						
								// FIXME: need AnnotEConnect to consume owned properly

				));
	}
	
	public Map<Role, List<EAction>> getFireable()
	{
		Map<Role, List<EAction>> res = new HashMap<>();
		for (Entry<Role, EState> e : this.P.entrySet())
		{
			Role self = e.getKey();
			EState s = e.getValue();
			res.put(self, new LinkedList<>());
			for (EAction a : s.getActions())
			{
				if (a.isSend())
				{
					AssrtESend es = (AssrtESend) a;
					getSendFireable(res, self, es);
				}
				else if (a.isReceive())
				{
					EReceive er = (EReceive) a;
					getReceiveFireable(res, self, er);
				}
				/*else if (a.isConnect())
				{
					EConnect lo = (EConnect) a;
					getConnectFireable(res, self, a, lo);
				}
				else if (a.isAccept())
				{
					EAccept la = (EAccept) a;
					getAcceptFireable(res, self, la);
				}
				else if (a.isDisconnect())
				{
					EDisconnect ld = (EDisconnect) a;
					getDisconnectFireable(res, self, ld);
				}*/
				else
				{
					throw new RuntimeException("[f17] Shouldn't get in here: " + a);
				}
			}
		}
		return res;
	}

	private void getSendFireable(Map<Role, List<EAction>> res, Role self, AssrtESend es)
	{
		if //(!isConnected(self, es.peer) || this.Q.get(es.peer).get(self) != null)
				(!hasMessage(self, es.peer))  // FIXME: for connect
		{
			return;
		}

		// Check assertion?
		//boolean ok = JavaSmtWrapper.getInstance().isSat(es.assertion.getFormula(), context);
		boolean ok = true;
		/*for (PayloadElemType<?> pt : (Iterable<PayloadElemType<?>>) 
				es.payload.elems.stream().filter(x -> x instanceof AssrtPayloadElemType<?>)::iterator)*/
		PayloadElemType<?> pt = es.payload.elems.get(0);  // assrt-core is hardcoded to one payload elem (empty source payload is filled in)
		{
			if (pt instanceof AssrtAnnotDataType)
			{
				// OK
			}
			else if (pt instanceof AssrtDataTypeVar)
			{
				/*if (!this.K.get(self).contains(pt))
				{
					ok = false;
					break;
				}*/
				throw new RuntimeException("[assrt-core] Shouldn't get in here: " + pt);  // "Encode" pay elem vars by fresh annot data elems for now
			}
			else
			{
				throw new RuntimeException("[assrt-core] TODO: " + pt);
			}
		}
		if (ok)
		{	
			res.get(self).add(es);
		}
	}

	private void getReceiveFireable(Map<Role, List<EAction>> res, Role self, EReceive er)
	{
		if (hasMessage(self, er.peer))
		{
			AssrtESend m = this.Q.get(self).get(er.peer);
			if (er.toDual(self).equals(m))  //&& !(m instanceof F17EBot)
			{
				res.get(self).add(er);
			}
		}
	}

	/*private void getConnectFireable(Map<Role, List<EAction>> res, Role self, EAction a, EConnect lo)
	{
		if (this.Q.get(self).get(lo.peer) instanceof F17EBot      // FIXME: !isConnected
				&& this.Q.get(lo.peer).get(self) instanceof F17EBot)
		{
			EState plt = this.P.get(lo.peer);
			if (plt.getActions().contains(lo.toDual(self)))
			{

				boolean ok = true;
				for (PayloadType<?> pt : (Iterable<PayloadType<?>>) a.payload.elems.stream()
						.filter((x) -> x instanceof AnnotType)::iterator)
				{
					if (pt instanceof AnnotPayloadType<?>)
					{
						AnnotPayloadType<?> apt = (AnnotPayloadType<?>) pt;
						String annot = ((AnnotString) apt.annot).val;
						String key = annot.substring(0, annot.indexOf("="));
						String val = annot.substring(annot.indexOf("=")+1,annot.length());
						if (key.equals("port"))
						{
							Role portRole = new Role(val);
							if (isConnected(self, portRole) || isPendingConnected(self, portRole))
							{
								ok = false;
								break;
							}
							if (!val.equals(lo.peer.toString()))
							{
								ok = false;
								break;
							}
						}
						else  // TODO: connect-with-message could also be open-annot
						{
							throw new RuntimeException("[f17] TODO: " + a);
						}
					}
					else if (pt instanceof PayloadVar)  // Check linear ownership of port
					{
						if (!this.owned.get(self).contains(pt) || !pt.equals(this.ports.get(lo.peer).get(self)))
						{
							ok = false;
							break;
						}
					}
					else
					{
						throw new RuntimeException("[f17] TODO: " + a);
					}
				}
				if (ok)
				{	
					res.get(self).add(lo);
				}

			}
		}
	}

	private void getAcceptFireable(Map<Role, List<EAction>> res, Role self, EAccept la)
	{
		if (this.Q.get(self).get(la.peer) instanceof F17EBot      // FIXME: !isConnected
				&& this.Q.get(la.peer).get(self) instanceof F17EBot)
		{
			EState plt = this.P.get(la.peer);
			if (plt.getActions().contains(la.toDual(self)))
			{
				res.get(self).add(la);
			}
		}
	}

	private void getDisconnectFireable(Map<Role, List<EAction>> res, Role self, EDisconnect ld)
	{
		if (!(this.Q.get(self).get(ld.peer) instanceof F17EBot)  // FIXME: isConnected
				&& this.Q.get(self).get(ld.peer) == null)
		{
			res.get(self).add(ld);
		}
	}*/
	
	public AssrtCoreSState fire(Role self, EAction a)  // Deterministic
	{
		Map<Role, EState> P = new HashMap<>(this.P);
		Map<Role, Map<Role, AssrtESend>> Q = AssrtCoreSState.copyQ(this.Q);
		Map<Role, Set<AssrtDataTypeVar>> K = copyK(this.K);
		EState succ = P.get(self).getSuccessor(a);

		if (a.isSend())
		{
			AssrtESend es = (AssrtESend) a;
			P.put(self, succ);
			Q.get(es.peer).put(self, es);
			
			for (PayloadElemType<?> pt : (Iterable<PayloadElemType<?>>) 
						a.payload.elems.stream().filter(x -> x instanceof AssrtDataTypeVar)::iterator)
			{
				if (pt instanceof AssrtAnnotDataType)
				{
					// Update knowledge
					AssrtDataTypeVar v = ((AssrtAnnotDataType) pt).var;
					putK(K, self, v);
					putK(K, es.peer, v);
					
					//...record assertions so far -- later error checking: *for all* values that satisify those, it should imply the next assertion
					putF(F, v, es.assertion.getFormula());
					
					//...can only send if true? (by API gen assertion failure means definitely not sending it) -- unsat as bad terminal state (safety error)?  no: won't catch all assert errors (branches)
					// check assertion satisfiable?  i.e., satisfiability part of operational semantics for model construction? or just record constraints and check later?
					// -- current assertions *imply* additional ones?
						
					// assertion error as queue token? for error preservation -- Cf. "decoupled" request/accept
				}
				/*else if (pt instanceof PayloadVar)  // Should not be used (for now), can encode
				{
					// Check known, and not "ambiguous" -- no for latter: that is error checking

					PayloadVar pv = (PayloadVar) pt;
					owned.get(self).remove(pv);
					owned.get(es.peer).add(pv);
				}*/
				else
				{
					throw new RuntimeException("[f17] TODO: " + a);
				}
			}

		}
		else if (a.isReceive())
		{
			EReceive lr = (EReceive) a;
			P.put(self, succ);
			Q.get(self).put(lr.peer, null);
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
		return new AssrtCoreSState(P, Q, K, F);
	}

	/*// "Synchronous version" of fire
	public AssrtCoreSState sync(Role r1, EAction a1, Role r2, EAction a2)
	{
		Map<Role, EState> P = new HashMap<>(this.P);
		Map<Role, Map<Role, AssrtESend>> Q = copyQ(this.Q);
		Map<Role, Map<Role, PayloadVar>> ports = copyPorts(this.ports);
		Map<Role, Set<PayloadVar>> owned = copyOwned(this.owned);
		EState succ1 = P.get(r1).getSuccessor(a1);
		EState succ2 = P.get(r2).getSuccessor(a2);

		if ((a1.isConnect() && a2.isAccept())
				|| (a1.isAccept() && a2.isConnect()))
		{
			P.put(r1, succ1);
			P.put(r2, succ2);
			Q.get(r1).put(r2, null);
			Q.get(r2).put(r1, null);

			Role cself = a1.isConnect() ? r1 : r2;
			Role cpeer = a1.isConnect() ? r2 : r1;
			EConnect ec = (EConnect) (a1.isConnect() ? a1 : a2);
			
			//if (...)  // FIXME: check if port pending, if so then correct port used -- need AnnotEConnect type -- cf., isConnectionError
			{
				ports.get(cpeer).put(cself, null); // HACK FIXME: incorrect without proper checks
				//owned.get(cself).clear();  // Hack doesn't work: will clear others' pending ports
			}
			
			for (PayloadElemType<?> pt : (Iterable<PayloadElemType<?>>) ec.payload.elems.stream()
					.filter((x) -> x instanceof AnnotType)::iterator)
			{
				if (pt instanceof AnnotPayloadType<?>)
				{
					AnnotPayloadType<?> apt = (AnnotPayloadType<?>) pt;
					String annot = ((AnnotString) apt.annot).val;
					String key = annot.substring(0, annot.indexOf("="));
					String val = annot.substring(annot.indexOf("=")+1,annot.length());
					if (key.equals("open"))  // Duplicated from fire/isSend -- opening+passing a port as part of connect
					{
						Role portRole = new Role(val);
						ports.get(cself).put(portRole, apt.var);
						owned.get(cpeer).add(apt.var);
					}
					else
					{
						throw new RuntimeException("[f17] TODO: " + ec);
					}
				}
				else if (pt instanceof PayloadVar)
				{
					PayloadVar pv = (PayloadVar) pt;
					owned.get(cself).remove(pv);
					owned.get(cpeer).add(pv);
				}
				else
				{
					throw new RuntimeException("[f17] TODO: " + ec);
				}
			}
		}
		else
		{
			throw new RuntimeException("[f17] Shouldn't get in here: " + a1 + ", " + a2);
		}
		return new AssrtCoreSState(P, Q, ports, owned);
	}*/
	
	@Override
	protected String getNodeLabel()
	{
		String lab = "(P=" + this.P + ", Q=" + this.Q + ", K=" + this.K + ", F=" + this.F + ")";
		//return "label=\"" + this.id + ":" + lab.substring(1, lab.length() - 1) + "\"";
		return "label=\"" + this.id + ":" + lab + "\"";
	}
	
	private boolean hasMessage(Role self, Role peer)
	{
		AssrtESend m = this.Q.get(self).get(peer);
		return m != null;// && !(m instanceof F17EBot);
	}

	@Override
	public void addEdge(SAction a, AssrtCoreSState s)  // Visibility hack (for F17SModelBuilder.build)
	{
		super.addEdge(a, s);
	}
	
	// isActive(SState, Role) becomes isActive(EState)
	public static boolean isActive(EState s, int init)
	{
		return !isInactive(s, init);
	}
	
	private static boolean isInactive(EState s, int init)
	{
		//return s.isTerminal() || (s.id == init && s.getStateKind() == EStateKind.ACCEPT);
		return s.isTerminal();
				// s.isTerminal means non-empty actions (i.e., edges) -- i.e., non-end (cf., fireable)
	}
	
	@Override
	public final int hashCode()
	{
		int hash = 79;
		hash = 31 * hash + this.P.hashCode();
		hash = 31 * hash + this.Q.hashCode();
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
		return them.canEquals(this) && this.P.equals(them.P) && this.Q.equals(them.Q)
				&& this.K.equals(them.K) && this.F.equals(them.F);
	}

	@Override
	protected boolean canEquals(MState<?, ?, ?, ?> s)
	{
		return s instanceof AssrtCoreSState;
	}
	
	private static Map<Role, Map<Role, AssrtESend>> makeQ(Set<Role> rs, AssrtESend init)
	{
		Map<Role, Map<Role, AssrtESend>> res = new HashMap<>();
		for (Role r : rs)
		{
			HashMap<Role, AssrtESend> tmp = new HashMap<>();
			for (Role rr : rs)
			{
				if (!rr.equals(r))
				{
					tmp.put(rr, init);
				}
			}
			res.put(r, tmp);
		}
		return res;
	}
	
	private static Map<Role, Map<Role, AssrtESend>> copyQ(Map<Role, Map<Role, AssrtESend>> Q)
	{
		Map<Role, Map<Role, AssrtESend>> copy = new HashMap<>();
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
		if (tmp == null)
		{
			tmp = new HashSet<>();
			K.put(r, tmp);
		}
		tmp.add(v);
	}
	
	private void putF(Map<AssrtDataTypeVar, BoolFormula> F, AssrtDataTypeVar v, BoolFormula f)
	{
		BoolFormula tmp = this.F.get(v);
		if (tmp != null)
		{
			f = AssrtFormulaFactoryImpl.BinBoolFormula("&&", tmp, f);  // FIXME: factor out constant
					// To store as Set, need equals/hashCode for SmtFormula
		}
		this.F.put(v, f);
	}
	
	/*private static Map<Role, Map<Role, PayloadVar>> copyPorts(Map<Role, Map<Role, PayloadVar>> ports)
	{
		Map<Role, Map<Role, PayloadVar>> copy = new HashMap<>();
		for (Role r : ports.keySet())
		{
			copy.put(r, new HashMap<>(ports.get(r)));
		}
		return copy;
	}

	private static Map<Role, Set<PayloadVar>> makeOwned(Set<Role> rs)
	{
		return rs.stream().collect(Collectors.toMap((r) -> r, (r) -> new HashSet<>()));
	}
	
	private static Map<Role, Set<PayloadVar>> copyOwned(Map<Role, Set<PayloadVar>> owned)
	{
		return owned.entrySet().stream().collect(Collectors.toMap((e) -> e.getKey(), (e) -> new HashSet<>(e.getValue())));
	}*/
	
	/*// Direction sensitive (not symmetric)
	private boolean isConnected(Role r1, Role r2)  // N.B. is more like the "input buffer" at r1 for r2 -- not the actual "connection from r1 to r2"
	{
		return !(this.Q.get(r1).get(r2) instanceof F17EBot);
	}

	private boolean isPendingConnected(Role r1, Role r2)
	{
		return (this.ports.get(r1).get(r2) != null) || (this.ports.get(r2).get(r1) != null);
	}*/
}


/*class F17EBot extends ESend
{
	public F17EBot()
	{
		super(null, Role.EMPTY_ROLE, Op.EMPTY_OPERATOR, Payload.EMPTY_PAYLOAD);  // null ef OK?
	}

	@Override
	public EReceive toDual(Role self)
	{
		throw new RuntimeException("Shouldn't get in here: " + this);
		//return this;
	}
	
	@Override
	public boolean isSend()
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return "#";
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
		if (!(obj instanceof F17EBot))
		{
			return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public boolean canEqual(Object o)  // FIXME: rename canEquals
	{
		return o instanceof F17EBot;
	}
}*/

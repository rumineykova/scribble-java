package org.scribble.ext.assrt.model.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EFsm;
import org.scribble.core.model.endpoint.EState;
import org.scribble.core.model.endpoint.actions.EAction;
import org.scribble.core.model.endpoint.actions.ERecv;
import org.scribble.core.model.endpoint.actions.ESend;
import org.scribble.core.model.global.SConfig;
import org.scribble.core.model.global.SingleBuffers;
import org.scribble.core.type.kind.PayElemKind;
import org.scribble.core.type.name.PayElemType;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.model.endpoint.action.AssrtERecv;
import org.scribble.ext.assrt.model.endpoint.action.AssrtESend;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtLogFormula;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.type.name.AssrtPayloadElemType;
import org.scribble.ext.assrt.util.JavaSmtWrapper;

// FIXME: equals/hashCode -- done?
// FIXME: override getFireable to check send ass implies recv ass
public class AssrtSConfig extends SConfig
{
	public final AssrtLogFormula formula;
	public final Map<Role, Set<String>> varsInScope; 
	
	protected AssrtSConfig(ModelFactory mf, Map<Role, EFsm> state,
			SingleBuffers buffs, AssrtLogFormula formula,
			Map<Role, Set<String>> varsInScope)
	{
		super(mf, state, buffs);
		this.formula = formula; 
		this.varsInScope = Collections.unmodifiableMap(varsInScope);
	}
	
	// FIXME: factor out better with super?
	@Override
	//public List<ASConfig> fire(Role r, EAction a)
	public List<SConfig> async(Role r, EAction a)
	{
		//List<ASConfig> res = new LinkedList<>();
		List<SConfig> res = new LinkedList<>();
		
		//List<EndpointState> succs = this.states.get(r).takeAll(a);
		List<EFsm> succs = this.efsms.get(r).getSuccs(a);
		//for (EndpointState succ : succs)
		for (EFsm succ : succs)
		{
			//Map<Role, EndpointState> tmp1 = new HashMap<>(this.states);
			Map<Role, EFsm> tmp1 = new HashMap<>(this.efsms);
			//Map<Role, Map<Role, Send>> tmp2 = new HashMap<>(this.buffs);
		
			tmp1.put(r, succ);

			/*Map<Role, Send> tmp3 = new HashMap<>(tmp2.get(a.peer));
			tmp2.put(a.peer, tmp3);* /
			Map<Role, Send> tmp3 = tmp2.get(a.peer);
			if (a.isSend())
			{
				tmp3.put(r, (Send) a);
			}
			else
			{
				tmp3.put(r, null);
			}*/
			SingleBuffers tmp2 = 
						//a.isSend()       ? this.buffs.send(r, (ESend) a)
						a.isSend()       ? this.queues.send(r, ((AssrtESend) a).toTrueAssertion())  // HACK FIXME: project receive assertion properly and check implication 
					
					: a.isReceive()    ? this.queues.receive(r, (ERecv) a)
					//: a.isDisconnect() ? this.buffs.disconnect(r, (EDisconnect) a)
					: null;
			if (tmp2 == null)
			{
				throw new RuntimeException("Shouldn't get in here: " + a);
			}
			
			AssrtBoolFormula assertion;
			if (a.isSend()) 
			{
				assertion = ((AssrtESend) a).ass;
			}
			else if (a.isReceive()) 
			{
				assertion = ((AssrtERecv) a).ass;
			}
			else
			{
				throw new RuntimeException("[assrt] TODO: " + a);
			}
			
			AssrtLogFormula newFormula = null; 
		
			if (assertion!=null) {
				AssrtBoolFormula currFormula = assertion;
				
				//try
				{
					newFormula = this.formula==null?
							new AssrtLogFormula(currFormula.getJavaSmtFormula(), currFormula.getIntVars()):
							this.formula.addFormula(currFormula);
				}
				/*catch (AssertionParseException e)
				{
					throw new RuntimeException("cannot parse the asserion"); 
				}*/
			}

			// maybe we require a copy this.formula here?
			AssrtLogFormula nextFormula = newFormula == null ? this.formula : newFormula;

			Map<Role, Set<String>> vars = new HashMap<Role, Set<String>>(this.varsInScope);

			if (a.isSend())
			{
				for (PayElemType<? extends PayElemKind> elem : a.payload.elems)
				{
					if (elem instanceof AssrtPayloadElemType<?>) // FIXME?
					{
						AssrtPayloadElemType<?> apt = (AssrtPayloadElemType<?>) elem;
						if (apt.isAnnotVarDecl() || apt.isAnnotVarName())
						{
							String varName;
							if (apt.isAnnotVarDecl())
							{
								varName = ((AssrtAnnotDataType) elem).var.toString();

								if (!vars.containsKey(r))
								{
									vars.put(r, new HashSet<String>());
								}
								vars.get(r).add(varName);
							}
							else
							{
								varName = ((AssrtDataTypeVar) elem).toString();
							}

							if (!vars.containsKey(a.obj))
							{
								vars.put(a.obj, new HashSet<String>());
							}

							vars.get(a.obj).add(varName);
						}
					}
				}
			}

			res.add(((AssrtSModelFactory) this.mf.global).newAssrtSConfig(tmp1, tmp2,
					nextFormula, vars));
					// FIXME: factor out with sync and with SConfig -- make an SModelBuilder (factored out with SGraph.buildSGraph), cf. AstFactory
		}

		return res;
	}

	@Override
	public List<SConfig> sync(Role r1, EAction a1, Role r2, EAction a2)
	{
		AssrtSModelFactory sf = (AssrtSModelFactory) this.mf.global;
		return super.sync(r1, a1, r2, a2).stream().map(c -> sf  // Inefficient, but reduces code duplication
				.newAssrtSConfig(c.efsms, c.queues, this.formula, this.varsInScope))
				.collect(Collectors.toList());
	}

	public Map<Role, EState> getVarsNotInScope()
	{
		throw new RuntimeException("[TODO]: ");
	}
	
	// For now we are checking that only the sender knows all variables. 
	public Map<Role, EState> checkHistorySensitivity()  // Not the full "formal" HS -- here, checking again "knowledge by message flow"? (already done syntactically?)
	{
		Map<Role, EState> res = new HashMap<>();
		for (Role r : this.efsms.keySet())
		{
			Set<ESend> unknownVars = new HashSet<ESend>(); 
			EFsm s = this.efsms.get(r);
			for (EAction action : s.curr.getActions())  
			{
				if (action.isSend())
				{
					AssrtESend send = (AssrtESend)action;
					AssrtBoolFormula assertion = send.ass;
					
					Set<String> newVarNames = send.payload.elems.stream()
							.filter(v -> (v instanceof AssrtPayloadElemType<?>) && ((AssrtPayloadElemType<?>) v).isAnnotVarDecl())  // FIXME?
							.map(v -> ((AssrtAnnotDataType) v).var.toString())
							.collect(Collectors.toSet()); 
					
					if (assertion !=null)
					{
						Set<String> varNames = assertion.getIntVars().stream().map(v -> v.toString()).collect(Collectors.toSet());
						varNames.removeAll(newVarNames); 
						if ((!varNames.isEmpty()) && (!this.varsInScope.containsKey(r) ||
							 !this.varsInScope.get(r).containsAll(varNames)))
							unknownVars.add(send); 
					}
				}
				if (!unknownVars.isEmpty())
				{
					res.put(r, this.efsms.get(r).curr);
				}
				
				unknownVars.clear();
			}
		}
		return res;
	}
	
	public Map<Role, EState> getUnsatAssertions()
	{
		Map<Role, EState> res = new HashMap<>();
		for (Role r : this.efsms.keySet())
		{
			Set<ESend> unsafStates = new HashSet<ESend>(); 
			EFsm s = this.efsms.get(r);
			for (EAction action : s.curr.getActions())  
			{
				if (action.isSend()) {
					AssrtESend send = (AssrtESend)action;
					AssrtBoolFormula assertion = send.ass; 
					if (assertion != null)
					{
						if (!JavaSmtWrapper.getInstance().isSat(assertion, this.formula)) {
							unsafStates.add(send); 
						}
					}
				}
				if (!unsafStates.isEmpty())
				{
					res.put(r, this.efsms.get(r).curr);
				}
				unsafStates.clear();
			}
		}
		return res;
	}

	@Override
	public final int hashCode()
	{
		int hash = 5507;
		hash = 31 * hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtSConfig))
		{
			return false;
		}
		return super.equals(o);
	}
}

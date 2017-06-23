/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.ext.assrt.model.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.ast.AssrtAssertionNode;
import org.scribble.ext.assrt.ast.formula.AssertionException;
import org.scribble.ext.assrt.ast.formula.AssertionLogFormula;
import org.scribble.ext.assrt.ast.formula.StmFormula;
import org.scribble.ext.assrt.model.endpoint.AssrtESend;
import org.scribble.ext.assrt.sesstype.AssrtAnnotPayload;
import org.scribble.ext.assrt.sesstype.name.AssrtAnnotVarName;
import org.scribble.ext.assrt.sesstype.name.AssrtPayloadType;
import org.scribble.ext.assrt.util.SMTWrapper;
import org.scribble.model.endpoint.EFSM;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.model.endpoint.actions.EDisconnect;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.model.global.SBuffers;
import org.scribble.model.global.SConfig;
import org.scribble.model.global.SModelFactory;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.name.PayloadType;
import org.scribble.sesstype.name.Role;

// FIXME: equals/hashCode
public class AssrtSConfig extends SConfig
{
	public final AssertionLogFormula formula;
	public final Map<Role, Set<String>> variablesInScope; 
	
	protected AssrtSConfig(SModelFactory sf, Map<Role, EFSM> state, SBuffers buffs, AssertionLogFormula formula, Map<Role, Set<String>> variablesInScope)
	{
		super(sf, state, buffs);
		this.formula = formula; 
		this.variablesInScope = Collections.unmodifiableMap(variablesInScope);
	}
	
	@Override
	//public List<ASConfig> fire(Role r, EAction a)
	public List<SConfig> fire(Role r, EAction a)  // FIXME
	{
		//List<ASConfig> res = new LinkedList<>();
		List<SConfig> res = new LinkedList<>();  // FIXME
		
		//List<EndpointState> succs = this.states.get(r).takeAll(a);
		List<EFSM> succs = this.efsms.get(r).fireAll(a);
		//for (EndpointState succ : succs)
		for (EFSM succ : succs)
		{
			//Map<Role, EndpointState> tmp1 = new HashMap<>(this.states);
			Map<Role, EFSM> tmp1 = new HashMap<>(this.efsms);
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
			SBuffers tmp2 = 
					a.isSend()       ? this.buffs.send(r, (ESend) a)
				: a.isReceive()    ? this.buffs.receive(r, (EReceive) a)
				: a.isDisconnect() ? this.buffs.disconnect(r, (EDisconnect) a)
				: null;
			if (tmp2 == null)
			{
				throw new RuntimeException("Shouldn't get in here: " + a);
			}
			
			AssrtAssertionNode assertion = a.isSend() ? ((AssrtESend) a).assertion: null; 
			
			AssertionLogFormula newFormula = null; 
		
			if (assertion!=null) {
				StmFormula currFormula = assertion.toFormula();
				
				try {
					newFormula = this.formula==null?
							new AssertionLogFormula(currFormula.getFormula(), currFormula.getVars()):
							this.formula.addFormula(currFormula);
				} catch (AssertionException e) {
					throw new RuntimeException("cannot parse the asserion"); 
				}
			}

			// maybe we require a copy this.formula here?
			AssertionLogFormula nextFormula = newFormula == null ? this.formula : newFormula;

			Map<Role, Set<String>> vars = new HashMap<Role, Set<String>>(this.variablesInScope);

			if (a.isSend())
			{
				for (PayloadType<? extends PayloadTypeKind> elem : a.payload.elems)
				{
					if (elem instanceof AssrtPayloadType<?>) // FIXME?
					{
						AssrtPayloadType<?> apt = (AssrtPayloadType<?>) elem;
						if (apt.isAnnotPayloadDecl() || apt.isAnnotPayloadInScope())
						{
							String varName;
							if (apt.isAnnotPayloadDecl())
							{
								varName = ((AssrtAnnotPayload) elem).varName.toString();

								if (!vars.containsKey(r))
								{
									vars.put(r, new HashSet<String>());
								}
								vars.get(r).add(varName);
							}
							else
							{
								varName = ((AssrtAnnotVarName) elem).toString();
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

			res.add(((AssrtSModelFactory) this.sf).newAssrtSConfig(tmp1, tmp2, nextFormula, vars));  // FIXME: factor out with sync and with SConfig -- make an SModelBuilder (factored out with SGraph.buildSGraph), cf. AstFactory
		}

		return res;
	}

	public List<SConfig> sync(Role r1, EAction a1, Role r2, EAction a2)
	{
		List<SConfig> res = new LinkedList<>();
		
		List<EFSM> succs1 = this.efsms.get(r1).fireAll(a1);
		List<EFSM> succs2 = this.efsms.get(r2).fireAll(a2);
		for (EFSM succ1 : succs1)
		{
			//for (EndpointState succ2 : succs2)
			for (EFSM succ2 : succs2)
			{
				//Map<Role, EndpointState> tmp1 = new HashMap<>(this.states);
				Map<Role, EFSM> tmp1 = new HashMap<>(this.efsms);
				tmp1.put(r1, succ1);
				tmp1.put(r2, succ2);
				SBuffers tmp2;
				if (((a1.isConnect() && a2.isAccept()) || (a1.isAccept() && a2.isConnect())))
						//&& this.buffs.canConnect(r1, r2))
				{
					tmp2 = this.buffs.connect(r1, r2);
				}
				else if (((a1.isWrapClient() && a2.isWrapServer()) || (a1.isWrapServer() && a2.isWrapClient())))
				{
					tmp2 = this.buffs;  // OK, immutable?
				}
				else
				{
					throw new RuntimeException("Shouldn't get in here: " + a1 + ", " + a2);
				}
				
				res.add(((AssrtSModelFactory) this.sf).newAssrtSConfig(tmp1, tmp2, this.formula, this.variablesInScope));  // FIXME: factor out with sync and with SConfig -- make an SModelBuilder (factored out with SGraph.buildSGraph), cf. AstFactory
			}
		}

		return res;
	}
	
	public Map<Role, EState> getUnsatAssertions() {
		Map<Role, EState> res = new HashMap<>();
		for (Role r : this.efsms.keySet())
		{
			Set<ESend> unsafStates = new HashSet<ESend>(); 
			EFSM s = this.efsms.get(r);
			for (EAction action : s.getAllFireable())  
			{
				if (action.isSend()) {
					ESend send = (ESend)action;
					AssrtAssertionNode assertion = ((AssrtESend) send).assertion; 
					if (assertion !=null)
					{
						if (!SMTWrapper.getInstance().isSat(assertion.toFormula(), this.formula)) {
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
	
	// For now we are checking that only the sender knows all variables. 
	public Map<Role, EState> checkHistorySensitivity() {
		Map<Role, EState> res = new HashMap<>();
		for (Role r : this.efsms.keySet())
		{
			Set<ESend> unknownVars = new HashSet<ESend>(); 
			EFSM s = this.efsms.get(r);
			for (EAction action : s.getAllFireable())  
			{
				if (action.isSend()) {
					AssrtESend send = (AssrtESend)action;
					AssrtAssertionNode assertion = send.assertion;
					
					Set<String> newVarNames = send.payload.elems.stream()
							.filter(v -> (v instanceof AssrtPayloadType<?>) && ((AssrtPayloadType<?>) v).isAnnotPayloadDecl())  // FIXME?
							.map(v -> ((AssrtAnnotPayload)v).varName.toString())
							.collect(Collectors.toSet()); 
					
					if (assertion !=null)
					{
						Set<String> varNames = assertion.toFormula().getVars();
						varNames.removeAll(newVarNames); 
						if ((!varNames.isEmpty()) && (!this.variablesInScope.containsKey(r) ||
							 !this.variablesInScope.get(r).containsAll(varNames)))
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

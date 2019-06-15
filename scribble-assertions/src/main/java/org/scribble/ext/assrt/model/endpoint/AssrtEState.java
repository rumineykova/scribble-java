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
package org.scribble.ext.assrt.model.endpoint;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.core.model.MState;
import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EState;
import org.scribble.core.model.endpoint.actions.EAction;
import org.scribble.core.type.name.RecVar;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBinBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtFormulaFactory;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.core.type.name.AssrtIntVar;

public class AssrtEState extends EState
{
	private final LinkedHashMap<AssrtIntVar, AssrtAFormula> statevars; // Note: even with syntactic single var per rec, nested recs can lead to mulitple vars per state
			// CHECKME: supported outside of assrt-core?
	
	private //final
			AssrtBFormula ass;  // TODO FIXME: make Set -- and eliminate placeholder True from various, use empty set instead

	// FIXME: make AssrtIntTypeVar?
	protected AssrtEState(Set<RecVar> labs, LinkedHashMap<AssrtIntVar, AssrtAFormula> vars,
			AssrtBFormula ass)  // FIXME: currently syntactically restricted to one annot var
	{
		super(labs);
		//this.vars = Collections.unmodifiableMap(vars);
		this.statevars = new LinkedHashMap<>(vars);  // N.B. mutated by addStateVars
		this.ass = ass;
	}
	
	@Override
	protected AssrtEState cloneNode(ModelFactory mf, Set<RecVar> labs)
	{
		return ((AssrtEModelFactory) mf.local).newAssrtEState(labs, this.statevars,
				this.ass);
	}
	
	public LinkedHashMap<AssrtIntVar, AssrtAFormula> getStateVars()
	{
		return this.statevars;
	}
	
	public AssrtBFormula getAssertion()
	{
		return this.ass;
	}

	// For public access, do via AssrtEGraphBuilderUtil
	protected final void addStateVars(LinkedHashMap<AssrtIntVar, AssrtAFormula> svars,
			AssrtBFormula ass)
	{
		this.statevars.putAll(svars);  // FIXME: ordering w.r.t. nested recs (i.e., multiple calls to here)
		
		this.ass = (this.ass.equals(AssrtTrueFormula.TRUE))
				? ass
				: AssrtFormulaFactory.AssrtBinBool(AssrtBinBFormula.Op.And, this.ass, ass);  // FIXME: make Set
	}

	@Override
	protected String getNodeLabel()
	{
		String labs = this.labs.toString();
		return "label=\"" + this.id + ": " + labs.substring(1, labs.length() - 1)
				/*+ (this.statevars.isEmpty() ? "" : ", " + this.statevars)
				+ (this.ass.equals(AssrtTrueFormula.TRUE) ? "" : ", " + this.ass)*/
				+ " <" + this.statevars.entrySet().stream()
						.map(x -> x.getKey() + ":=" + x.getValue())
						.collect(Collectors.joining(", "))
				+ "> " + this.ass
				+ "\"";  // FIXME: would be more convenient for this method to return only the label body
	}

	@Override
	protected String getEdgeLabel(EAction msg)
	{
		return "label=\"" + msg.toString().replaceAll("\\\"", "\\\\\"") + "\"";
				// Because of @"..." syntax, need to escape the quotes
	}
	
	@Override
	public AssrtEState getDetSucc(EAction a)
	{
		return (AssrtEState) super.getDetSucc(a);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 6619;
		hash = 31 * hash + super.hashCode();  // N.B. uses state ID only -- following super pattern
		hash = 31 * hash + this.statevars.hashCode();
		hash = 31 * hash + this.ass.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AssrtEState))
		{
			return false;
		}
		AssrtEState them = (AssrtEState) o;
		return super.equals(o) && this.statevars.equals(them.statevars)
				&& this.ass.equals(them.ass);  // Checks canEquals
	}

	@Override
	protected boolean canEquals(MState<?, ?, ?, ?> s)
	{
		return s instanceof AssrtEState;
	}
}

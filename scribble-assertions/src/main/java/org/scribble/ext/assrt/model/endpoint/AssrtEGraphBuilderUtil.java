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

import java.util.Collections;
import java.util.Map;

import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.model.endpoint.EGraphBuilderUtil;
import org.scribble.model.endpoint.EState;

// Helper class for EGraphBuilder -- can access the protected setters of EState (via superclass helper methods)
// Tailored to support graph building from syntactic local protocol choice and recursion
public class AssrtEGraphBuilderUtil extends EGraphBuilderUtil
{
	public AssrtEGraphBuilderUtil(AssrtEModelFactory ef)
	{
		super(ef);
	}
	
	@Override
	public void init(EState init)
	{
		clear();  // Duplicated from super
		reset(//(AssrtEState)
				init, ((AssrtEModelFactory) this.ef).newAssrtEState(Collections.emptySet(), Collections.emptyMap()));
	}
	
	public void addAnnotVarInits(Map<AssrtDataTypeVar, AssrtArithFormula> vars)
	{
		((AssrtEState) this.entry).addAnnotVarInits(vars);
	}
	
	@Override
	public AssrtEState getEntry()
	{
		return (AssrtEState) super.getEntry();
	}
	
	@Override
	public AssrtEState getExit()
	{
		return (AssrtEState) super.getExit();
	}
}

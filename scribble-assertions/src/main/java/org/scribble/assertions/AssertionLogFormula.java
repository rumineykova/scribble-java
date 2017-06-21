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
package org.scribble.assertions;

import java.util.Collections;
import java.util.Set;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;

public class AssertionLogFormula extends StmFormula {
	
	Set<String> vars; 
	
	
	public AssertionLogFormula(Formula f1, Set<String> vars)
	{
		this.vars = Collections.unmodifiableSet(vars); 	
		this.formula = f1;  
	}
	
	@Override
	protected BooleanFormula toFormula() throws AssertionException {
		return (BooleanFormula) this.formula; 
	}
	
	@Override
	public Set<String> getVars(){
		return vars; 
	}
	
	public AssertionLogFormula addFormula(StmFormula newFormula) throws AssertionException{		
		return this.formula==null? 
				new AssertionLogFormula(newFormula.formula, newFormula.getVars()):	
				SMTWrapper.getInstance().addFormula(this, newFormula);
	}
}

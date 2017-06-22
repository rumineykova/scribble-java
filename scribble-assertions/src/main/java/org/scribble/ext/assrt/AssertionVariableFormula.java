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
package org.scribble.ext.assrt;

import java.util.HashSet;
import java.util.Set;

import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

public class AssertionVariableFormula extends StmFormula {

	private String name; 
	public AssertionVariableFormula(String name){
		this.name = name; 
	}
	
	@Override
	public String toString()
	{
		return this.name; 
	}
	
	@Override
	public IntegerFormula toFormula() {
		IntegerFormulaManager fmanager = SMTWrapper.getInstance().imanager;
		return fmanager.makeVariable(this.name);   
	}
	
	@Override
	public Set<String> getVars(){
		Set<String> vars = new HashSet<String>();
		vars.add(this.name); 
		return vars; 
	}
}

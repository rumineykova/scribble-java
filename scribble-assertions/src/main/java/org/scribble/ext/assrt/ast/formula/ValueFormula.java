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
package org.scribble.ext.assrt.ast.formula;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ext.assrt.util.SMTWrapper;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

public class ValueFormula extends SmtFormula {

	private Integer value; 
	public ValueFormula(String value){
		this.value = Integer.parseInt(value); 
	}
	
	@Override
	public String toString()
	{
		return this.value.toString(); 
	}
	
	@Override
	public IntegerFormula toFormula() {
		IntegerFormulaManager fmanager = SMTWrapper.getInstance().imanager;
		return fmanager.makeNumber(this.value);  
	}
	
	@Override
	public Set<String> getVars(){
		Set<String> vars = new HashSet<String>(); 
		return vars; 
	}
}

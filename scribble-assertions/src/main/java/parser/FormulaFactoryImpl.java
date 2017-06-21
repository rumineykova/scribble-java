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
package parser;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.assertions.BoolFormula;
import org.scribble.assertions.CompFormula;
import org.scribble.assertions.StmFormula;
import org.scribble.assertions.ArithFormula;
import org.scribble.assertions.ValueFormula;
import org.scribble.assertions.VariableFormula;


public class FormulaFactoryImpl {

	public static BoolFormula parseBoolFormula(
			AssertionsScribParser assertionsScribParser, CommonTree ct) {
		return null;
	}

	public static ValueFormula parseValue(CommonTree ct, String text) {
		return new ValueFormula(text);
	}

	public static VariableFormula parseVariable(CommonTree ct, String text) {
		return new VariableFormula(text);
	}

	public static CompFormula CompFormula(String op, StmFormula left, StmFormula right) {
			return new CompFormula(op, left, right); 
	}
	
	public static BoolFormula BoolFormula(String op, StmFormula left, StmFormula right) {
		return new BoolFormula(op, left, right); 
	}
	
	public static ArithFormula ArithFormula(String op, StmFormula left, StmFormula right) {
		return new ArithFormula(op, left, right); 
	}
	
}

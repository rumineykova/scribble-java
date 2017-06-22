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
package org.scribble.ext.assrt.parser.assertions.ast.formula;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.ast.formula.CompFormula;
import org.scribble.ext.assrt.ast.formula.StmFormula;
import org.scribble.ext.assrt.parser.assertions.AssertionsParseException;
import org.scribble.ext.assrt.parser.assertions.AssertionsScribParser;

public class CompFormulaNode implements FormulaNode {

	private static Integer CHILD_OP_INDEX = 1; 
	private static Integer CHILD_LEFT_FORMULA_INDEX = 0;
	private static Integer CHILD_RIGHT_FORMULA_INDEX = 2;
	
	public static CompFormula parseCompFormula(
			AssertionsScribParser parser, CommonTree ct) throws AssertionsParseException {
	 
		String op = ct.getChild(CHILD_OP_INDEX).getText(); 
		StmFormula left = parser.parse((CommonTree)ct.getChild(CHILD_LEFT_FORMULA_INDEX)); 
		StmFormula right = parser.parse((CommonTree)ct.getChild(CHILD_RIGHT_FORMULA_INDEX));
		
		return FormulaFactoryImpl.CompFormula(op, left, right); 
		
	}

}
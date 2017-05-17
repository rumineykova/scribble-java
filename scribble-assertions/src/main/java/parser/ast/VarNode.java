package parser.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.assertions.VariableFormula;

import parser.AssertionsScribParser;
import parser.FormulaFactoryImpl;

public class VarNode implements FormulaNode {

	public static VariableFormula parseVarFormula(
			AssertionsScribParser assertionsScribParser, CommonTree ct) {
		
		return FormulaFactoryImpl.parseVariable(ct, ct.getChild(0).getText());
	}

}

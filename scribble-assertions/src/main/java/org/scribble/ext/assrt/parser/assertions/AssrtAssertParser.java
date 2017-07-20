package org.scribble.ext.assrt.parser.assertions;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrConstants.AssrtAntlrNodeType;
import org.scribble.ext.assrt.parser.assertions.ast.formula.AntlrArithFormulaNode;
import org.scribble.ext.assrt.parser.assertions.ast.formula.AntlrBoolFormulaNode;
import org.scribble.ext.assrt.parser.assertions.ast.formula.AntlrCompFormulaNode;
import org.scribble.ext.assrt.parser.assertions.ast.formula.AssrtFormulaFactoryImpl;
import org.scribble.ext.assrt.sesstype.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.parser.assertions.ast.formula.AntlrValueNode;
import org.scribble.ext.assrt.parser.assertions.ast.formula.AntlrVarNode;
import org.scribble.parser.scribble.ScribParser;

public class AssrtAssertParser  // Cf. ScribParser
{
	private static AssrtAssertParser instance = null;

	protected AssrtAssertParser()
	{
		
	}

	public static AssrtAssertParser getInstance()
	{
		if (AssrtAssertParser.instance == null)
		{
			AssrtAssertParser.instance = new AssrtAssertParser();
		}
		return AssrtAssertParser.instance;
	}

	// ct should be the first child of the root node of the assertion subtree (i.e., AssrtAntlrGMessageTransfer.getAssertionChild.getChild(0))
	public AssrtSmtFormula<?> parse(CommonTree ct) //throws AssertionsParseException
	{
		ScribParser.checkForAntlrErrors(ct);
		
		AssrtAntlrNodeType type = AssrtParserUtil.getAntlrNodeType(ct);
		switch (type)
		{
			case BEXPR: return AntlrBoolFormulaNode.parseBoolFormula(this, ct);
			case CEXPR: return AntlrCompFormulaNode.parseCompFormula(this, ct);
			case AEXPR: return AntlrArithFormulaNode.parseArithFormula(this, ct);
			case VAR:   return AntlrVarNode.parseVarFormula(this, ct);
			case VALUE: return AntlrValueNode.parseValueFormula(this, ct);
			
			case FALSE: return AssrtFormulaFactoryImpl.AssrtFalseFormula();
			case TRUE:  return AssrtFormulaFactoryImpl.AssrtTrueFormula();
			
			default:    throw new RuntimeException("Unknown ANTLR node type: " + type);
		}
	}
}

package org.scribble.ext.assrt.parser.assertions;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrConstants.AssrtAntlrNodeType;
import org.scribble.ext.assrt.parser.assertions.formula.AntlrArithFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AntlrBoolFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AntlrCompFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AntlrIntValFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AntlrIntVarFormula;
import org.scribble.ext.assrt.sesstype.formula.AssrtFalseFormula;
import org.scribble.ext.assrt.sesstype.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.sesstype.formula.AssrtTrueFormula;
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
			case BEXPR: return AntlrBoolFormula.parseBoolFormula(this, ct);
			case CEXPR: return AntlrCompFormula.parseCompFormula(this, ct);
			case AEXPR: return AntlrArithFormula.parseArithFormula(this, ct);
			case VAR:   return AntlrIntVarFormula.parseVarFormula(this, ct);
			case VALUE: return AntlrIntValFormula.parseValueFormula(this, ct);
			
			case FALSE: return AssrtFalseFormula.FALSE; //AssrtFormulaFactory.AssrtFalseFormula();
			case TRUE:  return AssrtTrueFormula.TRUE; //AssrtFormulaFactory.AssrtTrueFormula();
			
			default:    throw new RuntimeException("Unknown ANTLR node type: " + type);
		}
	}
}

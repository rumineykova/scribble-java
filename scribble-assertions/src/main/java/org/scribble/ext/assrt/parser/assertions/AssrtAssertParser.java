package org.scribble.ext.assrt.parser.assertions;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAntlrConstants.AssrtAntlrNodeType;
import org.scribble.ext.assrt.parser.assertions.formula.AntlrBinArithFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AntlrBinBoolFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AntlrBinCompFormula;
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
			case BEXPR: return AntlrBinBoolFormula.parseBinBoolFormula(this, ct);
			case CEXPR: return AntlrBinCompFormula.parseBinCompFormula(this, ct);
			case AEXPR: return AntlrBinArithFormula.parseBinArithFormula(this, ct);
			case VAR:   return AntlrIntVarFormula.parseVarFormula(this, ct);
			case VALUE: return AntlrIntValFormula.parseValueFormula(this, ct);
			case FALSE: return AssrtFalseFormula.FALSE;
			case TRUE:  return AssrtTrueFormula.TRUE;
			default:    throw new RuntimeException("[assrt] Unknown ANTLR node type: " + type);
		}
	}
}

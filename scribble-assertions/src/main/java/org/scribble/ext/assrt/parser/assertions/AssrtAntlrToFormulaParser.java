package org.scribble.ext.assrt.parser.assertions;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertionsAntlrConstants.AssrtAntlrNodeType;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrBinArithFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrBinBoolFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrBinCompFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrIntValFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrIntVarFormula;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrUnPredicateFormula;
import org.scribble.ext.assrt.type.formula.AssrtFalseFormula;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.parser.scribble.AntlrToScribParser;

// Embedded by AssrtAntlrToScribParser
public class AssrtAntlrToFormulaParser
{
	private static AssrtAntlrToFormulaParser instance = null;

	protected AssrtAntlrToFormulaParser()
	{
		
	}

	public static AssrtAntlrToFormulaParser getInstance()
	{
		if (AssrtAntlrToFormulaParser.instance == null)
		{
			AssrtAntlrToFormulaParser.instance = new AssrtAntlrToFormulaParser();
		}
		return AssrtAntlrToFormulaParser.instance;
	}

	// ct should be the first child of the root node of the assertion subtree (i.e., AssrtAntlrGMessageTransfer.getAssertionChild.getChild(0))
	// Does not parse ROOT directly -- AssrtAntlr[...] methods extract the children
	public AssrtSmtFormula<?> parse(CommonTree ct) //throws AssertionsParseException
	{
		AntlrToScribParser.checkForAntlrErrors(ct);
		
		AssrtAntlrNodeType type = AssrtAntlrToFormulaParserUtil.getAntlrNodeType(ct);
		switch (type)
		{
			case BINBOOLEXPR:  return AssrtAntlrBinBoolFormula.parseBinBoolFormula(this, ct);
			case BINCOMPEXPR:  return AssrtAntlrBinCompFormula.parseBinCompFormula(this, ct);
			case BINARITHEXPR: return AssrtAntlrBinArithFormula.parseBinArithFormula(this, ct);
			case UNPRED:       return AssrtAntlrUnPredicateFormula.parseUnPredicate(this, ct);
			case INTVAR:       return AssrtAntlrIntVarFormula.parseIntVarFormula(this, ct);
			case INTVAL:       return AssrtAntlrIntValFormula.parseIntValFormula(this, ct);
			case FALSE:        return AssrtFalseFormula.FALSE;
			case TRUE:         return AssrtTrueFormula.TRUE;
			default:           throw new RuntimeException("[assrt] Unexpected ANTLR node type: " + type);
		}
	}
}

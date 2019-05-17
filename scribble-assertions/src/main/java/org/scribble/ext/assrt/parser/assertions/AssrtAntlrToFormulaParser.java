package org.scribble.ext.assrt.parser.assertions;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ext.assrt.core.type.formula.AssrtFalseFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.parser.assertions.AssrtAssertionsAntlrConstants.AssrtAntlrNodeType;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrArithExpr;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrBoolExpr;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrCompExpr;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrIntVal;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrIntVar;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrNegExpr;
import org.scribble.ext.assrt.parser.assertions.formula.AssrtAntlrUnFun;
import org.scribble.parser.ScribAntlrWrapper;

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

	// Parses directly to "types" (not ast)
	// ct should be the first child of the root node of the assertion subtree (i.e., AssrtAntlrGMessageTransfer.getAssertionChild.getChild(0))
	// Does not parse ROOT directly -- AssrtAntlr[...] methods extract the children
	public AssrtSmtFormula<?> parse(CommonTree ct) //throws AssertionsParseException
	{
		ScribAntlrWrapper.checkForAntlrErrors(ct);
		
		AssrtAntlrNodeType type = AssrtAntlrToFormulaParserUtil.getAntlrNodeType(ct);
		switch (type)
		{
			case BOOLEXPR:  return AssrtAntlrBoolExpr.parseBoolExpr(this, ct);
			case COMPEXPR:  return AssrtAntlrCompExpr.parseCompExpr(this, ct);
			case ARITHEXPR: return AssrtAntlrArithExpr.parseArithExpr(this, ct);
			case NEGEXPR:   return AssrtAntlrNegExpr.parseNegExpr(this, ct);
			case UNFUN:     return AssrtAntlrUnFun.parseUnFun(this, ct);
			case INTVAR:    return AssrtAntlrIntVar.parseIntVar(this, ct);
			case INTVAL:    return AssrtAntlrIntVal.parseIntVal(this, ct);
			case NEGINTVAL: return AssrtAntlrIntVal.parseNegIntVal(this, ct);
			case FALSE:     return AssrtFalseFormula.FALSE;
			case TRUE:      return AssrtTrueFormula.TRUE;
			default:        throw new RuntimeException("[assrt] Unexpected ANTLR node type: " + type);
		}
	}
}

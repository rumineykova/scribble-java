package org.scribble.ext.assrt.core.type.formula;

import java.util.List;


// Would correspond to a "types factory" -- cf. AST factory
public class AssrtFormulaFactory
{
	/*public static AssrtBoolFormula parseBoolFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		return null;
	}*/
	
	public static AssrtBinBFormula AssrtBinBool(AssrtBinBFormula.Op op, AssrtBFormula left, AssrtBFormula right)
	{
		return new AssrtBinBFormula(op, left, right); 
	}

	public static AssrtBinCompFormula AssrtBinComp(AssrtBinCompFormula.Op op, AssrtAFormula left, AssrtAFormula right)
	{
		return new AssrtBinCompFormula(op, left, right); 
	}
	
	public static AssrtBinAFormula AssrtBinArith(AssrtBinAFormula.Op  op, AssrtAFormula left, AssrtAFormula right)
	{
		return new AssrtBinAFormula(op, left, right); 
	}
	
	public static AssrtNegFormula AssrtNeg(AssrtBFormula expr)
	{
		return new AssrtNegFormula(expr); 
	}

	public static AssrtUnintPredicateFormula AssrtUnPredicate(String name, List<AssrtAFormula> args)
	{
		return new AssrtUnintPredicateFormula(name, args);
	}

	public static AssrtIntValFormula AssrtIntVal(int i)
	{
		return new AssrtIntValFormula(i);
	}

	public static AssrtIntVarFormula AssrtIntVar(String text)
	{
		return new AssrtIntVarFormula(text);
	}
	
	/*public static AssrtTrueFormula AssrtTrueFormula() 
	{
		return AssrtTrueFormula.TRUE;
	}
	
	public static AssrtFalseFormula AssrtFalseFormula() 
	{ 
	
		return AssrtFalseFormula.FALSE;
	}*/
	
	

	// Not (currently) parsed
	
	public static AssrtExistsIntVarsFormula AssrtExistsFormula(List<AssrtIntVarFormula> vars, AssrtBFormula expr)
	{
		return new AssrtExistsIntVarsFormula(vars, expr); 
	}

	public static AssrtForallIntVarsFormula AssrtForallFormula(List<AssrtIntVarFormula> vars, AssrtBFormula expr)
	{
		return new AssrtForallIntVarsFormula(vars, expr); 
	}
}

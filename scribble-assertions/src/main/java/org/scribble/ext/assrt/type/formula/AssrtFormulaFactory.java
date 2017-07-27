package org.scribble.ext.assrt.type.formula;

import java.util.List;


// Would correspond to a "types factory" -- cf. AST factory
public class AssrtFormulaFactory
{
	/*public static AssrtBoolFormula parseBoolFormula(
			AssrtAssertParser assertionsScribParser, CommonTree ct) {
		return null;
	}*/

	public static AssrtIntValFormula AssrtIntVal(String text)
	{
		return new AssrtIntValFormula(text);
	}

	public static AssrtIntVarFormula AssrtIntVar(String text)
	{
		return new AssrtIntVarFormula(text);
	}

	public static AssrtBinCompFormula AssrtBinComp(AssrtBinCompFormula.Op op, AssrtArithFormula left, AssrtArithFormula right)
	{
		return new AssrtBinCompFormula(op, left, right); 
	}
	
	public static AssrtBinArithFormula AssrtBinArith(AssrtBinArithFormula.Op  op, AssrtArithFormula left, AssrtArithFormula right)
	{
		return new AssrtBinArithFormula(op, left, right); 
	}
	
	public static AssrtBinBoolFormula AssrtBinBool(AssrtBinBoolFormula.Op op, AssrtBoolFormula left, AssrtBoolFormula right)
	{
		return new AssrtBinBoolFormula(op, left, right); 
	}
	
	public static AssrtExistsFormula AssrtExistsFormula(List<AssrtIntVarFormula> vars, AssrtBoolFormula expr)
	{
		return new AssrtExistsFormula(vars, expr); 
	}
	
	/*public static AssrtTrueFormula AssrtTrueFormula() 
	{
		return AssrtTrueFormula.TRUE;
	}
	
	public static AssrtFalseFormula AssrtFalseFormula() 
	{ 
	
		return AssrtFalseFormula.FALSE;
	}*/
}

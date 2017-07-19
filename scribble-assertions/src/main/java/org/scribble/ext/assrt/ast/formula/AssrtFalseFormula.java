package org.scribble.ext.assrt.ast.formula;

import java.util.Collections;
import java.util.Set;

import org.scribble.ext.assrt.sesstype.name.AssrtDataTypeVar;
import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;

public class AssrtFalseFormula extends AssrtBoolFormula
{
	
	@Override
	public String toString()
	{
		return "False"; 
	}
	
	@Override
	protected BooleanFormula toJavaSmtFormula() { //throws AssertionParseException {
		return JavaSmtWrapper.getInstance().bfm.makeFalse();
	}
	
	@Override
	public Set<AssrtDataTypeVar> getVars()
	{
		return Collections.emptySet(); 
	}
}

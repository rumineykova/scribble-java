package org.scribble.ext.assrt.ast.formula;

import java.util.Collections;
import java.util.Set;

import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;

// FIXME: declare singleton constant
public class AssrtTrueFormula extends AssrtBoolFormula
{
	
	@Override
	public String toString()
	{
		return "True"; 
	}
	
	@Override
	protected BooleanFormula toJavaSmtFormula() { //throws AssertionParseException {
		return JavaSmtWrapper.getInstance().bfm.makeTrue();
	}
	
	@Override
	public Set<String> getVars(){
		return Collections.emptySet(); 
	}
}

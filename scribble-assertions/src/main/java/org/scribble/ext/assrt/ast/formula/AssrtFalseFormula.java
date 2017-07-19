package org.scribble.ext.assrt.ast.formula;

import java.util.Collections;
import java.util.Set;

import org.scribble.ext.assrt.util.JavaSmtWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;

public class AssrtFalseFormula extends BoolFormula
{
	
	@Override
	public String toString()
	{
		return "False"; 
	}
	
	@Override
	protected BooleanFormula toFormula() throws AssertionParseException {
		return JavaSmtWrapper.getInstance().bmanager.makeFalse();
	}
	
	@Override
	public Set<String> getVars(){
		return Collections.emptySet(); 
	}
}

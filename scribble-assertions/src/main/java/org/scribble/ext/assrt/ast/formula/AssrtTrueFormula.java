package org.scribble.ext.assrt.ast.formula;

import java.util.Collections;
import java.util.Set;

import org.scribble.ext.assrt.util.SMTWrapper;
import org.sosy_lab.java_smt.api.BooleanFormula;

public class AssrtTrueFormula extends SmtFormula
{
	
	@Override
	public String toString()
	{
		return "True"; 
	}
	
	@Override
	protected BooleanFormula toFormula() throws AssertionParseException {
		return SMTWrapper.getInstance().bmanager.makeTrue();
	}
	
	@Override
	public Set<String> getVars(){
		return Collections.emptySet(); 
	}
}

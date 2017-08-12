package org.scribble.ext.assrt.model.endpoint;

import java.util.Collections;
import java.util.Map;

import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;
import org.scribble.model.endpoint.EGraphBuilderUtil;
import org.scribble.model.endpoint.EState;

// Helper class for EGraphBuilder -- can access the protected setters of EState (via superclass helper methods)
// Tailored to support graph building from syntactic local protocol choice and recursion
public class AssrtEGraphBuilderUtil extends EGraphBuilderUtil
{
	public AssrtEGraphBuilderUtil(AssrtEModelFactory ef)
	{
		super(ef);
	}
	
	@Override
	public void init(EState init)
	{
		clear();  // Duplicated from super
		reset(//(AssrtEState)
				init, ((AssrtEModelFactory) this.ef).newAssrtEState(Collections.emptySet(), Collections.emptyMap()));
	}
	
	public void addStateVars(AssrtEState s, Map<AssrtDataTypeVar, AssrtArithFormula> vars)
	{
		//((AssrtEState) this.entry).addAnnotVars(vars);
		s.addStateVars(vars);
	}
	
	@Override
	public AssrtEState getEntry()
	{
		return (AssrtEState) super.getEntry();
	}
	
	@Override
	public AssrtEState getExit()
	{
		return (AssrtEState) super.getExit();
	}
}

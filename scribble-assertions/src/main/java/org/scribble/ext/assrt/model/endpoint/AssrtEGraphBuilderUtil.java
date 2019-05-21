package org.scribble.ext.assrt.model.endpoint;

import java.util.LinkedHashMap;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EGraphBuilderUtil;
import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtDataVar;

// Helper class for EGraphBuilder -- can access the protected setters of EState (via superclass helper methods)
// Tailored to support graph building from syntactic local protocol choice and recursion
public class AssrtEGraphBuilderUtil extends EGraphBuilderUtil
{
	public AssrtEGraphBuilderUtil(ModelFactory mf)
	{
		super(mf);
	}
	
	public void addStateVars(AssrtEState s,
			LinkedHashMap<AssrtDataVar, AssrtAFormula> vars,
			AssrtBFormula ass)
	{
		//((AssrtEState) this.entry).addAnnotVars(vars);
		s.addStateVars(vars, ass);
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


















	
	/*@Override
	public void init(EState init)
	{
		clear();  // Duplicated from super
		reset(//(AssrtEState)
				init, ((AssrtEModelFactory) this.ef).newAssrtEState(Collections.emptySet(), new LinkedHashMap<>(),
						AssrtTrueFormula.TRUE
				));
	}*/
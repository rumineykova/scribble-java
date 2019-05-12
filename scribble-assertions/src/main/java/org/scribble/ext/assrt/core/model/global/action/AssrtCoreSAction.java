package org.scribble.ext.assrt.core.model.global.action;

import java.util.List;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.core.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.model.global.actions.AssrtSAction;

public interface AssrtCoreSAction extends AssrtSAction
{
	List<AssrtArithFormula> getStateExprs();  // Cf. AssrtCoreEAction
	
	default String stateExprsToString()
	{
		List<AssrtArithFormula> exprs = getStateExprs();
		return (exprs.isEmpty() ? "" : "<" + exprs.stream().map(Object::toString).collect(Collectors.joining(", ")) + ">");
	}
}

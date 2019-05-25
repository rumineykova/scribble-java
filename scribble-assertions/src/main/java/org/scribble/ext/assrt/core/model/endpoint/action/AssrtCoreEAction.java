package org.scribble.ext.assrt.core.model.endpoint.action;

import java.util.List;
import java.util.stream.Collectors;

import org.scribble.ext.assrt.core.type.formula.AssrtAFormula;
import org.scribble.ext.assrt.model.endpoint.action.AssrtEAction;

public interface AssrtCoreEAction extends AssrtEAction
{
	//AssrtDataTypeVar DUMMY_VAR = new AssrtDataTypeVar("_dum0");  // for statevars -- cf. actionvars, AssrtCoreGProtocolTranslator::makeFreshDataTypeVar starts from 1

	/*AssrtDataTypeVar getAnnotVar();
	AssrtArithFormula getArithExpr();*/
	List<AssrtAFormula> getStateExprs();  // Any edge may be a continue-edge with state exprs
			// Cf. AssrtStateVarArgAnnotNode::getAnnotExprs
	
	default String stateExprsToString()
	{
		List<AssrtAFormula> aforms = getStateExprs();
		return aforms.isEmpty() 
				? "" 
				: ("<" + aforms.stream().map(Object::toString)
						.collect(Collectors.joining(", ")) + ">");
	}
}

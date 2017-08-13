package org.scribble.ext.assrt.core.model.endpoint.action;

import java.util.List;

import org.scribble.ext.assrt.model.endpoint.action.AssrtEAction;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;

public interface AssrtCoreEAction extends AssrtEAction
{
	//AssrtDataTypeVar DUMMY_VAR = new AssrtDataTypeVar("_dum0");  // for statevars -- cf. actionvars, AssrtCoreGProtocolTranslator::makeFreshDataTypeVar starts from 1

	/*AssrtDataTypeVar getAnnotVar();
	AssrtArithFormula getArithExpr();*/
	List<AssrtArithFormula> getStateExprs();  // Cf. AssrtStateVarArgAnnotNode::getAnnotExprs
}

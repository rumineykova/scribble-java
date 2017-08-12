package org.scribble.ext.assrt.core.model.endpoint.action;

import java.util.List;

import org.scribble.ext.assrt.model.endpoint.action.AssrtEAction;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;

public interface AssrtCoreEAction extends AssrtEAction
{
	AssrtDataTypeVar DUMMY_VAR = new AssrtDataTypeVar("_dum0");  // cf. AssrtCoreGProtocolTranslator::makeFreshDataTypeVar starts from 1

	/*AssrtDataTypeVar getAnnotVar();
	AssrtArithFormula getArithExpr();*/
	List<AssrtArithFormula> getStateExprs();
}

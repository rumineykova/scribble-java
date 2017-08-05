package org.scribble.ext.assrt.core.model.endpoint.action;

import org.scribble.ext.assrt.model.endpoint.action.AssrtEAction;
import org.scribble.ext.assrt.type.formula.AssrtArithFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;

public interface AssrtCoreEAction extends AssrtEAction
{
	AssrtDataTypeVar getAnnotVar();
	AssrtArithFormula getArithExpr();
}

package org.scribble.ext.assrt.core.model.stp.action;

import java.util.Map;

import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAction;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.type.name.AssrtDataTypeVar;

public interface AssrtStpEAction extends AssrtCoreEAction
{
	Map<AssrtDataTypeVar, AssrtSmtFormula<?>> getSigma();
}

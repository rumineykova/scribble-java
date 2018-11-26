package org.scribble.ext.assrt.core.model.stp.action;

import java.util.Map;

import org.scribble.ext.assrt.core.model.endpoint.action.AssrtCoreEAction;
import org.scribble.ext.assrt.type.formula.AssrtIntVarFormula;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;

public interface AssrtStpEAction extends AssrtCoreEAction
{
	Map<AssrtIntVarFormula, AssrtSmtFormula<?>> getSigma();
}

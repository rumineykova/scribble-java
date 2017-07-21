package org.scribble.ext.assrt.model.global;

import org.scribble.model.global.SGraph;
import org.scribble.model.global.SModel;
import org.scribble.model.global.SStateErrors;

public class AssrtSModel extends SModel
{
	protected AssrtSModel(SGraph graph)
	{
		super(graph);
	}

	@Override
	protected String appendSafetyErrorMessages(String errorMsg, SStateErrors errors)
	{
		AssrtSStateErrors errs = (AssrtSStateErrors) errors;
		errorMsg = super.appendSafetyErrorMessages(errorMsg, errs);
		if (!errs.varsNotInScope.isEmpty())
		{
			errorMsg += "\n    Assertion variables are not in scope: " + errs.varsNotInScope;
		}
		if (!errs.unsatAssertions.isEmpty())
		{
			errorMsg += "\n    Unsatisfiable constraints: " + errs.unsatAssertions;
		}
		return errorMsg;
	}
}

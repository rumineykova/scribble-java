/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
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
			errorMsg += "\n    Assertion variables are not in scope " + errs.varsNotInScope;
		}
		if (!errs.unsatAssertions.isEmpty())
		{
			errorMsg += "\n    Unsatisfiable constraints " + errs.unsatAssertions;
		}
		return errorMsg;
	}
}

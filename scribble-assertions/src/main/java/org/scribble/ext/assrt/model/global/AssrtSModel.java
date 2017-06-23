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
import org.scribble.model.global.SState;

public class AssrtSModel extends SModel
{
	protected AssrtSModel(SGraph graph)
	{
		super(graph);
	}

	@Override
	protected String appendSafetyErrorMessages(String errorMsg, SState init, SState s)
	{
		AssrtSStateErrors errors = (AssrtSStateErrors) s.getErrors();
		super.appendSafetyErrorMessages(errorMsg, init, s);
		if (!errors.varsNotInScope.isEmpty())
		{
			errorMsg += "\n    Assertion variables are not in scope " + errors.varsNotInScope;
		}
		if (!errors.unsatAssertions.isEmpty())
		{
			errorMsg += "\n    Unsatisfiable constraints " + errors.unsatAssertions;
		}
		return errorMsg;
	}
}

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
package org.scribble.core.job;

// CHECKME: deprecate?
public enum CoreFlags
{
	VERBOSE,
	FAIR,
	SPIN,
	NO_VALIDATION,
	NO_PROGRESS,  // TODO: deprecate
	MIN_EFSM,  // Currently only affects EFSM output (i.e. -fsm, -dot) and API gen -- doesn't affect model checking

	OLD_WF,  // TODO: deprecate
	NO_LCHOICE_SUBJ_CHECK,  // For debugging only?
	NO_ACC_CORRELATION_CHECK,  // Currently unused
	;
}

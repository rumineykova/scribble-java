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

import java.util.Set;

public class CoreArgs
{
	public final boolean VERBOSE;
	public final boolean FAIR;
	public final boolean SPIN;
	public final boolean NO_VALIDATION;
	public final boolean NO_PROGRESS;  // TODO: deprecate
	public final boolean MIN_EFSM;  // Currently only affects EFSM output (i.e. -fsm, -dot) and API gen -- doesn't affect model checking

	public final boolean OLD_WF;  // TODO: deprecate
	public final boolean NO_LCHOICE_SUBJ_CHECK;  // For debugging only?
	public final boolean NO_ACC_CORRELATION_CHECK;  // Currently unused
	
	public CoreArgs(Set<CoreFlags> flags)  // CHECKME: deprecate CoreFlags?
	{
		this.VERBOSE = flags.contains(CoreFlags.VERBOSE);
		this.FAIR = flags.contains(CoreFlags.FAIR);
		this.SPIN = flags.contains(CoreFlags.SPIN);
		this.NO_VALIDATION = flags.contains(CoreFlags.NO_VALIDATION);
		this.NO_PROGRESS = flags.contains(CoreFlags.NO_PROGRESS);
		this.MIN_EFSM = flags.contains(CoreFlags.MIN_EFSM);
		
		this.OLD_WF = flags.contains(CoreFlags.OLD_WF);
		this.NO_LCHOICE_SUBJ_CHECK = flags
				.contains(CoreFlags.NO_LCHOICE_SUBJ_CHECK);
		this.NO_ACC_CORRELATION_CHECK = flags
				.contains(CoreFlags.NO_ACC_CORRELATION_CHECK);
	}
}

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

import org.scribble.core.model.ModelFactory;
import org.scribble.core.type.name.ModuleName;
import org.scribble.core.type.session.STypeFactory;
import org.scribble.core.visit.STypeVisitorFactory;

// The "static" (constant) info for Jobs -- cf. JobContext "dynamic" state
public class CoreConfig
{
	public final ModuleName main;  // Full name   // CHECKME: refactor as a CoreArg?
	public final CoreArgs args;  // TODO: Rename flags and make Set

	public final STypeFactory tf;
	public final STypeVisitorFactory vf;
	public final ModelFactory mf;
	
	// N.B. MainContext is in a different non-visible (by Maven) package
	public CoreConfig(ModuleName main, CoreArgs args,
			STypeFactory tf, STypeVisitorFactory vf, ModelFactory mf)
	{
		this.main = main;
		this.args = args;
		this.tf = tf;
		this.vf = vf;
		this.mf = mf;
	}
}

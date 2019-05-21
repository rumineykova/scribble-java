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
package org.scribble.ext.assrt.core.job;

import java.util.Set;
import java.util.function.Function;

import org.scribble.core.job.Core;
import org.scribble.core.job.CoreArgs;
import org.scribble.core.job.CoreContext;
import org.scribble.core.lang.global.GProtocol;
import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EModelFactory;
import org.scribble.core.model.global.SModelFactory;
import org.scribble.core.type.name.ModuleName;
import org.scribble.core.type.session.STypeFactory;
import org.scribble.core.visit.STypeVisitorFactory;
import org.scribble.core.visit.STypeVisitorFactoryImpl;
import org.scribble.core.visit.global.GTypeVisitorFactoryImpl;
import org.scribble.core.visit.local.LTypeVisitorFactoryImpl;
import org.scribble.ext.assrt.core.model.endpoint.AssrtCoreEModelFactoryImpl;
import org.scribble.ext.assrt.core.model.global.AssrtCoreSModelFactoryImpl;
import org.scribble.util.ScribException;


// A "compiler job" front-end that supports operations comprising visitor passes over the AST and/or local/global models
public class AssrtCore extends Core
{
	public AssrtCore(ModuleName mainFullname, CoreArgs args, Set<GProtocol> imeds,
			STypeFactory tf)
	{
		super(mainFullname, args, imeds, tf);
	}
	
	// A Scribble extension should override newSTypeVisitorFactory/ModelFactory as appropriate
	@Override
	protected STypeVisitorFactory newSTypeVisitorFactory()
	{
		return new STypeVisitorFactoryImpl(new GTypeVisitorFactoryImpl(),
				new LTypeVisitorFactoryImpl());
	}
	
	// A Scribble extension should override newSTypeVisitorFactory/ModelFactory as appropriate
	@Override
	protected ModelFactory newModelFactory()
	{
		return new ModelFactory(
				(Function<ModelFactory, EModelFactory>) AssrtCoreEModelFactoryImpl::new,  // Explicit cast necessary (CHECKME, why?)
				(Function<ModelFactory, SModelFactory>) AssrtCoreSModelFactoryImpl::new);
	}

	/*// A Scribble extension should override newCoreConfig/Context/etc as appropriate
	@Override
	protected CoreConfig newCoreConfig(ModuleName mainFullname,
			CoreArgs args, STypeFactory tf)
	{
		STypeVisitorFactory vf = newSTypeVisitorFactory();
		ModelFactory mf = newModelFactory();
		return new CoreConfig(mainFullname, args, tf, vf, mf); 
	}*/

	// A Scribble extension should override newCoreConfig/Context/etc as appropriate
	@Override
	protected CoreContext newCoreContext(Set<GProtocol> imeds)
	{
		return new AssrtCoreContext(this, imeds);
	}

	@Override
	public void runPasses() throws ScribException
	{
		
		// TODO: override passes for Assrt as needed
		
		super.runPasses();
	}
}


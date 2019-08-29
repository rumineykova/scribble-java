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
package org.scribble.ext.assrt.job;

import java.util.Map;

import org.scribble.ast.Module;
import org.scribble.core.job.ScribNames;
import org.scribble.core.type.name.ModuleName;
import org.scribble.ext.assrt.ast.AssrtAssertDecl;
import org.scribble.ext.assrt.ast.AssrtModule;
import org.scribble.ext.assrt.core.type.name.AssrtAssertName;
import org.scribble.job.ModuleContextBuilder;
import org.scribble.util.ScribException;

public class AssrtModuleContextBuilder extends ModuleContextBuilder
{

	@Override
	protected void addModule(ScribNames names, Module mod, ModuleName modname)
	{
		super.addModule(names, mod, modname);

		AssrtScribNames ns = (AssrtScribNames) names;
		for (AssrtAssertDecl assd : ((AssrtModule) mod).getAssertDeclChildren())
		{
			AssrtAssertName qualif = new AssrtAssertName(modname, assd.getDeclName());
			ns.asserts.put(qualif, assd.getFullMemberName(mod));
		}
	}

	@Override
	protected void addVisible(Map<ModuleName, Module> parsed, Module root)
			throws ScribException
	{
		super.addVisible(parsed, root);

		AssrtScribNames vs = (AssrtScribNames) this.visible;
		for (AssrtAssertDecl assd : ((AssrtModule) root).getAssertDeclChildren())
		{
			AssrtAssertName visname = new AssrtAssertName(
					assd.getDeclName().toString());
			vs.asserts.put(visname, assd.getFullMemberName(root));
		}
	}
}

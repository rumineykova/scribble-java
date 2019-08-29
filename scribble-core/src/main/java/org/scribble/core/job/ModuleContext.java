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

import java.util.Map;

import org.scribble.core.type.kind.Global;
import org.scribble.core.type.kind.Kind;
import org.scribble.core.type.kind.ProtoKind;
import org.scribble.core.type.kind.SigKind;
import org.scribble.core.type.name.DataName;
import org.scribble.core.type.name.GProtoName;
import org.scribble.core.type.name.LProtoName;
import org.scribble.core.type.name.ModuleName;
import org.scribble.core.type.name.Name;
import org.scribble.core.type.name.ProtoName;
import org.scribble.core.type.name.SigName;

// Context information specific to each module as a root (wrt. to visitor passes)
// CHECKME: not currently used in core -- refactor out to scribble-ast job?
public class ModuleContext
{
	public final ModuleName root;  // full name  // The root Module for this ModuleContext -- cf. separate to the "main" module

  // All transitive name dependencies of this module: all names fully qualified
	// The ScribNames maps are basically just used as sets (identity map)
	// Cf. ProtocolDeclContext protocol dependencies from protocoldecl as root
	protected final ScribNames deps;

	// The modules and member names that are visible from this Module -- mapped to "cannonical" (fully qualified) names
	// visible names -> fully qualified names
  // Directly visible names from this module
	protected final ScribNames visible;

	// Made by ModuleContextBuilder
	// ModuleContext is the root context
	public ModuleContext(ModuleName root, ScribNames deps, ScribNames visible)
	{
		this.root = root;
		this.deps = deps;
		this.visible = visible;
	}

	/*public boolean isDataTypeDependency(DataType typename)
	{
		return this.deps.data.keySet().contains(typename);
	}

	public boolean isMessageSigNameDependency(Name<? extends SigKind> signame)
	{
		return this.deps.sigs.containsKey(signame);
	}*/

	// TODO: deprecate -- now redundant: proto should already be full name by namedisamb (and this.deps only stores full names)
	// Refactored as a "check" for now (although still redundant, not actually checking anything)
	public <K extends ProtoKind> ProtoName<K> checkProtocolDeclDependencyFullName(
			ProtoName<K> proto)
	{
		return getProtoFullName(this.deps, proto);
	}

	public boolean isDataNameVisible(DataName data)
	{
		return this.visible.data.containsKey(data);
	}

	public boolean isSigNameVisible(Name<? extends SigKind> sig)
	{
		return this.visible.sigs.containsKey(sig);
	}
	
	public <K extends ProtoKind> boolean isProtoNameVisible(
			ProtoName<K> proto)
	{
		return this.visible.isVisibleProtoName(proto);
	}
	
	public DataName getVisDataFullName(DataName data)
	{
		return getFullName(this.visible.data, data);
	}

	public SigName getVisSigNameFullName(SigName sig)
	{
		return getFullName(this.visible.sigs, sig);
	}
	
	public <K extends ProtoKind> ProtoName<K> getVisProtoFullName(
			ProtoName<K> proto)
	{
		return getProtoFullName(this.visible, proto);
	}

	public static <K extends ProtoKind> ProtoName<K> getProtoFullName(
			ScribNames names, ProtoName<K> proto)
	{
		ProtoName<? extends ProtoKind> pn = (proto.getKind()
				.equals(Global.KIND))
						? getFullName(names.globals, (GProtoName) proto)
						: getFullName(names.locals, (LProtoName) proto);
		@SuppressWarnings("unchecked")
		ProtoName<K> tmp = (ProtoName<K>) pn;
		return tmp;
	}

	private static <T extends Name<K>, K extends Kind> T getFullName(
			Map<T, T> map, T name)
	{
		if (!map.containsKey(name))
		{
			// FIXME: runtime exception bad -- make a guard method
			throw new RuntimeException("Unknown name: " + name);
		}
		return map.get(name);
	}

	@Override 
	public String toString()
	{
		return "[deps=" + this.deps + ", visible=" + this.visible + "]";
	}
}	

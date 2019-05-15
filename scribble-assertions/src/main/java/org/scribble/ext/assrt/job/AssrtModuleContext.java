package org.scribble.ext.assrt.job;

import org.scribble.core.job.ModuleContext;
import org.scribble.core.type.name.ModuleName;

// Context information specific to each module as a root (wrt. to visitor passes)
@Deprecated
public class AssrtModuleContext extends ModuleContext
{
	public AssrtModuleContext(ModuleName root, AssrtScribNames deps,
			AssrtScribNames visible)
	{
		super(root, deps, visible);
	}
}	

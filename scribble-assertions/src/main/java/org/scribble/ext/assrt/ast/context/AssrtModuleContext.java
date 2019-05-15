package org.scribble.ext.assrt.ast.context;

import org.scribble.ast.Module;
import org.scribble.core.lang.context.ModuleContext;
import org.scribble.core.lang.context.ScribNames;
import org.scribble.core.type.name.ModuleName;
import org.scribble.ext.assrt.ast.AssrtAssertDecl;
import org.scribble.ext.assrt.ast.AssrtModule;
import org.scribble.ext.assrt.core.type.name.AssrtAssertName;
import org.scribble.job.JobContext;
import org.scribble.util.ScribException;

// Context information specific to each module as a root (wrt. to visitor passes)
public class AssrtModuleContext extends ModuleContext
{
	public AssrtModuleContext(ModuleName root, AssrtScribNames deps,
			AssrtScribNames visible)
	{
		super(root, deps, visible);
	}

	@Override
	protected void addModule(ScribNames names, Module mod, ModuleName modname)
			throws ScribException
	{
		super.addModule(names, mod, modname);
		AssrtScribNames ns = (AssrtScribNames) names;
		for (AssrtAssertDecl ad : ((AssrtModule) mod).getAssertDeclChildren())
		{
			AssrtAssertName qualif = new AssrtAssertName(modname, ad.getDeclName());
			ns.asserts.put(qualif, ad.getFullMemberName(mod));
		}
	}

	@Override
	protected void addVisible(JobContext jcontext, Module root)
			throws ScribException
	{
		super.addVisible(jcontext, root);
		AssrtScribNames vs = (AssrtScribNames) this.visible;
		for (AssrtAssertDecl gpd : ((AssrtModule) root).getAssertDeclChildren())
		{
			AssrtAssertName visname = new AssrtAssertName(
					gpd.getDeclName().toString());
			vs.asserts.put(visname, gpd.getFullMemberName(root));
		}
	}
}	

package org.scribble.ext.assrt.ast.context;

import org.scribble.ast.Module;
import org.scribble.ast.context.ModuleContext;
import org.scribble.ast.context.ScribNameMap;
import org.scribble.ext.assrt.ast.AssrtAssertDecl;
import org.scribble.ext.assrt.ast.AssrtModule;
import org.scribble.ext.assrt.type.name.AssrtAssertName;
import org.scribble.main.JobContext;
import org.scribble.main.ScribbleException;
import org.scribble.type.name.ModuleName;

// Context information specific to each module as a root (wrt. to visitor passes)
public class AssrtModuleContext extends ModuleContext
{
	public AssrtModuleContext(JobContext jcontext, Module root, AssrtScribNameMap deps, AssrtScribNameMap visible) throws ScribbleException
	{
		super(jcontext, root, deps, visible);
	}

	@Override
	protected void addModule(ScribNameMap names, Module mod, ModuleName modname) throws ScribbleException
	{
		super.addModule(names, mod, modname);
		AssrtScribNameMap ns = (AssrtScribNameMap) names;
		for (AssrtAssertDecl ad : ((AssrtModule) mod).getAssertDecls())
		{
			AssrtAssertName qualif = new AssrtAssertName(modname, ad.getDeclName());
			ns.asserts.put(qualif, ad.getFullMemberName(mod));
		}
	}

	@Override
	protected void addVisible(JobContext jcontext, Module root) throws ScribbleException
	{
		super.addVisible(jcontext, root);
		AssrtScribNameMap vs = (AssrtScribNameMap) this.visible;
		for (AssrtAssertDecl gpd : ((AssrtModule) root).getAssertDecls())
		{
			AssrtAssertName visname = new AssrtAssertName(gpd.getDeclName().toString());
			vs.asserts.put(visname, gpd.getFullMemberName(root));
		}
	}
}	

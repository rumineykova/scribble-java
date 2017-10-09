package org.scribble.ext.assrt.del;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ast.Module;
import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ast.local.LProtocolDecl;
import org.scribble.del.ModuleDel;
import org.scribble.ext.assrt.ast.AssrtAssertDecl;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.AssrtModule;
import org.scribble.ext.assrt.ast.context.AssrtModuleContext;
import org.scribble.ext.assrt.ast.context.AssrtScribNameMap;
import org.scribble.ext.assrt.type.name.AssrtAssertName;
import org.scribble.main.ScribbleException;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;
import org.scribble.visit.context.ModuleContextBuilder;
import org.scribble.visit.context.Projector;
import org.scribble.visit.wf.NameDisambiguator;

public class AssrtModuleDel extends ModuleDel
{
	
	public AssrtModuleDel()
	{

	}

	@Override
	protected AssrtModuleDel copy()
	{
		return new AssrtModuleDel();
	}

	@Override
	public void enterModuleContextBuilding(ScribNode parent, ScribNode child, ModuleContextBuilder builder) throws ScribbleException
	{
		builder.setModuleContext(new AssrtModuleContext(builder.job.getContext(), (Module) child, new AssrtScribNameMap(), new AssrtScribNameMap()));
	}
		
	@Override
	public Module leaveDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb, ScribNode visited) throws ScribbleException
	{
		AssrtModule mod = (AssrtModule) super.leaveDisambiguation(parent, child, disamb, visited);

		List<AssrtAssertDecl> npds = mod.getAssertDecls();
		List<AssrtAssertName> npdnames = npds.stream().map(npd -> npd.getDeclName()).collect(Collectors.toList()); 
		if (npdnames.size() != npdnames.stream().distinct().count())
		{
			Set<AssrtAssertName> dups = npdnames.stream().filter(n -> npdnames.stream().filter(m -> m.equals(n)).count() > 1).collect(Collectors.toSet());
			AssrtAssertName first = dups.iterator().next();
			throw new ScribbleException(mod.getAssertDecl(first).getSource(), "Duplicate assert decls: " + first);
		}
		
		return mod;
	}

	@Override
	public Module createModuleForProjection(Projector proj, Module root, GProtocolDecl gpd, LProtocolDecl lpd, Map<GProtocolName, Set<Role>> deps)
	{
		Module sup = super.createModuleForProjection(proj, root, gpd, lpd, deps);
		List<AssrtAssertDecl> ads = new LinkedList<>(((AssrtModule) root).getAssertDecls());  // FIXME: copy?  // FIXME: only project the dependencies
		return ((AssrtAstFactory) proj.job.af).AssrtModule(sup.getSource(), sup.moddecl, sup.imports, sup.data, sup.protos, ads);
	}
}

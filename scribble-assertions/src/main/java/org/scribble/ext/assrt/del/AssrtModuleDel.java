package org.scribble.ext.assrt.del;

import org.scribble.del.ModuleDel;

public class AssrtModuleDel extends ModuleDel
{
	public AssrtModuleDel()
	{

	}
		
	/*
	@Override
	public Module leaveDisambiguation(ScribNode parent, ScribNode child,
			NameDisambiguator disamb, ScribNode visited) throws ScribException
	{
		AssrtModule mod = (AssrtModule) super.leaveDisambiguation(parent, child, disamb, visited);
	
		List<AssrtAssertDecl> npds = mod.getAssertDecls();
		List<AssrtAssertName> npdnames = npds.stream().map(npd -> npd.getDeclName())
				.collect(Collectors.toList());
		if (npdnames.size() != npdnames.stream().distinct().count())
		{
			Set<AssrtAssertName> dups = npdnames.stream()
					.filter(n -> npdnames.stream().filter(m -> m.equals(n)).count() > 1)
					.collect(Collectors.toSet());
			AssrtAssertName first = dups.iterator().next();
			throw new ScribException(mod.getAssertDeclChild(first).getSource(),
					"Duplicate assert decls: " + first);
		}
		
		return mod;
	}
	
	@Override
	public Module createModuleForProjection(Projector proj, Module root,
			GProtoDecl gpd, LProtoDecl lpd, Map<GProtoName, Set<Role>> deps)
	{
		Module sup = super.createModuleForProjection(proj, root, gpd, lpd, deps);
		List<AssrtAssertDecl> ads = new LinkedList<>(((AssrtModule) root).getAssertDecls());  // FIXME: copy?  // FIXME: only project the dependencies
		return ((AssrtAstFactory) proj.job.af).AssrtModule(sup.getSource(), sup.moddecl, sup.imports, sup.data, sup.protos, ads);
	}
	//*/
}











	/*@Override
	protected AssrtModuleDel copy()
	{
		return new AssrtModuleDel();
	}

	@Override
	public void enterModuleContextBuilding(ScribNode parent, ScribNode child, AssrtModuleContextBuilder builder) throws ScribException
	{
		builder.setModuleContext(new AssrtModuleContext(builder.job.getContext(), (Module) child, new AssrtScribNames(), new AssrtScribNames()));
	}
	//*/

package org.scribble.ext.assrt.model.global;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.model.endpoint.EFSM;
import org.scribble.model.endpoint.EGraph;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.global.SBuffers;
import org.scribble.model.global.SConfig;
import org.scribble.model.global.SGraphBuilderUtil;
import org.scribble.model.global.SModelFactory;
import org.scribble.type.name.Role;

public class AssrtSGraphBuilderUtil extends SGraphBuilderUtil
{
	protected AssrtSGraphBuilderUtil(SModelFactory sf)
	{
		super(sf);
	}
	
	@Override
	protected SConfig createInitialSConfig(EModelFactory ef, Map<Role, EGraph> egraphs, boolean explicit)
	{
		Map<Role, EFSM> efsms = egraphs.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().toFsm()));
		SBuffers b0 = new SBuffers(ef, efsms.keySet(), !explicit);
		return ((AssrtSModelFactory) this.sf).newAssrtSConfig(efsms, b0, null, new HashMap<Role, Set<String>>());
	}
}

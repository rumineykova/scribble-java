package org.scribble.ext.assrt.model.global;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.core.model.ModelFactory;
import org.scribble.core.model.endpoint.EFsm;
import org.scribble.core.model.endpoint.EGraph;
import org.scribble.core.model.global.SConfig;
import org.scribble.core.model.global.SGraphBuilderUtil;
import org.scribble.core.model.global.SingleBuffers;
import org.scribble.core.type.name.Role;

public class AssrtSGraphBuilderUtil extends SGraphBuilderUtil
{
	protected AssrtSGraphBuilderUtil(ModelFactory mf)
	{
		super(mf);
	}
	
	// TODO: factor out of util, cf. SGraphBuilder.createInitConfig
	protected SConfig createInitConfig(Map<Role, EGraph> egraphs,
			boolean explicit)
	{
		Map<Role, EFsm> efsms = egraphs.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().toFsm()));
		SingleBuffers b0 = new SingleBuffers(efsms.keySet(), !explicit);
		return ((AssrtSModelFactory) this.mf.global).newAssrtSConfig(efsms, b0,
				null, new HashMap<Role, Set<String>>());
	}
}

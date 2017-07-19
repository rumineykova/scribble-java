package org.scribble.ext.assrt.model.global;

import java.util.Map;
import java.util.Set;

import org.scribble.ext.assrt.ast.formula.AssrtLogFormula;
import org.scribble.model.endpoint.EFSM;
import org.scribble.model.global.SBuffers;
import org.scribble.model.global.SConfig;
import org.scribble.model.global.SModelFactory;
import org.scribble.sesstype.name.Role;

public interface AssrtSModelFactory extends SModelFactory
{
	SConfig newAssrtSConfig(Map<Role, EFSM> state, SBuffers buffs, AssrtLogFormula formula, Map<Role, Set<String>> variablesInScope);
}

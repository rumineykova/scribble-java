package org.scribble.ext.assrt.core.ast.global;

import org.scribble.ext.assrt.core.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.ast.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.ast.AssrtCoreType;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.sesstype.name.Role;


public interface AssrtCoreGType extends AssrtCoreType
{
	AssrtCoreLType project(AssrtCoreAstFactory af, Role subj) throws AssrtCoreSyntaxException;
}

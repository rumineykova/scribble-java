package org.scribble.ext.assrt.core.ast.global;

import java.util.List;

import org.scribble.ext.assrt.core.ast.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.ast.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.ast.AssrtCoreType;
import org.scribble.ext.assrt.core.ast.local.AssrtCoreLType;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.name.AssrtAnnotDataType;
import org.scribble.type.name.Role;


public interface AssrtCoreGType extends AssrtCoreType
{
	
	AssrtCoreLType project(AssrtCoreAstFactory af, Role subj, AssrtBoolFormula f) throws AssrtCoreSyntaxException;
	
	List<AssrtAnnotDataType> collectAnnotDataTypes();  // Currently only the vars are needed (not the data types)
}

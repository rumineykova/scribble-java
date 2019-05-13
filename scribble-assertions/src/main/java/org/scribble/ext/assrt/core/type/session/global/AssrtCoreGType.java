package org.scribble.ext.assrt.core.type.session.global;

import java.util.List;

import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.core.type.session.AssrtCoreAstFactory;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.type.session.AssrtCoreType;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLType;


public interface AssrtCoreGType extends AssrtCoreType<Global>
{
	
	// CHECKME: refactor as visitors?

	AssrtCoreLType project(AssrtCoreAstFactory af, Role subj, AssrtBoolFormula f)
			throws AssrtCoreSyntaxException;  // N.B. checking "mergability"
	
	List<AssrtAnnotDataType> collectAnnotDataTypeVarDecls();  // Currently only the vars are needed (not the data types)
}


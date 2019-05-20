package org.scribble.ext.assrt.core.type.session.global;

import java.util.List;

import org.scribble.core.type.kind.Global;
import org.scribble.core.type.name.Role;
import org.scribble.ext.assrt.core.type.formula.AssrtBFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataName;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSTypeFactory;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSyntaxException;
import org.scribble.ext.assrt.core.type.session.AssrtCoreSType;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLType;


public interface AssrtCoreGType extends AssrtCoreSType<Global>
{
	
	// CHECKME: refactor as visitors?

	AssrtCoreLType project(AssrtCoreSTypeFactory af, Role subj, AssrtBFormula f)
			throws AssrtCoreSyntaxException;  // N.B. checking "mergability"
	
	List<AssrtAnnotDataName> collectAnnotDataTypeVarDecls();  // Currently only the vars are needed (not the data types)
}


package org.scribble.ext.assrt.core.type.session;

import java.util.List;

import org.scribble.core.type.name.Op;
import org.scribble.ext.assrt.core.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.core.type.name.AssrtAnnotDataType;
import org.scribble.ext.assrt.core.type.session.global.AssrtCoreGTypeFactory;
import org.scribble.ext.assrt.core.type.session.local.AssrtCoreLTypeFactory;


public class AssrtCoreSTypeFactory
{
	public final AssrtCoreGTypeFactory global;
	public final AssrtCoreLTypeFactory local;
	
	public AssrtCoreSTypeFactory(AssrtCoreGTypeFactory global,
			AssrtCoreLTypeFactory local)
	{
		this.global = global;
		this.local = local;
	}
	
	// Pre: not null
	public AssrtCoreMsg AssrtCoreAction(Op op, List<AssrtAnnotDataType> pays,
			AssrtBoolFormula ass)
	{
		return new AssrtCoreMsg(op, pays, ass);
	}
}
